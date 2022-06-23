package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.gamemembers.CreateMemberDto;
import com.aviumauctores.pioneers.dto.games.CreateGameDto;
import com.aviumauctores.pioneers.dto.games.UpdateGameDto;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.GameSettings;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.rest.GameMembersApiService;
import com.aviumauctores.pioneers.rest.GamesApiService;
import com.aviumauctores.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class GameService {
    private final GameMembersApiService gameMembersApiService;
    private final GamesApiService gamesApiService;
    private final PioneersApiService pioneersApiService;

    private String currentGameID;

    private String ownerID;

    private String name;

    public String password;

    @Inject
    public GameService(GameMembersApiService gameMembersApiService, GamesApiService gamesApiService, PioneersApiService pioneersApiService) {
        this.gameMembersApiService = gameMembersApiService;
        this.gamesApiService = gamesApiService;
        this.pioneersApiService = pioneersApiService;
    }

    public String getCurrentGameID() {
        return currentGameID;
    }

    public void setCurrentGameID(String currentGameID) {
        this.currentGameID = currentGameID;

    }

    public Observable<String> create(String name, String password) {
        this.name = name;
        this.password = password;
        return gamesApiService.createGame(new CreateGameDto(name, false, new GameSettings(2, 10), password))
                .map(Game::_id);
    }

    public Observable<Member> joinGame(String password) {
        return gameMembersApiService.createMember(currentGameID, new CreateMemberDto(false, null, password, false));
    }

    public Observable<List<Game>> listGames() {
        return gamesApiService.listGames();
    }

    public Observable<Game> getCurrentGame() {
        return gamesApiService.getGame(currentGameID);
    }


    public Observable<Game> deleteGame() {
        return gamesApiService.deleteGame(currentGameID);
    }

    public Observable<Game> updateGame(Boolean started) {
        return gamesApiService.updateGame(currentGameID, new UpdateGameDto(name, ownerID, started, null, password));
    }

    public Observable<Game> startGame() {
        return gamesApiService.updateGame(currentGameID, new UpdateGameDto(null, null, true, null, null));
    }

    public String getOwnerID() {
        return this.ownerID;
    }

    public void setOwnerID(String ID) {
        this.ownerID = ID;
    }
}