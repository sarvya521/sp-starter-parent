package com.sp.boilerplate.commons.interceptor;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public class AsyncRequestHeaderTaskDecorator implements TaskDecorator {

  @Override
  public Runnable decorate(Runnable runnable) {
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    RequestAttributes context = RequestContextHolder.currentRequestAttributes();
    final Authentication a = SecurityContextHolder.getContext().getAuthentication();
    return () -> {
      try {
        MDC.setContextMap(contextMap);
        RequestContextHolder.setRequestAttributes(context);
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(a);
        SecurityContextHolder.setContext(ctx);
        runnable.run();
      } finally {
        MDC.clear();
        RequestContextHolder.resetRequestAttributes();
        SecurityContextHolder.clearContext();
      }
    };
  }
}