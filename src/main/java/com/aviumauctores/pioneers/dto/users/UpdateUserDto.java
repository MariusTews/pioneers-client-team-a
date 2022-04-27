package com.aviumauctores.pioneers.dto.users;

public record UpdateUserDto(
        String name,
        String status,
        String avatar,
        String password
) {
}
