package com.aviumauctores.pioneers.dto.error;

import java.util.List;

public record ValidationErrorResponse(
        int statusCode,
        String error,
        List<String> message
) {
}
