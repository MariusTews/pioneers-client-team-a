package com.aviumauctores.pioneers.dto.gamemembers;


import java.awt.*;

public record CreateMemberDto(

        boolean ready,
        String password,
        String color
) {
}
