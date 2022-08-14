package com.sp.boilerplate.commons.exception;

import lombok.Getter;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * see {@link ErrorDetails}
     */
    @Getter
    private final transient ErrorDetails error;


    /**
     * @param error GhError
     * @see RuntimeException#RuntimeException(String)
     */
    public ResourceNotFoundException(ErrorDetails error) {
        super(error.toString());
        this.error = error;
    }

    /**
     * @param message message
     * @see RuntimeException#RuntimeException(String)
     */
    public ResourceNotFoundException(String message) {
        super(message);
        this.error = null;
    }

}
