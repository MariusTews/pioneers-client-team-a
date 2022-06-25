package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.ExpectedMove;
import com.aviumauctores.pioneers.model.Move;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.model.State;

import javax.inject.Inject;
import java.util.List;

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
        player = pioneerService.getPlayer(userID).blockingFirst();
        oldAction = currentAction;
        oldPlayerID = currentPlayerID;
        State currentState = state.data();
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

    public String getOldAction() {
        return oldAction;
    }
}
