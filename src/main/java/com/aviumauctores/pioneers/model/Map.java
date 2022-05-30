package com.aviumauctores.pioneers.model;

import java.util.List;

public record Map(
        String gameId,
        List<Tile> tiles
) {
}
