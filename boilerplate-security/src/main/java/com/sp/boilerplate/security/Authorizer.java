package com.sp.boilerplate.security;

import com.sp.boilerplate.commons.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Slf4j
public final class Authorizer {

    private Authorizer() {
        throw new AssertionError();
    }

    @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
    public static void doAuthorize(final HttpServletRequest request,
                                   final User authenticatedUser) throws AccessDeniedException {
        String method = request.getMethod();
        log.debug("User {} is trying to access {}-{}",
                authenticatedUser.getUsername(),
                method,
                request.getRequestURI());
        String resource = parseApiUri(request, getHandlerMethodFrom(request));

        boolean isUserAuthorized = true;
        List<String> userGroups = authenticatedUser.getAuthorities().stream()
                .map(ga -> (SimpleGrantedAuthority) ga)
                .map(SimpleGrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        final String org = request.getParameter("org");
        if(!userGroups.contains(org)) {
            isUserAuthorized = false;
        }
        if (isUserAuthorized && log.isDebugEnabled()) {
            log.debug("User {} is allowed to access the resource {}-{} for org {}",
                    authenticatedUser.getUsername(),
                    method,
                    resource,
                    org);
        }
        if (!isUserAuthorized) {
            log.warn("User {} is not allowed to access the resource {}-{} for org {}",
                    authenticatedUser.getUsername(),
                    method,
                    resource,
                    org);
            throw new AccessDeniedException("Forbidden");
        }
    }

    static HandlerMethod getHandlerMethodFrom(HttpServletRequest request) {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
        RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping) webApplicationContext.getBean("requestMappingHandlerMapping");
        HandlerExecutionChain handlerExecutionChain;
        try {
            handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);
            if (Objects.isNull(handlerExecutionChain)) {
                log.error("HandlerExecutionChain not found for the current request {}-{}", request.getMethod(), request.getRequestURI());
                //throw new Exception();
                return null;
            }
        } catch (Exception e) {
            log.error("404-RequestMapping not found for current request", e);
            throw new ResourceNotFoundException("404-RequestMapping not found for current request");
        }
        return (HandlerMethod) handlerExecutionChain.getHandler();
    }

    static boolean isApiUriMatched(@NotNull String resourceEndpoint, @NotNull String apiUri) {
        if (resourceEndpoint.equals(apiUri)) {
            return true;
        }
        String[] resourceEndpointPaths = resourceEndpoint.split("/");
        String[] apiUriPaths = apiUri.split("/");

        if (resourceEndpointPaths.length != apiUriPaths.length) {
            return false;
        }

        for (int i = 0; i < resourceEndpointPaths.length; i++) {
            if (apiUriPaths[i].startsWith("{") && apiUriPaths[i].endsWith("}")) {
                continue;
            }
            if (!Objects.equals(resourceEndpointPaths[i], apiUriPaths[i])) {
                return false;
            }
        }
        return true;
    }

    private static String parseApiUri(final HttpServletRequest request, final HandlerMethod handlerMethod) {
        if(Objects.isNull(handlerMethod)) {
            log.error("HandlerMethod not found for the current request {}-{}", request.getMethod(), request.getRequestURI());
            return null;
        }
        String resourceEndpoint = request.getRequestURI();
        String[] apiUris = HandlerMappingUriReader.readUris(handlerMethod, HttpMethod.valueOf(request.getMethod()));
        for (String apiUri : apiUris) {
            if (isApiUriMatched(resourceEndpoint, apiUri)) {
                return apiUri;
            }
        }
        return resourceEndpoint;
    }
}
