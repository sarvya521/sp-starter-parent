package com.sp.boilerplate.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.boilerplate.commons.constant.Status;
import com.sp.boilerplate.commons.dto.ErrorDetails;
import com.sp.boilerplate.commons.dto.Response;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Slf4j
public class BoilerplateSecurityAccessDeniedHandler implements AccessDeniedHandler {

  private static final String ERROR_MESSAGE = "You are not allowed to access this resource";

  private ObjectMapper objectMapper;

  public BoilerplateSecurityAccessDeniedHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
  @Override
  public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
      AccessDeniedException accessDeniedException) throws IOException {
    log.error("user is not allowed to access the resource", accessDeniedException);
    SecurityContextHolder.clearContext();

    try (ServletServerHttpResponse res = new ServletServerHttpResponse(httpServletResponse)) {
      res.setStatusCode(HttpStatus.FORBIDDEN);
      res.getServletResponse()
          .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

      Response response = new Response();
      response.setStatus(Status.FAIL);
      response.setCode(HttpStatus.FORBIDDEN.value());
      ErrorDetails ocError = new ErrorDetails(String.valueOf(HttpStatus.FORBIDDEN.value()),
          ERROR_MESSAGE);
      List<ErrorDetails> errors = new ArrayList<>();
      errors.add(ocError);
      response.setErrors(errors);

      res.getBody().write(objectMapper.writeValueAsString(response).getBytes());
    }
  }
}
