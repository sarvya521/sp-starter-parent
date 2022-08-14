package com.sp.boilerplate.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Slf4j
@Component
public class TraceFilter extends OncePerRequestFilter {
    private static final String TRACKING_HEADER = "X-Trace-Id";
    private Tracer tracer;
    public TraceFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        attacheTraceId(httpServletResponse);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void attacheTraceId(HttpServletResponse response) {
        final Span currentSpan = tracer.currentSpan();
        if (null != currentSpan) {
            final String traceId = currentSpan.context().traceId();
            log.debug("Added tracking id in response - {}", traceId);
            response.setHeader(TRACKING_HEADER, traceId);
        }
    }
}
