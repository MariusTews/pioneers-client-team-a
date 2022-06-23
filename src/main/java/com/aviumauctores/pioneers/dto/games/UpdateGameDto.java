package com.aviumauctores.pioneers.dto.games;

import com.aviumauctores.pioneers.model.GameSettings;

public record UpdateGameDto(
        String name,
        String owner,
        boolean started,
        GameSettings settings,
        String password
) {
}
