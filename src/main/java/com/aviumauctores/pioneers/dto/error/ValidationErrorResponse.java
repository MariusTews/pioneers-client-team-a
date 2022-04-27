package com.aviumauctores.pioneers.dto.error;

public record ValidationErrorResponse(
        int statusCode,
        String error,
        String message
) {
}
