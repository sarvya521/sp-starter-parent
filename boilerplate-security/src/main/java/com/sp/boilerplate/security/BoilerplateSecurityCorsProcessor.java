package com.sp.boilerplate.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.DefaultCorsProcessor;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Slf4j
public class BoilerplateSecurityCorsProcessor extends DefaultCorsProcessor {

  private List<String> allowedOrigins;

  public BoilerplateSecurityCorsProcessor(String allowedUrls) {
    allowedOrigins = Optional.ofNullable(allowedUrls)
        .map(urls -> Arrays.stream(urls.split(",")).collect(Collectors.toList())).orElse(
            Collections.emptyList());
    log.info("Configured origins {}", allowedOrigins);
  }

  @Override
  public boolean processRequest(CorsConfiguration configuration,
      HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    String origin = request.getHeader("origin");
    boolean hasAllowedOrigin = Optional.ofNullable(origin)
        .map(headerValue -> allowedOrigins.stream()
            .anyMatch(allowedOrigin -> allowedOrigin.equals(headerValue)))
        .orElse(false);

    if (hasAllowedOrigin || allowedOrigins.contains("*")) {
      response.setHeader("Access-Control-Allow-Origin", origin);
    } else {
      log.trace("request url {}", request.getRequestURL());
    }
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    response.setHeader("Access-Control-Max-Age", "3600");
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Headers",
        "Accept,Accept-Language,Content-Language,Content-Type,Access-Control-Allow-Origin");
    return super.processRequest(configuration, request, response);
  }
}
