package com.sp.boilerplate.security;

import static com.sp.boilerplate.security.BoilerplateSecurityAuthenticationEntryPoint.ERROR_MESSAGE;

import java.io.IOException;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Slf4j
public class BoilerplateSecurityAuthorizationFilter extends OncePerRequestFilter {

  private final AccessDeniedHandler accessDeniedHandler;

  private final AuthenticationEntryPoint authenticationEntryPoint;

  public BoilerplateSecurityAuthorizationFilter(final AccessDeniedHandler accessDeniedHandler,
      final AuthenticationEntryPoint authenticationEntryPoint) {
    this.accessDeniedHandler = accessDeniedHandler;
    this.authenticationEntryPoint = authenticationEntryPoint;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filters)
      throws ServletException, IOException {
    if (Objects.isNull(AuthUtil.getAuthentication())) {
      authenticationEntryPoint.commence(request, response,
          new AuthenticationServiceException(ERROR_MESSAGE));
      return;
    }
    log.debug("authorizing user access");
    try {
      Authorizer.doAuthorize(request, AuthUtil.getLoggedInUser());
    } catch (AccessDeniedException e) {
      accessDeniedHandler.handle(request, response, e);
      return;
    }
    filters.doFilter(request, response);
  }
}