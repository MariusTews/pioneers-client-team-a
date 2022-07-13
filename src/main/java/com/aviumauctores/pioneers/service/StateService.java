package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.*;

import javax.inject.Inject;

public class StateService {
    private final BuildService buildService;

    private String oldAction;
    private String currentAction;
    private String currentPlayerID;
    private final String userID;
    private String oldPlayerID;
    private boolean newPlayer;
    private Point3D robberPosition;


    @Inject
    public StateService(UserService userService, BuildService buildService,
                        PioneerService pioneerService) {
        this.buildService = buildService;
        this.userID = userService.getCurrentUserID();
        currentPlayerID = pioneerService.getState().blockingFirst().expectedMoves().get(0).players().get(0);
    }

    public void updateState(EventDto<State> state) {
        oldAction = currentAction;
        oldPlayerID = currentPlayerID;
        State currentState = state.data();
        robberPosition = currentState.robber();
        ExpectedMove move = currentState.expectedMoves().get(0);

        //check whether my own player is allowed to take his turn now
        if (move.players().contains(userID)) {
            currentPlayerID = userID;
        } else {
            currentPlayerID = move.players().get(0);
        }

        currentAction = move.action();

        if (oldPlayerID == null) {
            newPlayer = true;
        } else {
            newPlayer = !currentPlayerID.equals(oldPlayerID);
        }

        buildService.setPlayerId(currentPlayerID);
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

    public String getOldPlayerID() {
        return oldPlayerID;
    }

    public String getOldAction() {
        return oldAction;
    }

    public Point3D getRobberPosition() {
        return robberPosition;
    }
}
