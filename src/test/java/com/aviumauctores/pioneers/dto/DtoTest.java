package com.aviumauctores.pioneers.dto;

import com.aviumauctores.pioneers.dto.auth.LoginDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DtoTest {

    @Test
    void dtoTest() {
        LoginDto loginDto = new LoginDto(
                "Struppi",
                "12345678"
        );
        Assertions.assertEquals(loginDto.name(), "Struppi");
        Assertions.assertEquals(loginDto.password(), "12345678");
    }
}