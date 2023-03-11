package com.sp.boilerplate.security;

import static com.nimbusds.jose.JWSAlgorithm.RS256;
import static com.nimbusds.jose.JWSAlgorithm.RS512;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import java.net.URL;
import java.util.Objects;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Slf4j
@Configuration
public class BoilerplateSecurityAutoConfiguration {

  public static final String[] WHITE_LISTED_APIS = {};

  @Autowired
  private Environment env;

  @Autowired
  private Tracer tracer;

  @Bean("AuthObjectMapper")
  public ObjectMapper authObjectMapper() {
    return new ObjectMapper();
  }

  @Bean("BoilerplateSecurityAccessDeniedHandler")
  public AccessDeniedHandler ocAccessDeniedHandler(
      @Qualifier("AuthObjectMapper") ObjectMapper authObjectMapper) {
    log.debug("configuring BoilerplateSecurityAccessDeniedHandler");
    return new BoilerplateSecurityAccessDeniedHandler(authObjectMapper);
  }

  @Bean("BoilerplateSecurityAuthenticationEntryPoint")
  public AuthenticationEntryPoint ocAuthenticationEntryPoint(
      @Qualifier("AuthObjectMapper") ObjectMapper authObjectMapper) {
    log.debug("configuring BoilerplateSecurityAuthenticationEntryPoint");
    return new BoilerplateSecurityAuthenticationEntryPoint(authObjectMapper);
  }

