package com.aviumauctores.pioneers.dto.gamemembers;

public record CreateMemberDto(

        boolean ready,
        String color,
        String password,
        boolean spectator
) {
}
