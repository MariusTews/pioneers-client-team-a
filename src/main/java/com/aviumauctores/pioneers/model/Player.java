package com.aviumauctores.pioneers.model;

import java.awt.*;

public record Player(String gameID,
                     String userId,
                     Color color,
                     int foundingRoll,
                     int unknow,
                     int gain,
                     int brick,
                     int ore,
                     int lumber,
                     int wool,
                     int settlement,
                     int city,
                     int road
) {
}
