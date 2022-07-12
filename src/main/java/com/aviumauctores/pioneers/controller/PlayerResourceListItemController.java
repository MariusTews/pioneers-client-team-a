package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.service.UserService;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.*;

public class PlayerResourceListItemController {

    private Player player;

    private Member member;

    private ImageView arrowView;

    private final String id;

    private final String name;

    private final String color;

    private final UserService userService;

    private final ResourceBundle bundle;

    //Containers
    private HBox playerBox;

    private Label resourceLabel = new Label();

    private final Label spectatorLabel = new Label();
    private HashMap<String, Integer> resources = new HashMap<>();
    private Tooltip tooltip;
    private Label longestRoadViewLabel;


    public PlayerResourceListItemController(Player player, String name, String color, UserService userService, ResourceBundle bundle) {
        this.player = player;
        this.name = name;
        this.id = player.userId();
        this.color = color;
        this.userService = userService;
        this.bundle = bundle;
        tooltip = new Tooltip(bundle.getString("longest.road"));


    }

    public PlayerResourceListItemController(Member member, String name, String color, UserService userService, ResourceBundle bundle) {
        this.member = member;
        this.name = name;
        this.id = member.userId();
        this.color = color;
        this.userService = userService;
        this.bundle = bundle;
    }

    public HBox createBox() {
        playerBox = new HBox();
        playerBox.setId(id);

        String avatarUrl = userService.getUserByID(player.userId()).blockingFirst().avatar();
        Image playerIcon = avatarUrl == null ? new Image(Objects.requireNonNull(Main.class.getResource("icons/playerIcon_" + color + ".png")).toString()) : new Image(avatarUrl);

        ImageView playerView = new ImageView(playerIcon);
        playerView.setFitHeight(40.0);
        playerView.setFitWidth(40.0);

        Label playerName = new Label(name.length() > 12 ? name.substring(0, 9) + ".." : name);
        playerName.setFont(new Font(18));
        playerName.setStyle("-fx-font-weight: bold");
        playerName.setStyle("-fx-text-fill: " + color);

        Image arrowIcon = new Image(Objects.requireNonNull(Main.class.getResource("icons/arrow_" + color + ".png")).toString());
        arrowView = new ImageView(arrowIcon);
        arrowView.setFitHeight(40.0);
        arrowView.setFitWidth(40.0);
        arrowView.setVisible(false);

        Image longestRoadStar = new Image(Objects.requireNonNull(Main.class.getResource("views/longestRoad.png")).toString());
        ImageView longestRoadView = new ImageView(longestRoadStar);
        longestRoadView.setFitHeight(20.0);
        longestRoadView.setFitWidth(20.0);

        longestRoadViewLabel = new Label();
        longestRoadViewLabel.setGraphic(longestRoadView);
        longestRoadViewLabel.setVisible(false);

        resourceLabel = new Label();
        updateResources();

        HBox nameBox = new HBox();
        nameBox.getChildren().addAll(playerName, longestRoadViewLabel);
        VBox playerInfo = new VBox(nameBox, resourceLabel);

        playerBox.getChildren().addAll(arrowView, playerView, playerInfo);
        playerBox.setSpacing(5.0);
        return playerBox;
    }

    public HBox createSpectatorBox() {
        playerBox = new HBox();
        playerBox.setId(id);

        String avatarUrl = userService.getUserByID(member.userId()).blockingFirst().avatar();
        Image playerIcon = avatarUrl == null ? new Image(Objects.requireNonNull(Main.class.getResource("icons/playerIcon_" + color + ".png")).toString()) : new Image(avatarUrl);

        ImageView playerView = new ImageView(playerIcon);
        playerView.setFitHeight(40.0);
        playerView.setFitWidth(40.0);

        spectatorLabel.setText(bundle.getString("spectator2"));

        Label playerName = new Label(name.length() > 12 ? name.substring(0, 9) + ".." : name);
        playerName.setFont(new Font(18));
        playerName.setStyle("-fx-font-weight: bold");
        playerName.setStyle("-fx-text-fill: " + color);

        Image spectatorImage = new Image(Objects.requireNonNull(Main.class.getResource("views/spectator.png")).toString());
        ImageView spectatorView = new ImageView(spectatorImage);
        spectatorView.setFitHeight(40.0);
        spectatorView.setFitWidth(40.0);

        VBox playerInfo = new VBox(playerName, spectatorLabel);

        playerBox.getChildren().addAll(spectatorView, playerView, playerInfo);
        playerBox.setSpacing(5.0);
        return playerBox;
    }

    public void showArrow() {
        arrowView.setVisible(true);
    }

    public void hideArrow() {
        arrowView.setVisible(false);
    }

    public void updateResources() {
        int num;
        if (id.equals(userService.getCurrentUserID())) {
            num = getResource(RESOURCE_BRICK) + getResource(RESOURCE_ORE) + getResource(RESOURCE_GRAIN)
                    + getResource(RESOURCE_LUMBER) + getResource(RESOURCE_WOOL);
        }
        else {
            num = getResource(RESOURCE_UNKNOWN);
        }
        resourceLabel.setText(num + " " + bundle.getString("resources"));
    }

    public void setPlayer(Player player) {
        this.player = player;
        this.resources = player.resources();
    }

    public int getResource(String resource) {
        return resources.getOrDefault(resource, 0);
    }

    public HBox getPlayerBox() {
        return this.playerBox;
    }

    public void setLongestRoadViewVisible() {
        longestRoadViewLabel.setVisible(true);
        longestRoadViewLabel.setTooltip(tooltip);
    }

    public void setLongestRoadViewInvisible() {
        longestRoadViewLabel.setVisible(false);
        longestRoadViewLabel.setTooltip(null);
    }

}
