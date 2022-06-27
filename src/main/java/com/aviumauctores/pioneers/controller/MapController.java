package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Map;
import com.aviumauctores.pioneers.model.Tile;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.*;

public class MapController implements Controller {

    private InGameController inGameController;
    private final ResourceBundle bundle;
    @FXML
    public Pane mainPane;
    @FXML
    public Pane tilePane;
    @FXML
    public Pane tileLabelPane;
    @FXML
    public Pane robberPane;
    @FXML
    public Pane roadAndCrossingPane;
    @FXML
    public Pane crossingPane;
    @FXML
    public Pane roadPane;
    @FXML
    public HBox vpBox;
    @FXML
    public Label timeLabel;
    Image desert;
    Image fields;
    Image hills;
    Image mountains;
    Image forest;
    Image pasture;
    Image emptyCrossing;

    public int mapRadius;
    public Map gameMap;


    @Inject
    public MapController(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public void init() {
        desert = new Image(Objects.requireNonNull(Main.class.getResource("views/tiles/desert.png")).toString());
        fields = new Image(Objects.requireNonNull(Main.class.getResource("views/tiles/wheat.png")).toString());
        hills = new Image(Objects.requireNonNull(Main.class.getResource("views/tiles/brick.png")).toString());
        mountains = new Image(Objects.requireNonNull(Main.class.getResource("views/tiles/ore.png")).toString());
        forest = new Image(Objects.requireNonNull(Main.class.getResource("views/tiles/forest.png")).toString());
        pasture = new Image(Objects.requireNonNull(Main.class.getResource("views/tiles/pasture.png")).toString());
        emptyCrossing = new Image(Objects.requireNonNull(Main.class.getResource("views/buildings/empty.png")).toString());
    }

    @Override
    public void destroy(boolean closed) {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/inGameMap.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (gameMap != null) {
            // calculate parameters
            double factor = (1 + 2 * mapRadius);
            double fitWidthHexagon = WIDTH_HEXAGON / factor;
            double fitHeightHexagon = HEIGHT_HEXAGON / factor;
            double fitSizeCrossing = WIDTH_HEIGHT_BUILDING / factor;
            double middleX = MAIN_PAIN_MIDDLE_X - (fitWidthHexagon / 2);
            double middleY = MAIN_PAIN_MIDDLE_Y - (fitHeightHexagon / 2);
            double offsetCrossing = 0.5 * fitSizeCrossing;

            // creates the tiles
            for (Tile tile : gameMap.tiles()) {
                int posX = tile.x();
                int posY = tile.y();
                int posZ = tile.z();
                double tileX = middleX - (0.75 * posX * fitWidthHexagon) - (0.75 * posY * fitWidthHexagon);
                double tileY = middleY - (0.5 * posX * fitHeightHexagon) + (0.5 * posY * fitHeightHexagon);
                if (posX == 0 && posY == 0) {
                    tileX += posZ * fitWidthHexagon;
                }
                String position = "X" + posX + "Y" + posY + "Z" + posZ;
                position = position.replace("-", "_");
                double offsetMiddleY = tileY + 0.5 * fitHeightHexagon;
                double offsetMiddleX = tileX + 0.5 * fitWidthHexagon;
                createTile(position, tileX, tileY, fitWidthHexagon, fitHeightHexagon, tile);
                createLabel(offsetMiddleX - offsetCrossing, offsetMiddleY - offsetCrossing, "" + tile.numberToken());
                createRobberPosition(position, offsetMiddleX - offsetCrossing, offsetMiddleY - offsetCrossing, fitSizeCrossing);
                createCrossing(position + "R0", tileX - offsetCrossing, offsetMiddleY - offsetCrossing, fitSizeCrossing);
                createCrossing(position + "R6", tileX + fitWidthHexagon - offsetCrossing, offsetMiddleY - offsetCrossing, fitSizeCrossing);
            }
        }
        return parent;
    }

    public void createTile(String position, double coordinateX, double coordinateY, double sizeX, double sizeY, Tile tile) {
        ImageView imageView = new ImageView();
        imageView.setId("hexagon" + position);
        imageView.setFitHeight(sizeY);
        imageView.setFitWidth(sizeX);
        switch (tile.type()) {
            case "desert" -> imageView.setImage(desert);
            case "fields" -> imageView.setImage(fields);
            case "hills" -> imageView.setImage(hills);
            case "mountains" -> imageView.setImage(mountains);
            case "forest" -> imageView.setImage(forest);
            case "pasture" -> imageView.setImage(pasture);
        }
        if (tilePane.getChildren() != null) {
            tilePane.getChildren().add(imageView);
        }
        imageView.setX(coordinateX);
        imageView.setY(coordinateY);
    }

    public void createCrossing(String position, double coordinateX, double coordinateY, double size) {
        Circle circle = new Circle();
        circle.setFill(Color.TRANSPARENT);
        circle.setId("building" + position + "Colour");
        circle.setRadius(size / 2);
        circle.setLayoutX(coordinateX);
        circle.setLayoutY(coordinateY);
        crossingPane.getChildren().add(circle);
        ImageView imageView = new ImageView();
        imageView.setId("building" + position);
        imageView.setFitHeight(size);
        imageView.setFitWidth(size);
        imageView.setImage(emptyCrossing);
        imageView.setX(coordinateX);
        imageView.setY(coordinateY);
        imageView.setOnMouseClicked(this::onFieldClicked);
        crossingPane.getChildren().add(imageView);
    }

    public void createLabel(double coordinateX, double coordinateY, String text) {
        Label label = new Label();
        label.setText(text);
        label.setStyle("-fx-background-color: #f0f0f0; -fx-font-size: 2em; -fx-text-fill: #00000f");
        label.setLayoutX(coordinateX);
        label.setLayoutY(coordinateY);
        tileLabelPane.getChildren().add(label);
    }

    public void createRobberPosition(String position, double coordinateX, double coordinateY, double size) {
        ImageView imageView = new ImageView();
        imageView.setId("robber" + position);
        imageView.setFitHeight(size);
        imageView.setFitWidth(size);
        imageView.setImage(emptyCrossing);
        imageView.setX(coordinateX);
        imageView.setY(coordinateY);
        robberPane.getChildren().add(imageView);
    }

    public void createRoad(String position, double coordinateX, double coordinateY, double sizeX, double sizeY) {

    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    public void setMapRadius(int mapRadius) {
        this.mapRadius = mapRadius;
    }

    public Pane getMainPane() {
        return mainPane;
    }

    public Pane getRoadAndCrossingPane() {
        return roadAndCrossingPane;
    }

    public Pane getCrossingPane() {
        return crossingPane;
    }

    public Pane getRoadPane() {
        return roadPane;
    }

    public void onMainPaneClicked(MouseEvent mouseEvent) {
        inGameController.onMainPaneClicked(mouseEvent);
    }

    public void setGameMap(Map gameMap) {
        this.gameMap = gameMap;
    }

    public Label getTimeLabel() {
        return timeLabel;
    }

    public void onFieldClicked(MouseEvent mouseEvent) {
        inGameController.onFieldClicked(mouseEvent);
    }
}
