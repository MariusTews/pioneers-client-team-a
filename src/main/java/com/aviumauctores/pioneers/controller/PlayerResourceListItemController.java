package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.service.UserService;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.*;

public class PlayerResourceListItemController {


    private Player player;

    private ImageView arrowView;

    private final String id;

    private final String name;

    private final String color;

    private final UserService userService;
    private final ResourceBundle bundle;

    //Containers
    private HBox playerBox;



    private Label resourceLabel;


    public PlayerResourceListItemController(Player player, String name, String color, UserService userService, ResourceBundle bundle){
        this.player = player;
        this.name = name;
        this.id = player.userId();
        this.color = color;
        this.userService = userService;
        this.bundle = bundle;
    }

    public HBox createBox(){
        playerBox = new HBox();
        playerBox.setId(id);

        String avatarUrl = userService.getUserByID(player.userId()).blockingFirst().avatar();
        Image playerIcon = avatarUrl == null ? new Image(Objects.requireNonNull(Main.class.getResource("icons/playerIcon_" + color + ".png")).toString()) : new Image(avatarUrl);

        ImageView playerView = new ImageView(playerIcon);
        playerView.setFitHeight(40.0);
        playerView.setFitWidth(40.0);

        Label playerName = new Label(name);
        playerName.setFont(new Font(20));
        playerName.setStyle("-fx-font-weight: bold");
        playerName.setStyle("-fx-text-fill: " + color);

        Image arrowIcon = new Image(Objects.requireNonNull(Main.class.getResource("icons/arrow_" + color + ".png")).toString());
        arrowView = new ImageView(arrowIcon);
        arrowView.setFitHeight(40.0);
        arrowView.setFitWidth(40.0);
        arrowView.setVisible(false);

        this.resourceLabel = new Label();
        updateResources();
        VBox playerInfo = new VBox(playerName, resourceLabel);


        playerBox.getChildren().addAll(arrowView, playerView, playerInfo);
        playerBox.setSpacing(10.0);
        return playerBox;
    }





    public void showArrow(){
        arrowView.setVisible(true);
    }

    public void hideArrow(){
        arrowView.setVisible(false);
    }

    public void updateResources() {
        HashMap<String, Integer> resources = player.resources();
        int num = 0;
        if (resources.containsKey(RESOURCE_LUMBER)) {
            num += resources.get(RESOURCE_LUMBER);
        }
        if (resources.containsKey(RESOURCE_ORE)) {
            num += resources.get(RESOURCE_ORE);
        }
        if (resources.containsKey(RESOURCE_WOOL)){
            num += resources.get(RESOURCE_WOOL);
        }
        if (resources.containsKey(RESOURCE_GRAIN)){
            num += resources.get(RESOURCE_GRAIN);
        }
        if (resources.containsKey(RESOURCE_BRICK)) {
            num += resources.get(RESOURCE_BRICK);
        }
        resourceLabel.setText(Integer.toString(num) + " " + bundle.getString("resources"));
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Label getResourceLabel(){
        return resourceLabel;
    }


}
