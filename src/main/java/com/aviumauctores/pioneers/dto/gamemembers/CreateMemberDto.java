package com.aviumauctores.pioneers.dto.gamemembers;

public record CreateMemberDto(
        boolean ready,
        String password
) {
}
