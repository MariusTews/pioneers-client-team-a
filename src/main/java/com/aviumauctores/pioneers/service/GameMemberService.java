package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.model.User;
import io.reactivex.rxjava3.core.Observable;
import javax.inject.Inject;


public class GameMemberService {

    public final GameService service;


    @Inject
    public GameMemberService(GameService service) {
        this.service = service;
    }


    public void deleteGame(){

    }
}
