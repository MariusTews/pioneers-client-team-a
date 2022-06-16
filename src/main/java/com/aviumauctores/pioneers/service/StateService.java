package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.model.State;

import javax.inject.Inject;

public class StateService {
    private final BuildService buildService;
    private final PioneerService pioneerService;

    private String oldAction;
    private String currentAction;
    private String currentPlayerID;
    private Player player;
    private final String userID;
    private String oldPlayerID;
    private boolean newPlayer;

    @Inject
    public StateService(UserService userService, BuildService buildService,
                        PioneerService pioneerService) {
        this.buildService = buildService;
        this.pioneerService = pioneerService;
        this.userID = userService.getCurrentUserID();
        currentPlayerID = pioneerService.getState().blockingFirst().expectedMoves().get(0).players().get(0);
    }

    public void updateState(EventDto<State> state) {
        oldAction = currentAction;
        oldPlayerID = currentPlayerID;
        State currentState = state.data();
        currentPlayerID = currentState.expectedMoves().get(0).players().get(0);
        currentAction = currentState.expectedMoves().get(0).action();
        if (oldPlayerID == null) {
            newPlayer = true;
        } else {
            newPlayer = !currentPlayerID.equals(oldPlayerID);
        }
        player = pioneerService.getPlayer(userID).blockingFirst();
        buildService.setPlayer(player);
    }

    public String getCurrentAction() {
        return this.currentAction;
    }

    public String getCurrentPlayerID() {
        return currentPlayerID;
    }

    public boolean getNewPlayer() {
        return newPlayer;
    }

    public Player getUpdatedPlayer() {
        return player;
    }

    public String getOldPlayerID() {
        return oldPlayerID;
    }

    public String getOldAction(){ return oldAction;}
}
