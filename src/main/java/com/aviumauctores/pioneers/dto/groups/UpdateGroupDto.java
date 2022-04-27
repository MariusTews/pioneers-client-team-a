package com.aviumauctores.pioneers.dto.groups;

import java.util.List;

public record UpdateGroupDto(
        List<String> members
) {
}
