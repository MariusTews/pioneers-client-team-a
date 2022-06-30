package com.aviumauctores.pioneers.model;

import java.util.Arrays;

public record Point3D(
        int x,
        int y,
        int z
) {
    /**
     * Returns a dummy object which only contains the coordinates given by the building id.
     */
    public static Point3D readCoordinatesFromID(String id) {
        if (!id.startsWith("robber")) {
            return null;
        } else if (id.contains("#")) {
            id = id.split("#")[0];
        }
        // Cut the robber string off and replace _ with -
        // so coordinateString contains only the concatenated coordinates
        String coordinateString = id.substring("robberX".length()).replace('_', '-');
        coordinateString = coordinateString.replace('Y', '#');
        coordinateString = coordinateString.replace('Z', '#');
        int[] coordinates = Arrays.stream(coordinateString.split("#", 4))
                .mapToInt(Integer::parseInt).toArray();

        return new Point3D(coordinates[0], coordinates[1], coordinates[2]);
    }
}
