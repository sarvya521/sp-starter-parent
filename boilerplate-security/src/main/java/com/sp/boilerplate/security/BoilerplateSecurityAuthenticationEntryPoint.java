package com.sp.boilerplate.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.boilerplate.commons.constant.Status;
import com.sp.boilerplate.commons.dto.ErrorDetails;
import com.sp.boilerplate.commons.dto.Response;
import com.sp.boilerplate.commons.exception.BoilerplateException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Slf4j
public class BoilerplateSecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {

  static final String ERROR_MESSAGE = "Authorization token not present or invalid";
  static final String EXPIRED_TOKEN_ERROR_MESSAGE = "Authorization token expired";

  private ObjectMapper objectMapper;

  public BoilerplateSecurityAuthenticationEntryPoint(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public void commence(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      AuthenticationException authException) throws
      IOException {
    log.error("Unauthenticated Access", authException);
    if (httpServletResponse.getStatus() == HttpStatus.FORBIDDEN.value()) {
      return;
    }

    try (ServletServerHttpResponse res = new ServletServerHttpResponse(httpServletResponse)) {
      res.setStatusCode(HttpStatus.UNAUTHORIZED);
      res.getServletResponse()
          .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      Response response = new Response();
      response.setStatus(Status.FAIL);
      response.setCode(HttpStatus.UNAUTHORIZED.value());
      ErrorDetails errorDetails;
      if (authException.getCause() instanceof BoilerplateException) {
        errorDetails = ((BoilerplateException) authException.getCause()).getError();
      } else {
        errorDetails = new ErrorDetails(String.valueOf(HttpStatus.UNAUTHORIZED.value()),
            ERROR_MESSAGE);
      }
      List<ErrorDetails> errors = new ArrayList<>();
      errors.add(errorDetails);
      response.setErrors(errors);
      res.getBody().write(objectMapper.writeValueAsString(response).getBytes());
    }
  }
}
