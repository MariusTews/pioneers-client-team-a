package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.gamemembers.CreateMemberDto;
import com.aviumauctores.pioneers.dto.games.CreateGameDto;
import com.aviumauctores.pioneers.dto.games.UpdateGameDto;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.rest.GameMembersApiService;
import com.aviumauctores.pioneers.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class GameService {
    private final GameMembersApiService gameMembersApiService;
    private final GamesApiService gamesApiService;

    private String currentGameID;

    private String ownerID;

    private String name;

    public String password;

    @Inject
    public GameService(GameMembersApiService gameMembersApiService, GamesApiService gamesApiService) {
        this.gameMembersApiService = gameMembersApiService;
        this.gamesApiService = gamesApiService;
    }

    public String getCurrentGameID() {
        return currentGameID;
    }

    public void setCurrentGameID(String currentGameID) {
        this.currentGameID = currentGameID;
    }

    public Observable<String> create(String name, String password){
        this.name = name;
        this.password = password;
        return gamesApiService.createGame(new CreateGameDto(name, password))
                .map(Game::_id);
    }

    public Observable<Member> joinGame(String password) {
        return gameMembersApiService.createMember(currentGameID, new CreateMemberDto(true, password));
    }

    public Observable<List<Game>> listGames() {
        return gamesApiService.listGames();
    }

    public Observable<Game> getCurrentGame() {
        return gamesApiService.getGame(currentGameID);
    }


    public Observable<Game> deleteGame(){
        return gamesApiService.deleteGame(currentGameID);
    }

    public Observable<Game> updateGame(){
        return gamesApiService.updateGame(currentGameID, new UpdateGameDto(name, ownerID, password));
    }

    public String getOwnerID(){
        return this.ownerID;
    }

    public void setOwnerID(String ID){
        this.ownerID = ID;
    }

}