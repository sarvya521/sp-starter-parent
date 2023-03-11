package com.sp.boilerplate.security;

import java.util.Objects;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public final class AuthUtil {

  private AuthUtil() {
    throw new AssertionError();
  }

  public static Authentication getAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (Objects.isNull(authentication)) {
      throw new AuthenticationServiceException("No Authentication object found in SecurityContext");
    }
    return authentication;
  }

  public static User getLoggedInUser() {
    JwtAuthentication authentication = (JwtAuthentication) getAuthentication();
    return (User) authentication.getPrincipal();
  }

  public static String getUsername() {
    return getLoggedInUser().getUsername();
  }
}
