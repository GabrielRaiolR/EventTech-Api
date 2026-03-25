package com.eventostec.api.exception;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String message,
        String path,
        List<FieldViolation> fieldErrors
) {
    public static ApiErrorResponse of(int status, String message, String path, List<FieldViolation> fieldErrors) {
        return new ApiErrorResponse(Instant.now(), status, message, path,
                fieldErrors != null ? List.copyOf(fieldErrors) : List.of());
    }
}
