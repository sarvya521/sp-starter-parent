package com.sp.boilerplate.security;

import static com.sp.boilerplate.security.BoilerplateSecurityAuthenticationEntryPoint.ERROR_MESSAGE;

import com.sp.boilerplate.commons.exception.BoilerplateException;
import java.util.Objects;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public class BoilerplateSecurityAuthenticationFilter extends OncePerRequestFilter {

  private static final String COGNITO_AUTH_HEADER = "x-aws-cognito-auth";
  private static final String MS_AUTH_HEADER = "x-ms-auth";
  private static final String GUEST_AUTH_HEADER = "x-guest-auth";
  private static final Set<String> BOOLEAN_VALUES = Set.of("true", "yes", "y", "1");

  private final CognitoIdTokenProcessor cognitoIdTokenProcessor;
  private final MicrosoftTokenProcessor microsoftTokenProcessor;
  private final GuestTokenProcessor guestTokenProcessor;

  private final AuthenticationEntryPoint authenticationEntryPoint;

  public BoilerplateSecurityAuthenticationFilter(CognitoIdTokenProcessor cognitoIdTokenProcessor,
      MicrosoftTokenProcessor microsoftTokenProcessor, GuestTokenProcessor guestTokenProcessor,
      AuthenticationEntryPoint authenticationEntryPoint) {
    this.cognitoIdTokenProcessor = cognitoIdTokenProcessor;
    this.microsoftTokenProcessor = microsoftTokenProcessor;
    this.guestTokenProcessor = guestTokenProcessor;
    this.authenticationEntryPoint = authenticationEntryPoint;
  }

  @SneakyThrows
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) {
    Authentication authentication = null;
    if (Objects.isNull(request.getHeader(HttpHeaders.AUTHORIZATION))) {
      authenticationEntryPoint.commence(request, response,
          new AuthenticationServiceException(ERROR_MESSAGE));
      return;
    }
    String cognitoAuthHeader = request.getHeader(COGNITO_AUTH_HEADER);
    String msAuthHeader = request.getHeader(MS_AUTH_HEADER);
    String guestAuthHeader = request.getHeader(GUEST_AUTH_HEADER);

    try {
      if (StringUtils.hasText(cognitoAuthHeader) && BOOLEAN_VALUES.contains(
          cognitoAuthHeader.toLowerCase())) {
        authentication = cognitoIdTokenProcessor.authenticate(request);
      } else if (StringUtils.hasText(msAuthHeader) && BOOLEAN_VALUES.contains(
          msAuthHeader.toLowerCase())) {
        authentication = microsoftTokenProcessor.authenticate(request);
      } else if (StringUtils.hasText(guestAuthHeader) && BOOLEAN_VALUES.contains(
          guestAuthHeader.toLowerCase())) {
        authentication = guestTokenProcessor.authenticate(request);
      }
    } catch (BoilerplateException e) {
      authenticationEntryPoint.commence(request, response,
          new AuthenticationServiceException(e.getMessage(), e));
      return;
    }

    // Get jwt token and validate
    if (Objects.isNull(authentication)) {
      authenticationEntryPoint.commence(request, response,
          new AuthenticationServiceException(ERROR_MESSAGE));
      return;
    }

    SecurityContextHolder.getContext().setAuthentication(authentication);

    chain.doFilter(request, response);
  }
}
