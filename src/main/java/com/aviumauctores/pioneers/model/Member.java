package com.aviumauctores.pioneers.model;

public record Member(
        String createdAt,
        String updatedAt,
        String gameID,
        String userID,
        boolean ready
) {
}
