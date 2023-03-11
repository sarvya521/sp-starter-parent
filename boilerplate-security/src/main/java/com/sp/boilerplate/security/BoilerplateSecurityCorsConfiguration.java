package com.sp.boilerplate.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public class BoilerplateSecurityCorsConfiguration extends UrlBasedCorsConfigurationSource {

  private final List<String> allowedOrigins;

  public BoilerplateSecurityCorsConfiguration(String allowedUrls) {
    allowedOrigins = Optional.ofNullable(allowedUrls)
        .map(urls -> Arrays.stream(urls.split(",")).collect(Collectors.toList())).orElse(
            Collections.emptyList());
  }

  @Override
  public void registerCorsConfiguration(String path, CorsConfiguration configuration) {
    configuration.setAllowedOrigins(allowedOrigins);
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setMaxAge(3600L);
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(
        Arrays.asList("X-Trace-Id", "Accept", "Accept-Language", "Content-Language",
            "Content-Type", "Authorization"));

    super.registerCorsConfiguration(path, configuration);
  }
}
