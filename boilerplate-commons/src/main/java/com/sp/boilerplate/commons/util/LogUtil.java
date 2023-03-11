package com.sp.boilerplate.commons.util;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public final class LogUtil {

  private LogUtil() {
    throw new AssertionError();
  }

  private enum Direction {
    IN, OUT
  }

  public static void fillMdcForIncomingApi(String target, String httpMethod,
      String xForwardedFor, String referer,
      String userAgent, int httpStatusCode) {
    MDC.put("target", target);
    MDC.put("http_method", httpMethod);
    MDC.put("x-forwarded-for", xForwardedFor);
    MDC.put("referer", referer);
    MDC.put("user_agent", userAgent);
    MDC.put("http_status_code", String.valueOf(httpStatusCode));
    MDC.put("direction", Direction.IN.name());
  }

  public static void updateHttpStatusInMdc(int httpStatusCode) {
    MDC.put("http_status_code", String.valueOf(httpStatusCode));
  }

  public static void clearMdcForIncomingApi() {
    MDC.remove("target");
    MDC.remove("http_method");
    MDC.remove("x-forwarded-for");
    MDC.remove("referer");
    MDC.remove("user_agent");
    MDC.remove("http_status_code");
    MDC.remove("direction");
  }

  public static void fillMdcForOutgoingApi(String target, String httpMethod,
      int httpStatusCode) {
    MDC.put("target", target);
    MDC.put("http_method", httpMethod);
    MDC.put("http_status_code", String.valueOf(httpStatusCode));
    MDC.put("direction", Direction.OUT.name());
  }

  public static void clearMdcForOutgoingApi() {
    MDC.remove("target");
    MDC.remove("http_method");
    MDC.remove("http_status_code");
    MDC.remove("direction");
  }

  public static void fillCommonMdc(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("x-forwarded-for");
    if (Objects.isNull(xForwardedFor)) {
      MDC.put("user_ip", request.getRemoteAddr());
    } else {
      MDC.put("user_ip", xForwardedFor.split(",")[0]);
    }
    MDC.put("api", request.getMethod() + "-" + request.getRequestURI());
  }
}
