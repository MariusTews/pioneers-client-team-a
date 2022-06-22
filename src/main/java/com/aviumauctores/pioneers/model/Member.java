package com.aviumauctores.pioneers.model;


import javafx.scene.paint.Color;

public record Member(
        String createdAt,
        String updatedAt,
        String gameId,
        String userId,
        boolean ready,
        Color color,

        boolean spectator
) {
}
