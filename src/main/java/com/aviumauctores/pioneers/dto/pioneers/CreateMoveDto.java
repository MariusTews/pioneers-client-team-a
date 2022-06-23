package com.aviumauctores.pioneers.dto.pioneers;

import com.aviumauctores.pioneers.dto.rob.RobDto;
import com.aviumauctores.pioneers.model.Building;

import java.util.HashMap;

public record CreateMoveDto(
        String action,
        Building building,
        HashMap<String, Integer> resources,
        String partner,
        RobDto rob
) {
}
