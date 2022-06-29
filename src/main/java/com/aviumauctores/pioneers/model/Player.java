package com.aviumauctores.pioneers.model;

import java.util.HashMap;

public record Player(String gameID,
                     String userId,
                     String color,
                     boolean active,
                     int foundingRoll,
                     HashMap<String, Integer> resources,
                     HashMap<String, Integer> remainingBuildings,

                     int victoryPoints,

                     int longestRoad

) {
}