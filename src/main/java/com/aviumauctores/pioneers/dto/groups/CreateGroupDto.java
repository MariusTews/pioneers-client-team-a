package com.aviumauctores.pioneers.dto.groups;

import java.util.List;

public record CreateGroupDto(
        List<String> members
) {
}
