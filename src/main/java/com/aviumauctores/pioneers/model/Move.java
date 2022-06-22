package com.aviumauctores.pioneers.model;

import com.aviumauctores.pioneers.dto.rob.RobDto;

import java.util.HashMap;

public record Move(String _id,
                   String createdAt,
                   String gameId,
                   String userId,
                   String action,
                   int roll,
                   String building,

                   RobDto rob,

                   HashMap<String, Integer> resources,

                   String partner


                   ) {
}
