package com.aviumauctores.pioneers.dto.gamemembers;

public record UpdateMemberDto(
        boolean ready,
        String color,
        boolean spectator
) {
}
