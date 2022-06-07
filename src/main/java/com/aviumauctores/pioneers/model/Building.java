package com.aviumauctores.pioneers.model;

import java.util.Arrays;

public record Building(
        int x ,
        int y ,
        int z,
        int side,
        String type,
        String gameId,
        String owner
) {
    public static Building readCoordinatesFromID(String id) {
        if (!id.startsWith("building")) {
            return null;
        }
        String coordinateString = id.substring("building".length()).replace('_', '-');
        int[] coordinates = Arrays.stream(coordinateString.split("(?<=(-?\\d))", 4))
                .mapToInt(Integer::parseInt).toArray();
        return new Building(coordinates[0], coordinates[1], coordinates[2], coordinates[3],null, null, null);
    }
}
