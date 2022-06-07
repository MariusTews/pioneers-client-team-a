package com.aviumauctores.pioneers.dto.games;

public record UpdateGameDto(
        String name,
        String owner,
        boolean started,
        String password
) {
}
