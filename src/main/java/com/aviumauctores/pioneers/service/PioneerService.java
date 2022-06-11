package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.pioneers.CreateMoveDto;
import com.aviumauctores.pioneers.model.*;
import com.aviumauctores.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class PioneerService {

    private final GameService gameService;
    private final GameMemberService gameMemberService;
    private final ColorService colorService;
    private final PioneersApiService pioneersApiService;
    private String gameID;

    @Inject
    public PioneerService(GameService gameService, GameMemberService gameMemberService, ColorService colorService,
                          PioneersApiService pioneersApiService) {

        this.gameService = gameService;
        this.gameMemberService = gameMemberService;
        this.colorService = colorService;
        this.pioneersApiService = pioneersApiService;
    }

    public Observable<State> getState() {
        return pioneersApiService.getState(gameService.getCurrentGameID());
    }

    public Observable<Move> createMove(String action, Building building) {
        return pioneersApiService.createMove(gameService.getCurrentGameID(), new CreateMoveDto(action, building));
    }

    public Observable<Player> getPlayer(String playerID) {
        return pioneersApiService.getPlayer(gameService.getCurrentGameID(), playerID);
    }

    public Observable<List<Player>> listPlayers() {
        return pioneersApiService.listMembers(gameService.getCurrentGameID());
    }

    public Observable<List<Building>> listBuildings(){
        return pioneersApiService.listBuildings(gameService.getCurrentGameID());
    }

    public Observable<Map> getMap() {
        return pioneersApiService.getMap(gameService.getCurrentGameID());
    }

    public void setGameID(String id) {
        this.gameID = id;
    }
}
