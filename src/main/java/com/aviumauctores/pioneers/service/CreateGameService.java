package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.gamemembers.CreateMemberDto;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.rest.GameMembersApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CreateGameService {
    private final GameMembersApiService gameMembersApiService;

    private Game currentGame;

    @Inject
    public CreateGameService(GameMembersApiService gameMembersApiService) {
        this.gameMembersApiService = gameMembersApiService;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }

    public Observable<Member> joinGame(String password) {
        return gameMembersApiService.createMember(currentGame._id(), new CreateMemberDto(true, password));
    }
}
