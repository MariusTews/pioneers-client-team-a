package com.aviumauctores.pioneers.dto.gamemembers;


import javafx.scene.paint.Color;

public record CreateMemberDto(

        boolean ready,
        String color,
        String password,
        boolean spectator
) {
}
