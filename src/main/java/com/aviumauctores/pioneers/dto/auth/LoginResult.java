package com.aviumauctores.pioneers.dto.auth;

public record LoginResult(
        String _id,
        String name,
        String status,
        String avatar,
        String accessToken,
        String refreshToken
) {
}
