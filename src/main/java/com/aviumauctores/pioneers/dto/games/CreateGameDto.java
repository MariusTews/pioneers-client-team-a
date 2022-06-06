package com.aviumauctores.pioneers.dto.games;

public record CreateGameDto(
        String name,
        boolean started,
        String password
) {
}
