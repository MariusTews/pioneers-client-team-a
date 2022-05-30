package com.aviumauctores.pioneers.dto.users;

import java.util.List;

public record UpdateUserDto(
        String name,
        String status,
        String avatar,
        String password,
        List<String>friends
) {
}
