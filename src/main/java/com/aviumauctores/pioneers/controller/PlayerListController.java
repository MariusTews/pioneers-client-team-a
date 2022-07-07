package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;

import java.util.HashMap;
import java.util.Map;

public abstract class PlayerListController extends LoggedInController {
    protected final ObservableList<Parent> playerItems = FXCollections.observableArrayList();
    protected final Map<String, PlayerListItemController> playerListItemControllers = new HashMap<>();

    protected PlayerListController(LoginService loginService, UserService userService) {
        super(loginService, userService);
    }

    protected void onUserEvent(EventDto<User> eventDto) {
        String event = eventDto.event();
        User user = eventDto.data();
        boolean isOnline = user.status().equals("online");
        if (event.endsWith("created") && isOnline) {
            addPlayerToList(user);
        } else {
            PlayerListItemController controller = playerListItemControllers.get(user._id());
            if (event.endsWith("updated")) {
                if (controller != null) {
                    if (isOnline) {
                        controller.onPlayerUpdated(user);
                    } else {
                        // Don't display offline users
                        removePlayerFromList(user._id(), controller);
                    }
                } else if (isOnline) {
                    addPlayerToList(user);
                }
            } else if (event.endsWith("deleted")) {
                if (controller != null) {
                    removePlayerFromList(user._id(), controller);
                }
            }
        }
        updatePlayerLabel();
    }

    protected void addPlayerToList(User user) {
        addPlayerToList(user, null);
    }

    protected void addPlayerToList(User user, Member gameMember) {
        PlayerListItemController controller = new PlayerListItemController(this, user, gameMember, playerItems);
        playerListItemControllers.put(user._id(), controller);
        addPlayerItem(controller);
    }

    protected void addPlayerItem(PlayerListItemController controller) {
        playerItems.add(controller.render());
    }

    protected void addMemberToList(Member gameMember) {
        // Do nothing at default
    }

    protected abstract void updatePlayerLabel();

    protected void removePlayerFromList(String userID, PlayerListItemController controller) {
        controller.destroy(false);
        playerListItemControllers.remove(userID);
    }

    protected void removeMemberFromList(Member member, PlayerListItemController controller) {
        removePlayerFromList(member.userId(), controller);
    }

    public void onPlayerItemClicked(User selectedUser) {
        // Do nothing at default
    }
}
