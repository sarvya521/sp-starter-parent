package com.sp.boilerplate.security;

import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Component
public class CognitoIdTokenProcessor {
    @Autowired
    private CognitoJwtConfigProperties cognitoJwtConfigProperties;

    @SuppressWarnings("rawtypes")
    @Autowired
    private ConfigurableJWTProcessor configurableJWTProcessor;

    public Authentication authenticate(HttpServletRequest request) throws Exception {
        String idToken = request.getHeader(this.cognitoJwtConfigProperties.getHttpHeader());
        if (idToken != null) {
            @SuppressWarnings("unchecked")
            JWTClaimsSet claims = this.configurableJWTProcessor.process(this.getBearerToken(idToken),null);
            validateIssuer(claims);
            verifyIfIdToken(claims);
            String username = getUserNameFrom(claims);
            if (username != null) {
                List<String> groups = getUserGroupsFrom(claims);
                List<GrantedAuthority> grantedAuthorities = groups.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                User user = new User(username, "", grantedAuthorities);
                JwtAuthentication jwtAuthentication = new JwtAuthentication(user, claims, grantedAuthorities);
                jwtAuthentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                return jwtAuthentication;
            }
        }
        return null;
    }

    private String getUserNameFrom(JWTClaimsSet claims) {
        return claims.getClaims().get(this.cognitoJwtConfigProperties.getUserNameField()).toString();
    }

    private List<String> getUserGroupsFrom(JWTClaimsSet claims) {
        try {
            return JSONObjectUtils.getStringList(claims.getClaims(), this.cognitoJwtConfigProperties.getUserGroupsField());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void verifyIfIdToken(JWTClaimsSet claims) throws Exception {
        if (!claims.getIssuer().equals(this.cognitoJwtConfigProperties.getIdentityPoolUrl())) {
            throw new Exception("JWT Token is not an ID Token");
        }
    }

    private void validateIssuer(JWTClaimsSet claims) throws Exception {
        if (!claims.getIssuer().equals(this.cognitoJwtConfigProperties.getIdentityPoolUrl())) {
            throw new Exception(String.format("Issuer %s does not match cognito idp %s", claims.getIssuer(), this.cognitoJwtConfigProperties.getIdentityPoolUrl()));
        }
    }

    private String getBearerToken(String token) {
        return token.startsWith("Bearer ") ? token.substring("Bearer ".length()) : token;
    }
}
