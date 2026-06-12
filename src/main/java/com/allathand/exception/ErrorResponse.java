package com.allathand.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    Instant timestamp;
    int status;
    String error;
    String message;
    List<FieldError> details;

    @Value
    public static class FieldError {
        String field;
        String message;
    }
}
