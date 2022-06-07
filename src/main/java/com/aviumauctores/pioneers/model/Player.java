package com.aviumauctores.pioneers.model;

import javafx.scene.paint.Color;

import java.awt.*;

public record Player(String gameID,
                     String userId,
                     String color,
                     int foundingRoll,
                     int unknown,
                     int grain,
                     int brick,
                     int ore,
                     int lumber,
                     int wool,
                     int settlement,
                     int city,
                     int road
) {
}
