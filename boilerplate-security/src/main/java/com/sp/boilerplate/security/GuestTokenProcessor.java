package com.sp.boilerplate.security;

import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
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
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Slf4j
@Component
public class GuestTokenProcessor {

  @SuppressWarnings("rawtypes")
  private final ConfigurableJWTProcessor configurableJWTProcessor;
  private final JwtUtil jwtUtil;

  public GuestTokenProcessor(
      @Qualifier("GuestConfigurableJWTProcessor") ConfigurableJWTProcessor configurableJWTProcessor,
      @Qualifier("GuestJwtUtil") JwtUtil jwtUtil) {
    this.configurableJWTProcessor = configurableJWTProcessor;
    this.jwtUtil = jwtUtil;
  }

  private List<String> getUserGroupsFrom(JWTClaimsSet claims) {
    try {
      return JSONObjectUtils.getStringList(claims.getClaims(), "groups");
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  private JWTClaimsSet allowForRefreshToken(String token) throws ParseException {
    String[] parts = token.split("\\.");
    Map<String, Object> payload = JSONObjectUtils.parse(
        new String(Base64Utils.decodeFromString(parts[1])));
    return JWTClaimsSet.parse(payload);
  }

  private JwtAuthentication prepareJwtAuthentication(JWTClaimsSet claims,
      HttpServletRequest request) {
    String userId = jwtUtil.getUserNameFrom(claims);
    MDC.put("guest_user", userId);
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
    JWTClaimsSet claims;
    try {
      claims = configurableJWTProcessor.process(token, null);
    } catch (BadJWTException e) {
      if (e.getMessage().equals("Expired JWT")) {
        log.debug("Expired JWT");
        if (request.getMethod().equalsIgnoreCase("GET")
            && request.getRequestURI().equals("/v1/auth/token")) {
          claims = allowForRefreshToken(token);
          return prepareJwtAuthentication(claims, request);
        }
        throw e;
      }
      throw e;
    }
    jwtUtil.validateIssuer(claims);
    log.debug("guest user token validated");
    return prepareJwtAuthentication(claims, request);
  }
}
