package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.model.User;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class PlayerListItemController implements Controller {
    private HBox root;
    private ImageView avatarView;
    private Label playerName;

    private final PlayerListController parentController;
    private User user;
    private final ObservableList<Parent> playerItems;

    public PlayerListItemController(PlayerListController parentController, User user, ObservableList<Parent> playerItems) {
        this.parentController = parentController;
        this.user = user;
        this.playerItems = playerItems;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {
        playerItems.remove(root);
    }

    @Override
    public Parent render() {
        avatarView = new ImageView();
        avatarView.setFitWidth(40.0);
        avatarView.setFitHeight(40.0);
        String avatarUrl = user.avatar();
        Image avatar = avatarUrl == null ? null : new Image(avatarUrl);
        avatarView.setImage(avatar);
        playerName = new Label(user.name());
        root = new HBox(10.0, avatarView, playerName);
        root.setAlignment(Pos.CENTER_LEFT);
        return root;
    }

    public void onPlayerUpdated(User newUser) {
        user = newUser;
        String avatarUrl = newUser.avatar();
        Image newAvatar = avatarUrl == null ? null : new Image(avatarUrl);
        avatarView.setImage(newAvatar);
        playerName.setText(newUser.name());
    }
}
