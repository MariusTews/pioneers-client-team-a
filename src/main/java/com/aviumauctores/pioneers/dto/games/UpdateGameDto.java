package com.aviumauctores.pioneers.dto.games;

public record UpdateGameDto(
        String name,
        String owner,
        String password
) {
}
