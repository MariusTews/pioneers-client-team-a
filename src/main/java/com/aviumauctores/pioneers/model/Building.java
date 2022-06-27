package com.aviumauctores.pioneers.model;

import java.util.Arrays;

public record Building(
        int x,
        int y,
        int z,
        int side,
        String _id,
        String type,
        String gameId,
        String owner
) {
    /**
     * Returns a dummy object which only contains the coordinates given by the building id.
     */
    public static Building readCoordinatesFromID(String id) {
        if (!id.startsWith("building")) {
            return null;
        } else if (id.contains("#")) {
            id = id.split("#")[0];
        }
        // Cut the building string off and replace _ with -
        // so coordinateString contains only the concatenated coordinates
        String coordinateString = id.substring("buildingX".length()).replace('_', '-');
        coordinateString = coordinateString.replace('Y', '#');
        coordinateString = coordinateString.replace('Z', '#');
        coordinateString = coordinateString.replace('R', '#');
        int[] coordinates = Arrays.stream(coordinateString.split("#", 4))
                .mapToInt(Integer::parseInt).toArray();

        return new Building(coordinates[0], coordinates[1], coordinates[2], coordinates[3], null, null, null, null);
    }
}
