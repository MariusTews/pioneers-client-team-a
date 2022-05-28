package com.aviumauctores.pioneers.model;

import java.awt.*;

public record Member(
        String createdAt,

        String updatedAt,

        String gameId,

        String userId,

        boolean ready,

        Color color
) {
}
