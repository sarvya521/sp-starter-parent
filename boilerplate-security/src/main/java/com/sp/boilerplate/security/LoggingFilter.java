package com.sp.boilerplate.security;

import static ch.qos.logback.classic.Level.DEBUG;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.sp.boilerplate.commons.util.LogUtil;
import java.io.IOException;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

  private static final String TRACKING_HEADER = "X-Trace-Id";
  private static final String SAMPLING_HEADER = "debug-enabled";
  private static final String BASE_PACKAGE = "com.sp";
  private static final Set<String> BOOLEAN_VALUES = Set.of("true", "yes", "y", "1");
  private Tracer tracer;

  public LoggingFilter(Tracer tracer) {
    this.tracer = tracer;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String enableDebug = request.getHeader(SAMPLING_HEADER);
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    Level existingRootLevel = loggerContext.getLogger(ROOT_LOGGER_NAME).getLevel();
    Level existingAppLevel = loggerContext.getLogger(BASE_PACKAGE).getLevel();

    if (StringUtils.hasText(enableDebug) && BOOLEAN_VALUES.contains(enableDebug.toLowerCase())) {
      loggerContext.getLogger(ROOT_LOGGER_NAME).setLevel(DEBUG);
      loggerContext.getLogger(BASE_PACKAGE).setLevel(DEBUG);
    }

    attacheTraceId(response);
    LogUtil.fillCommonMdc(request);

    chain.doFilter(request, response);

    MDC.clear();

    if (StringUtils.hasText(enableDebug) && BOOLEAN_VALUES.contains(enableDebug.toLowerCase())) {
      loggerContext.getLogger(ROOT_LOGGER_NAME).setLevel(existingRootLevel);
      loggerContext.getLogger(BASE_PACKAGE).setLevel(existingAppLevel);
    }
  }

  private void attacheTraceId(HttpServletResponse response) {
    final Span currentSpan = tracer.currentSpan();
    if (null != currentSpan) {
      final String traceId = currentSpan.context().traceId();
      log.debug("added tracking id in response - {}", traceId);
      response.setHeader(TRACKING_HEADER, traceId);
    }
  }
}
