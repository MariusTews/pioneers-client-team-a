package com.aviumauctores.pioneers.model;

public record Member(
        String createdAt,
        String updatedAt,
        String gameId,
        String userId,
        boolean ready
) {
}
