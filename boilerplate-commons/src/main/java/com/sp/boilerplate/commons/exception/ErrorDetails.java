package com.sp.boilerplate.commons.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Wrapper of error details that has to be sent in Api Response
 *
 * @author sarvesh
 * @version 0.0.1
 * @see com.sp.boilerplate.commons.dto.Response
 * @since 0.0.1
 */
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_NULL)
@JsonPropertyOrder({"code", "message", "target"})
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "All details about the error.")
public class ErrorDetails {
    /**
     * Service Error Code
     */
    @Schema(description = "Service Error Code")
    private String code;
    /**
     * Service Error Message
     */
    @Schema(description = "Service Error Message")
    private String message;
    /**
     * Error's whereabouts
     */
    @Schema(description = "Error's whereabouts")
    private String target;

    /**
     * @param code
     * @param message
     */
    public ErrorDetails(@NonNull String code, @NonNull String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @param code
     * @param message
     * @param target
     */
    public ErrorDetails(@NonNull String code, @NonNull String message, @NonNull String target) {
        this.code = code;
        this.message = message;
        this.target = target;
    }

    @Override
    public String toString() {
        String error = String.format("%s-%s", code, message);
        if(null != target && !target.isBlank()) {
            error = String.format("%s target:%s", error, target);
        }
        return error;
    }
}
