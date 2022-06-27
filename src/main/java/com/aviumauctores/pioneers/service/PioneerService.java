package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.pioneers.CreateMoveDto;
import com.aviumauctores.pioneers.dto.rob.RobDto;
import com.aviumauctores.pioneers.model.*;
import com.aviumauctores.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

public class PioneerService {

    private final GameService gameService;
    private final PioneersApiService pioneersApiService;

    @Inject
    public PioneerService(GameService gameService, PioneersApiService pioneersApiService) {

        this.gameService = gameService;
        this.pioneersApiService = pioneersApiService;
    }

    public Observable<State> getState() {
        return pioneersApiService.getState(gameService.getCurrentGameID());
    }

    public Observable<Move> createMove(String action, Building building, String partner,
                                       RobDto rob, HashMap<String, Integer> resources) {
        return pioneersApiService.createMove(gameService.getCurrentGameID(), new CreateMoveDto(action, building, resources, partner, rob));
    }


    public Observable<Player> getPlayer(String playerID) {
        return pioneersApiService.getPlayer(gameService.getCurrentGameID(), playerID);
    }

    public Observable<List<Player>> listPlayers() {
        return pioneersApiService.listMembers(gameService.getCurrentGameID());
    }

    public Observable<List<Building>> listBuildings() {
        return pioneersApiService.listBuildings(gameService.getCurrentGameID());
    }

    public Observable<Map> getMap() {
        return pioneersApiService.getMap(gameService.getCurrentGameID());
    }

    public Observable<Building> getBuilding(String buildingID) {
        return pioneersApiService.getBuilding(gameService.getCurrentGameID(), buildingID);
    }

}
