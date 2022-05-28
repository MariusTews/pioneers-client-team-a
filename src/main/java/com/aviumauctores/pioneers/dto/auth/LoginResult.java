package com.aviumauctores.pioneers.dto.auth;

import java.util.List;

public record LoginResult(
        String _id,
        String name,
        String status,
        String avatar,
        String accessToken,
        String refreshToken,

        List<String>  friends
) {
}
