package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.service.UserService;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.Objects;

public class PlayerRequestsItemController implements Controller {

    private final Player player;
    private final String name;
    private final UserService userService;
    private final String id;
    private final String color;
    private ImageView requestView;

    public PlayerRequestsItemController(Player player, String name, String color, UserService userService) {
        this.player = player;
        this.id = player.userId();
        this.name = name;
        this.color = color;
        this.userService = userService;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy(boolean closed) {

    }

    @Override
    public Parent render() {
        return null;
    }

    public HBox createBox() {
        HBox playerBox = new HBox();
        playerBox.setId(id);

        String avatarUrl = userService.getUserByID(player.userId()).blockingFirst().avatar();
        Image playerIcon = avatarUrl == null ? new Image(Objects.requireNonNull(Main.class.getResource("icons/playerIcon_" + color + ".png")).toString()) : new Image(avatarUrl);
        ImageView playerView = new ImageView(playerIcon);
        playerView.setFitHeight(40.0);
        playerView.setFitWidth(40.0);

        Image requestStatus = new Image(Objects.requireNonNull(Main.class.getResource("views/Loading.png")).toString());
        requestView = new ImageView(requestStatus);
        requestView.setFitHeight(40.0);
        requestView.setFitWidth(40.0);
        requestView.setVisible(false);

        Label playerName = new Label(name.length() > 12 ? name.substring(0, 9) + ".." : name);
        playerName.setFont(new Font(18));
        playerName.setStyle("-fx-font-weight: bold");
        playerName.setStyle("-fx-text-fill: " + color);
        VBox playerInfo = new VBox(playerName);


        playerBox.getChildren().addAll(requestView, playerView, playerInfo);
        playerBox.setSpacing(5.0);
        return playerBox;
    }

    public void showRequestOpen() {
        requestView.setVisible(true);
    }

    public void showRequestDeclined() {
        Image requestStatus = new Image(Objects.requireNonNull(Main.class.getResource("views/notReady.png")).toString());
        requestView.setImage(requestStatus);
    }

    public void showRequestAccepted() {
        Image requestStatus = new Image(Objects.requireNonNull(Main.class.getResource("views/ready.png")).toString());
        requestView.setImage(requestStatus);
    }
}
