package com.aviumauctores.pioneers.dto.users;

public record CreateUserDto(
        String name,
        String avatar,
        String password
) {
}
