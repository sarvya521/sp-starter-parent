package com.sp.boilerplate.security;

import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Slf4j
@Component
public class MicrosoftTokenProcessor {

  @SuppressWarnings("rawtypes")
  private final ConfigurableJWTProcessor configurableJWTProcessor;

  public MicrosoftTokenProcessor(
      @Qualifier("MicrosoftConfigurableJWTProcessor") ConfigurableJWTProcessor configurableJWTProcessor) {
    this.configurableJWTProcessor = configurableJWTProcessor;
  }

  private String getUserNameFrom(JWTClaimsSet claims) {
    return claims.getClaims().get("oid").toString();
  }

  private List<String> getUserGroupsFrom(JWTClaimsSet claims) {
    try {
      return JSONObjectUtils.getStringList(claims.getClaims(), "groups");
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  private JwtAuthentication prepareJwtAuthentication(JWTClaimsSet claims,
      HttpServletRequest request) {
    String userId = getUserNameFrom(claims);
    MDC.put("ms_user", userId);
    List<String> groups = getUserGroupsFrom(claims);
    List<GrantedAuthority> grantedAuthorities = groups.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
    User user = new User(userId, "", grantedAuthorities);
    JwtAuthentication jwtAuthentication = new JwtAuthentication(user, claims, grantedAuthorities);
    jwtAuthentication.setDetails(
        new WebAuthenticationDetailsSource().buildDetails(request)
    );
    return jwtAuthentication;
  }

  public Authentication authenticate(HttpServletRequest request) throws Exception {
    String token = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (!StringUtils.hasText(token)) {
      return null;
    }
    token = token.replaceFirst("Bearer ", "");
    JWTClaimsSet claims = configurableJWTProcessor.process(token, null);
    log.debug("microsoft user token validated");
    return prepareJwtAuthentication(claims, request);
  }
}
