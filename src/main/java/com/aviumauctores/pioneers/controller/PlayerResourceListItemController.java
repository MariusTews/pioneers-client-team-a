package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.service.UserService;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.util.Objects;

public class PlayerResourceListItemController {


    private Player player;

    private ImageView arrowView;

    private final String id;

    private final String name;

    private final String color;

    private final UserService userService;

    //Containers
    private HBox playerBox;
    private HBox iconContainer;
    private VBox iconBox1;
    private VBox iconBox2;
    private VBox iconBox3;


    //Resource Labels
    private Label brickDisplay = new Label();
    private Label oreDisplay = new Label();
    private Label breadDisplay = new Label();
    private Label sheepDisplay = new Label();
    private Label woodDisplay = new Label();



    public PlayerResourceListItemController(Player player, String name, String color, UserService userService){
        this.player = player;
        this.name = name;
        this.id = player.userId();
        this.color = color;
        this.userService = userService;
    }

    public HBox createBox(){
        playerBox = new HBox();
        playerBox.setId(id);

        String avatarUrl = userService.getUserByID(player.userId()).blockingFirst().avatar();
        Image playerIcon;
        playerIcon = new Image(Objects.requireNonNull(Main.class.getResource(Objects.requireNonNullElseGet(avatarUrl, () -> "icons/playerIcon_" + color + ".png"))).toString());

        ImageView playerView = new ImageView(playerIcon);
        playerView.setFitHeight(40.0);
        playerView.setFitWidth(40.0);

        Label playerName = new Label(name);
        playerName.setFont(new Font(20));
        playerName.setStyle("-fx-font-weight: bold");
        playerName.setStyle("-fx-text-fill: " + color);
        VBox playerInfoBox = new VBox(playerView, playerName);

        Image arrowIcon = new Image(Objects.requireNonNull(Main.class.getResource("icons/arrow_" + color + ".png")).toString());
        arrowView = new ImageView(arrowIcon);
        arrowView.setFitHeight(40.0);
        arrowView.setFitWidth(40.0);
        arrowView.setVisible(false);


        playerBox.getChildren().addAll(arrowView, playerInfoBox, loadResourceIcons());
        playerBox.setSpacing(10.0);
        return playerBox;
    }

    public HBox loadResourceIcons(){
        ImageView woodIcon = loadImage("wood");
        ImageView oreIcon = loadImage("ore");
        ImageView sheepIcon = loadImage("sheep");
        ImageView breadIcon = loadImage("bread");
        ImageView brickIcon = loadImage("brick");
        iconBox1 = new VBox(createContainer(woodIcon, woodDisplay), createContainer(oreIcon, oreDisplay));
        iconBox2 = new VBox(createContainer(sheepIcon, sheepDisplay), createContainer(breadIcon, breadDisplay));
        iconBox3 = new VBox(createContainer(brickIcon, brickDisplay));
        iconContainer = new HBox(iconBox1, iconBox2, iconBox3);
        return iconContainer;

    }

    public ImageView loadImage(String name){
        Image icon = new Image(Objects.requireNonNull(Main.class.getResource(("icons/" + name + "_icon.png")).toString()));
        ImageView iconView = new ImageView(icon);
        iconView.setFitHeight(25.0);
        iconView.setFitWidth(25.0);
        return iconView;

    }

    public HBox createContainer(ImageView view, Label label){
        label.setText("0");
        HBox container = new HBox(view, label);
        container.setSpacing(10.0);
        return container;
    }

    public void showArrow(){
        arrowView.setVisible(true);
    }

    public void hideArrow(){
        arrowView.setVisible(false);
    }

    public void updateResources(Player player){
        woodDisplay.setText(Integer.toString(player.lumber()));
        oreDisplay.setText(Integer.toString(player.ore()));
        sheepDisplay.setText(Integer.toString(player.wool()));
        breadDisplay.setText(Integer.toString(player.grain()));
        brickDisplay.setText(Integer.toString(player.brick()));
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
