package com.BackEnd.Master.GYM.ExceptionHandler;

import java.time.Instant;
import java.util.Map;

// Single response shape every handler in GlobalExceptionHandler funnels through,
// so every error the API returns is JSON with the same fields.
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors) {
}
