package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;

import java.util.HashMap;
import java.util.Map;

public abstract class PlayerListController implements Controller {
    protected final ObservableList<Parent> playerItems = FXCollections.observableArrayList();
    protected final Map<String, PlayerListItemController> playerListItemControllers = new HashMap<>();

    protected void addPlayerToList(User user) {
        PlayerListItemController controller = new PlayerListItemController(this, user, playerItems);
        playerListItemControllers.put(user._id(), controller);
        playerItems.add(controller.render());
    }

    protected abstract void updatePlayerLabel();

    protected void removePlayerFromList(String userID, PlayerListItemController controller) {
        controller.destroy();
        playerListItemControllers.remove(userID);
    }
}
