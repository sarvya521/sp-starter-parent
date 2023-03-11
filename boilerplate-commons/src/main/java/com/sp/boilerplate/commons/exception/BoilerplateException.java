package com.sp.boilerplate.commons.exception;

import com.sp.boilerplate.commons.dto.ErrorDetails;
import lombok.Getter;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public class BoilerplateException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * see {@link ErrorDetails}
   */
  @Getter
  private final ErrorDetails error;


  /**
   * @param error ErrorDetails
   * @see RuntimeException#RuntimeException(String)
   */
  public BoilerplateException(ErrorDetails error) {
    super(error.toString());
    this.error = error;
  }

  /**
   * @param error ErrorDetails
   * @see RuntimeException#RuntimeException(String)
   */
  public BoilerplateException(ErrorDetails error, Throwable cause) {
    super(error.toString(), cause);
    this.error = error;
  }
}
