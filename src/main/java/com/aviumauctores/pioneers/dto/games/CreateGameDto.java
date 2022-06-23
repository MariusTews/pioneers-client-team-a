package com.aviumauctores.pioneers.dto.games;

import com.aviumauctores.pioneers.model.GameSettings;

public record CreateGameDto(
        String name,
        boolean started,
        GameSettings settings,
        String password
) {
}
