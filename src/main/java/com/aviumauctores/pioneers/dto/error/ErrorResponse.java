package com.aviumauctores.pioneers.dto.error;

public record ErrorResponse(
        int statusCode,
        String error,
        String message
) {
}
