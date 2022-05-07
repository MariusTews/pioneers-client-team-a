package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.gamemembers.CreateMemberDto;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.rest.GameMembersApiService;
import com.aviumauctores.pioneers.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class CreateGameService {
    private final GameMembersApiService gameMembersApiService;
    private final GamesApiService gamesApiService;

    private Game currentGame;

    @Inject
    public CreateGameService(GameMembersApiService gameMembersApiService, GamesApiService gamesApiService) {
        this.gameMembersApiService = gameMembersApiService;
        this.gamesApiService = gamesApiService;
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

    public Observable<List<Game>> listGames() {
        return gamesApiService.listGames();
    }
}
