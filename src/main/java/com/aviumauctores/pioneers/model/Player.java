package com.aviumauctores.pioneers.model;

import javafx.scene.paint.Color;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

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
