package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.controller.PlayerResourceListController;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.model.State;

import javax.inject.Inject;

import static com.aviumauctores.pioneers.Constants.*;
import static com.aviumauctores.pioneers.Constants.MOVE_FOUNDING_ROAD;

public class StateService {
    private final GameMemberService gameMemberService;
    private final UserService userService;
    private final BuildService buildService;
    private final PioneerService pioneerService;
    private State currentState;
    private String currentAction;
    private String currentPlayerID;
    private Player player;
    private String userID;
    private String oldPlayerID;
    private boolean newPlayer;


    @Inject
    public StateService(GameMemberService gameMemberService, UserService userService, BuildService buildService,
                        PioneerService pioneerService){
        this.gameMemberService = gameMemberService;
        this.userService = userService;
        this.buildService = buildService;
        this.pioneerService = pioneerService;
        this.userID = userService.getCurrentUserID();
        currentPlayerID = pioneerService.getState().blockingFirst().expectedMoves().get(0).players().get(0);
    }

    public void updateState(EventDto<State> state){
        oldPlayerID = currentPlayerID;
        currentState = state.data();
        currentPlayerID = currentState.expectedMoves().get(0).players().get(0);
        currentAction = currentState.expectedMoves().get(0).action();
        newPlayer = !currentPlayerID.equals(oldPlayerID);

        player = pioneerService.getPlayer(userID).blockingFirst();
        buildService.setPlayer(player);

        }

    public String getCurrentAction(){
        return this.currentAction;
    }

    public String getCurrentPlayerID() {
        return currentPlayerID;
    }

    public boolean getNewPlayer() {
        return newPlayer;
    }

    public Player getUpdatedPlayer(){
        return player;
    }

    public String getOldPlayerID(){
        return oldPlayerID;
    }
}
