package com.sp.boilerplate.commons.autoconfigure;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@RefreshScope
public class LocaleAutoConfiguration {

  @Getter
  @Setter
  private List<String> supportedLocales = new ArrayList<>();

  // @formatter:off

  /**
   * It will create {@link AcceptHeaderLocaleResolver} We can configure {@code supoortedLocales} in
   * {@code application.yml} file. If we are not configuring {@code supoortedLocales}, then default
   * {@code supoortedLocales} will be considered. We have added {@code @RefreshScope} to
   * {@link LocaleAutoConfiguration} to enable refreshing {@link LocaleResolver} with updated
   * {@code supoortedLocales}
   *
   * @return LocaleResolver
   */
  // @formatter:on
  @RefreshScope
  @Bean
  public LocaleResolver localeResolver() {
    AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
    List<Locale> locales = supportedLocales.stream().map(StringUtils::parseLocale)
        .collect(Collectors.toList());
    localeResolver.setSupportedLocales(locales);
    return localeResolver;
  }
}
