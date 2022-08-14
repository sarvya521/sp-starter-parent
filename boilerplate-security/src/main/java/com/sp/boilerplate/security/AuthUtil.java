package com.sp.boilerplate.security;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Objects;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public final class AuthUtil {

    public static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            throw new AuthenticationServiceException("No Authentication object found in SecurityContext");
        }
        return authentication;
    }

    public static User getLoggedInUser() {
        JwtAuthentication authentication = (JwtAuthentication) getAuthentication();
        return (User)authentication.getPrincipal();
    }

    private AuthUtil() {
        throw new AssertionError();
    }
}
