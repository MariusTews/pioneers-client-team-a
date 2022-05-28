package com.aviumauctores.pioneers.dto.pioneers;

public record CreateMoveDto(
        String action,

        CreateBuildingDto building
) {
}
