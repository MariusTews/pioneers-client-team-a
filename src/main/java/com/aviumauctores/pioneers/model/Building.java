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
        String coordinateString = id.substring("building".length()).replace('_', '-');
        // The call coordinateString.split("(?<=(-?\\d))", 4) splits coordinateString after any digit
        // which could be preceded by a negative sign (this works because the x, y, z coordinates have only one digit).
        // The parameter limit=4 ensures the array contains at most 4 elements
        // which is necessary because the last coordinate (side) could have two digits
        // which would be else split in two single array elements.
        int[] coordinates = Arrays.stream(coordinateString.split("(?<=(-?\\d))", 4))
                .mapToInt(Integer::parseInt).toArray();
        return new Building(coordinates[0], coordinates[1], coordinates[2], coordinates[3], null, null, null, null);
    }
}
