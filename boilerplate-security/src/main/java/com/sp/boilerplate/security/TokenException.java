package com.sp.boilerplate.security;

import com.sp.boilerplate.commons.dto.ErrorDetails;
import com.sp.boilerplate.commons.exception.BoilerplateException;

public class TokenException extends BoilerplateException {

  public TokenException(ErrorDetails errorDetails) {
    super(errorDetails);
  }

  public TokenException(ErrorDetails errorDetails, Throwable cause) {
    super(errorDetails, cause);
  }

}
