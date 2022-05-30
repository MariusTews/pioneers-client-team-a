package com.aviumauctores.pioneers.dto.groups;

import java.util.List;

public record UpdateGroupDto(
        String name,
        List<String> members
) {
}
