package com.ialmeida.marketpulse.user.dto.error;

import java.time.Instant;
import java.util.List;

public class ApiErrorResponse {

    private final int status;
    private final String message;
    private final List<FieldError> errors;
    private final String path;
    private final Instant timestamp;

    public ApiErrorResponse(
        int status,
        String message,
        List<FieldError> errors,
        String path,
        Instant timestamp
    ) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.path = path;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public String getPath() {
        return path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public record FieldError(
        String field,
        String message
    ) {
    }
}
