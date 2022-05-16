package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.model.User;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class PlayerListItemController implements Controller {
    public static final String READY_SRC = "views/ready.png";
    public static final String NOT_READY_SRC = "views/notReady.png";

    private HBox root;
    private ImageView avatarView;
    private Label playerName;
    private ImageView readyView;

    private final PlayerListController parentController;

    private User user;
    private Member gameMember;

    private final ObservableList<Parent> playerItems;

    public PlayerListItemController(PlayerListController parentController, User user, ObservableList<Parent> playerItems) {
        this(parentController, user, null, playerItems);
    }

    public PlayerListItemController(PlayerListController parentController, User user, Member gameMember, ObservableList<Parent> playerItems) {
        this.parentController = parentController;
        this.user = user;
        this.gameMember = gameMember;
        this.playerItems = playerItems;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {
        playerItems.remove(root);
    }

    private Image createReadyImg() {
        String src = gameMember.ready() ? READY_SRC : NOT_READY_SRC;
        return new Image(Objects.requireNonNull(Main.class.getResourceAsStream(src)));
    }

    @Override
    public Parent render() {
        avatarView = new ImageView();
        avatarView.setFitWidth(40.0);
        avatarView.setFitHeight(40.0);
        String avatarUrl = user.avatar();
        Image avatar = avatarUrl == null ? null : new Image(avatarUrl);
        avatarView.setImage(avatar);
        if (gameMember != null) {
            readyView = new ImageView(createReadyImg());
        }
        playerName = new Label(user.name(), readyView);
        root = new HBox(10.0, avatarView, playerName);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setOnMouseClicked(this::onItemClicked);
        return root;
    }

    private void onItemClicked(MouseEvent event) {
        if (!event.getButton().equals(MouseButton.PRIMARY) || event.getClickCount() < 2) {
            // only listen to double-clicks on primary button
            return;
        }
        parentController.onPlayerItemClicked(user);
    }

    public void onPlayerUpdated(User newUser) {
        user = newUser;
        String avatarUrl = newUser.avatar();
        Image newAvatar = avatarUrl == null ? null : new Image(avatarUrl);
        avatarView.setImage(newAvatar);
        playerName.setText(newUser.name());
    }

    public void onGameMemberUpdated(Member newGameMember) {
        gameMember = newGameMember;
        readyView.setImage(createReadyImg());
    }
}