  @Bean("BoilerplateSecurityCorsFilter")
  public CorsFilter corsFilter() {
    log.debug("configuring BoilerplateSecurityCorsFilter");
    String allowedUrls = env.getProperty("boilerplate.cors.allowed-origins");
    Objects.requireNonNull(allowedUrls,
        "property {boilerplate.cors.allowed-origins} is not configured");

    final CorsFilter corsFilter = new CorsFilter(corsConfigurationSource());
    corsFilter.setCorsProcessor(new BoilerplateSecurityCorsProcessor(allowedUrls));

    return corsFilter;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    String allowedUrls = env.getProperty("boilerplate.cors.allowed-origins");
    Objects.requireNonNull(allowedUrls,
        "property {boilerplate.cors.allowed-origins} is not configured");

    BoilerplateSecurityCorsConfiguration corsConfiguration = new BoilerplateSecurityCorsConfiguration(
        allowedUrls);
    corsConfiguration.registerCorsConfiguration("/**", new CorsConfiguration());
    return corsConfiguration;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @SneakyThrows
  @Bean("CognitoConfigurableJWTProcessor")
  public ConfigurableJWTProcessor cognitoConfigurableJWTProcessor(
      CognitoJwtConfigProperties cognitoJwtConfigProperties) {
    ResourceRetriever resourceRetriever =
        new DefaultResourceRetriever(cognitoJwtConfigProperties.getConnectionTimeout(),
            cognitoJwtConfigProperties.getReadTimeout());
    URL jwkURL = new URL(cognitoJwtConfigProperties.getJwkUrl());
    JWKSource keySource = new RemoteJWKSet(jwkURL, resourceRetriever);
    ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
    JWSKeySelector keySelector = new JWSVerificationKeySelector(RS256, keySource);
    jwtProcessor.setJWSKeySelector(keySelector);
    return jwtProcessor;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @SneakyThrows
  @Bean("MicrosoftConfigurableJWTProcessor")
  public ConfigurableJWTProcessor microsoftConfigurableJWTProcessor() {
    String jwkUrl = env.getProperty("boilerplate.ms.jwks");
    Objects.requireNonNull(jwkUrl,
        "property {boilerplate.ms.jwks} is not configured");
    JWKSet jwkSet = JWKSet.load(new URL(jwkUrl));
    JWKSource<SecurityContext> keySource = new ImmutableJWKSet<>(jwkSet);
    ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
    jwtProcessor.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier<>(new JOSEObjectType("jwt")));
    JWSKeySelector<SecurityContext> keySelector =
        new JWSVerificationKeySelector<SecurityContext>(RS256, keySource);
    jwtProcessor.setJWSKeySelector(keySelector);
    return jwtProcessor;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @SneakyThrows
  @Bean("GuestConfigurableJWTProcessor")
  public ConfigurableJWTProcessor guestConfigurableJWTProcessor() {
    String jwk = env.getProperty("boilerplate.guest.jwk");
    Objects.requireNonNull(jwk,
        "property {boilerplate.guest.jwk} is not configured");
    JWKSet jwkSet = new JWKSet(JWK.parse(jwk));
    JWKSource<SecurityContext> keySource = new ImmutableJWKSet<>(jwkSet);
    ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
    jwtProcessor.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier<>(new JOSEObjectType("jwt")));
    JWSKeySelector<SecurityContext> keySelector =
        new JWSVerificationKeySelector<>(RS512, keySource);
    jwtProcessor.setJWSKeySelector(keySelector);
    return jwtProcessor;
  }

  @Bean("GuestJwtUtil")
  @SneakyThrows
  public JwtUtil jwtUtil() {
    String jwk = env.getProperty("boilerplate.guest.jwk");
    Objects.requireNonNull(jwk,
        "property {boilerplate.guest.jwk} is not configured");
    String issuer = env.getProperty("boilerplate.guest.issuer");
    Objects.requireNonNull(issuer,
        "property {boilerplate.guest.issuer} is not configured");
    String expiryInMs = env.getProperty("boilerplate.guest.expiryInMs");
    Objects.requireNonNull(expiryInMs,
        "property {boilerplate.guest.expiryInMs} is not configured");
    return new JwtUtil(jwk, issuer, Integer.parseInt(expiryInMs));
  }

  @SneakyThrows
  @Primary
  @Order(0)
  @Bean
  public SecurityFilterChain unauthenticatedResource(HttpSecurity http, Tracer tracer) {
    return http.csrf().disable()
        .requestMatchers(
            matcher -> matcher.antMatchers(HttpMethod.GET, "/actuator/health/**")
                .antMatchers(HttpMethod.POST, "/v1/auth/login", "/v1/auth/signup")
                .regexMatchers(HttpMethod.GET, "/v1/auth/verification\\?token=.*")
        )
        .addFilterBefore(new LoggingFilter(tracer), ChannelProcessingFilter.class)
        .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
        .requestCache().disable()
        .securityContext().disable()
        .sessionManagement().disable()
        .build();
  }

  @SneakyThrows
  @Primary
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      @Qualifier("BoilerplateSecurityAccessDeniedHandler") AccessDeniedHandler accessDeniedHandler,
      @Qualifier("BoilerplateSecurityAuthenticationEntryPoint") AuthenticationEntryPoint authenticationEntryPoint,
      @Qualifier("BoilerplateSecurityCorsFilter") CorsFilter ocCorsFilter,
      CognitoIdTokenProcessor cognitoIdTokenProcessor,
      MicrosoftTokenProcessor microsoftTokenProcessor,
      GuestTokenProcessor guestTokenProcessor) {
    return http
        .cors().and()
        .csrf().disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .anyRequest().authenticated()
        .and()
        .exceptionHandling()
        .accessDeniedHandler(accessDeniedHandler)
        .authenticationEntryPoint(authenticationEntryPoint)
        .and()
        .addFilterBefore(new LoggingFilter(tracer), ChannelProcessingFilter.class)
        .addFilterBefore(
            new BoilerplateSecurityAuthenticationFilter(
                cognitoIdTokenProcessor,
                microsoftTokenProcessor,
                guestTokenProcessor,
                authenticationEntryPoint),
            BasicAuthenticationFilter.class)
        .addFilterAt(ocCorsFilter, CorsFilter.class)
        .addFilterAfter(
            new BoilerplateSecurityAuthorizationFilter(
                accessDeniedHandler,
                authenticationEntryPoint
            ),
            BoilerplateSecurityAuthenticationFilter.class)
        .build();
  }
}