package com.aviumauctores.pioneers.model;

public record Building(
        int x ,

        int y ,

        int z,

        int side,

        String type,

        String gameId,

        String owner
) {
}
