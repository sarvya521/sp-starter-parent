package com.sp.boilerplate.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Objects;

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
    public AccessDeniedHandler ocAccessDeniedHandler(@Qualifier("AuthObjectMapper") ObjectMapper authObjectMapper) {
        log.debug("configuring BoilerplateSecurityAccessDeniedHandler");
        return new BoilerplateSecurityAccessDeniedHandler(authObjectMapper);
    }

    @Bean("BoilerplateSecurityAuthenticationEntryPoint")
    public AuthenticationEntryPoint ocAuthenticationEntryPoint(@Qualifier("AuthObjectMapper") ObjectMapper authObjectMapper) {
        log.debug("configuring BoilerplateSecurityAuthenticationEntryPoint");
        return new BoilerplateSecurityAuthenticationEntryPoint(authObjectMapper);
    }

    @Bean("BoilerplateSecurityCorsFilter")
    public CorsFilter corsFilter() {
        log.debug("configuring BoilerplateSecurityCorsFilter");
        String allowedUrls = env.getProperty("boilerplate.cors.allowed-origins");
        Objects.requireNonNull(allowedUrls, "property {boilerplate.cors.allowed-origins} is not configured");

        final CorsFilter corsFilter = new CorsFilter(corsConfigurationSource());
        corsFilter.setCorsProcessor(new BoilerplateSecurityCorsProcessor(allowedUrls));

        return corsFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        String allowedUrls = env.getProperty("boilerplate.cors.allowed-origins");
        Objects.requireNonNull(allowedUrls, "property {boilerplate.cors.allowed-origins} is not configured");

        BoilerplateSecurityCorsConfiguration corsConfiguration = new BoilerplateSecurityCorsConfiguration(allowedUrls);
        corsConfiguration.registerCorsConfiguration("/**", new CorsConfiguration());
        return corsConfiguration;
    }

    @Primary
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {
            web.ignoring()
                    .antMatchers(HttpMethod.GET,"/actuator/health/**");
//                    .antMatchers(HttpMethod.GET, WHITE_LISTED_APIS);
        };
    }

    @Primary
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
                                           @Qualifier("BoilerplateSecurityAccessDeniedHandler") AccessDeniedHandler ocAccessDeniedHandler,
                                           @Qualifier("BoilerplateSecurityAuthenticationEntryPoint") AuthenticationEntryPoint ocAuthenticationEntryPoint,
                                           @Qualifier("BoilerplateSecurityCorsFilter") CorsFilter ocCorsFilter,
                                           CognitoIdTokenProcessor cognitoIdTokenProcessor) throws Exception {
        httpSecurity
                .cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                        .antMatchers(HttpMethod.GET, WHITE_LISTED_APIS).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(ocAccessDeniedHandler)
                .authenticationEntryPoint(ocAuthenticationEntryPoint)
                .and()
                .addFilterBefore(new TraceFilter(tracer), ChannelProcessingFilter.class)
                .addFilterBefore(
                        new BoilerplateSecurityAuthenticationFilter(cognitoIdTokenProcessor, ocAuthenticationEntryPoint),
                        BasicAuthenticationFilter.class)
                .addFilterAt(ocCorsFilter, CorsFilter.class)
                .addFilterAfter(
                        new BoilerplateSecurityAuthorizationFilter(
                                ocAccessDeniedHandler,
                                ocAuthenticationEntryPoint
                        ),
                        BoilerplateSecurityAuthenticationFilter.class);

        return httpSecurity.build();
    }


}