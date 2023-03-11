package com.sp.boilerplate.security;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.util.UriTemplate;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Slf4j
public final class Authorizer {

  public static final Set<String> WHITLISTED_APIS = Set.of(
      "GET-/v1/auth/token" //Refresh user token
  );

  private Authorizer() {
    throw new AssertionError();
  }

  private static boolean isApiUriMatched(@NotNull String resourceEndpoint, @NotNull String apiUri) {
    if (resourceEndpoint.equals(apiUri)) {
      return true;
    }

    long resourceEndpointPaths = apiUri.chars().filter(ch -> ch == '/').count();
    long apiUriPaths = resourceEndpoint.chars().filter(ch -> ch == '/').count();

    if (resourceEndpointPaths != apiUriPaths) {
      return false;
    }

    return !new UriTemplate(resourceEndpoint).match(apiUri).isEmpty();
  }

  private static boolean isWhitelistedApi(@NotNull String apiUri) {
    return WHITLISTED_APIS.stream()
        .anyMatch(s -> isApiUriMatched(s, apiUri));
  }

  @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
  public static void doAuthorize(final HttpServletRequest request,
      final User authenticatedUser) throws AccessDeniedException {
    String resource = request.getRequestURI();
    String method = request.getMethod().toUpperCase();
    log.debug("User {} is trying to access {}-{}",
        authenticatedUser.getUsername(),
        method,
        resource);

    if (isWhitelistedApi(method + "-" + resource)) {
      log.warn("Open access to Whitelisted API {}-{}", method, resource);
      return;
    }

    boolean isUserAuthorized = true;
    List<String> userGroups = authenticatedUser.getAuthorities().stream()
        .map(ga -> (SimpleGrantedAuthority) ga)
        .map(SimpleGrantedAuthority::getAuthority)
        .collect(Collectors.toList());
    final String org = request.getParameter("org");
    if (!userGroups.contains(org)) {
      isUserAuthorized = false;
    }

    if (!isUserAuthorized) {
      log.warn("User {} is not allowed to access the resource {}-{}",
          authenticatedUser.getUsername(),
          method,
          resource);
      throw new AccessDeniedException("Forbidden");
    }
    log.debug("User {} is allowed to access the resource {}-{}",
        authenticatedUser.getUsername(),
        method,
        resource);
  }
}
