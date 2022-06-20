package com.aviumauctores.pioneers.dto;

import com.aviumauctores.pioneers.dto.auth.LoginDto;
import com.aviumauctores.pioneers.dto.games.UpdateGameDto;
import com.aviumauctores.pioneers.dto.players.UpdatePlayerDto;
import com.aviumauctores.pioneers.dto.rob.RobDto;
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

        UpdatePlayerDto updatePlayerDto=new UpdatePlayerDto(true);
        Assertions.assertEquals(updatePlayerDto.active(),true);
        UpdatePlayerDto updatePlayerDto1=new UpdatePlayerDto(false);
        Assertions.assertEquals(updatePlayerDto1.active(),false);

        RobDto robDto= new RobDto(1,2,3,"target");
        Assertions.assertEquals(robDto.x(),1);
        Assertions.assertEquals(robDto.y(),2);
        Assertions.assertEquals(robDto.z(),3);
        Assertions.assertEquals(robDto.target(),"target");

    }





}