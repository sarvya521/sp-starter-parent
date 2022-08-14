package com.sp.boilerplate.security;

import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

import static com.sp.boilerplate.security.BoilerplateSecurityAuthenticationEntryPoint.ERROR_MESSAGE;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public class BoilerplateSecurityAuthenticationFilter extends OncePerRequestFilter {
    private final CognitoIdTokenProcessor cognitoIdTokenProcessor;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    public BoilerplateSecurityAuthenticationFilter(CognitoIdTokenProcessor cognitoIdTokenProcessor,
                                                   AuthenticationEntryPoint authenticationEntryPoint) {
        this.cognitoIdTokenProcessor = cognitoIdTokenProcessor;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) {
        Authentication authentication = cognitoIdTokenProcessor.authenticate(request);
        // Get jwt token and validate
        if (Objects.isNull(authentication)) {
            authenticationEntryPoint.commence(request, response, new AuthenticationServiceException(ERROR_MESSAGE));
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }
}
