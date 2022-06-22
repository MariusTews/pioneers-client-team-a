package com.aviumauctores.pioneers.dto.gamemembers;

import javafx.scene.paint.Color;

public record UpdateMemberDto(
        boolean ready,
        String color,
        boolean spectator
) {
}
