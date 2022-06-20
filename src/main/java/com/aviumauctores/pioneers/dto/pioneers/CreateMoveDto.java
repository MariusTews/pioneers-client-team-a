package com.aviumauctores.pioneers.dto.pioneers;

import com.aviumauctores.pioneers.dto.rob.RobDto;
import com.aviumauctores.pioneers.model.Building;

public record CreateMoveDto(
        String action,
        Building building,
        String partner,
        RobDto rob
) {
}
