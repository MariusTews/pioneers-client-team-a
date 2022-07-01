package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Harbor;
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
    Image emptyRoad;
    Image harborImage;

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
        emptyRoad = new Image(Objects.requireNonNull(Main.class.getResource("views/buildings/emptyRoad.png")).toString());
        harborImage = new Image(Objects.requireNonNull(Main.class.getResource("views/harbor.png")).toString());
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
            double fitWidthRoad = WIDTH_ROAD / factor;
            double fitHeightRoad = HEIGHT_ROAD / factor;
            double offsetCrossing = 0.5 * fitSizeCrossing;
            double offsetWidthRoad = 0.5 * fitWidthRoad;
            double offsetHeightRoad = 0.5 * fitHeightRoad;

            // creates the tiles
            for (Tile tile : gameMap.tiles()) {
                int posX = tile.x();
                int posY = tile.y();
                int posZ = tile.z();

                // tile-coordinates
                double tileX = middleX - (0.75 * posX * fitWidthHexagon) - (0.75 * posY * fitWidthHexagon);
                double tileY = middleY - (0.5 * posX * fitHeightHexagon) + (0.5 * posY * fitHeightHexagon);
                if (posX == 0 && posY == 0) {
                    tileX += posZ * fitWidthHexagon;
                }

                // position in grid
                String position = "X" + posX + "Y" + posY + "Z" + posZ;
                position = position.replace("-", "_");

                // offsets
                double offsetMiddleY = tileY + 0.5 * fitHeightHexagon;
                double offsetMiddleX = tileX + 0.5 * fitWidthHexagon;

                // creation
                createTile(position, tileX, tileY, fitWidthHexagon, fitHeightHexagon, tile);
                createLabel(offsetMiddleX - offsetCrossing, offsetMiddleY - offsetCrossing, "" + tile.numberToken());
                createRobberPosition(position, offsetMiddleX - offsetCrossing, offsetMiddleY - offsetCrossing, fitSizeCrossing);
                createCrossing(position + "R0", tileX - offsetCrossing, offsetMiddleY - offsetCrossing, fitSizeCrossing);
                createCrossing(position + "R6", tileX + fitWidthHexagon - offsetCrossing, offsetMiddleY - offsetCrossing, fitSizeCrossing);
                createRoad(position + "R3", offsetMiddleX - offsetWidthRoad, tileY - offsetHeightRoad, fitWidthRoad, fitHeightRoad, 0.0);
                createRoad(position + "R7", tileX + 0.75 * fitWidthHexagon, tileY + fitHeightHexagon - 3 * fitHeightRoad, fitWidthRoad, fitHeightRoad, -60.0);
                createRoad(position + "R11", tileX, offsetMiddleY + 2 * fitHeightRoad, fitWidthRoad, fitHeightRoad, 60.0);
            }

            // add remaining crossings and roads
            // top left (1 o clock)
            int radius = mapRadius + 1;
            for (int yIterator = 0; yIterator > -1 * radius; yIterator--) {
                double tileX = middleX - (0.75 * radius * fitWidthHexagon) - (0.75 * yIterator * fitWidthHexagon);
                double tileY = middleY - (0.5 * radius * fitHeightHexagon) + (0.5 * yIterator * fitHeightHexagon);
                String position = "X" + radius + "Y" + yIterator + "Z" + -1 * (radius + yIterator);
                position = position.replace("-", "_");
                createCrossing(position + "R6", tileX + fitWidthHexagon - offsetCrossing, (tileY + 0.5 * fitHeightHexagon) - offsetCrossing, fitSizeCrossing);
                createRoad(position + "R7", tileX + 0.75 * fitWidthHexagon, tileY + fitHeightHexagon - 3 * fitHeightRoad, fitWidthRoad, fitHeightRoad, -60.0);
            }
            // bottom left (11 o clock)
            for (int xIterator = 0; xIterator >= -1 * radius; xIterator--) {
                double tileX = middleX - (0.75 * xIterator * fitWidthHexagon) - (0.75 * radius * fitWidthHexagon);
                double tileY = middleY - (0.5 * xIterator * fitHeightHexagon) + (0.5 * radius * fitHeightHexagon);
                String position = "X" + xIterator + "Y" + radius + "Z" + -1 * (radius + xIterator);
                position = position.replace("-", "_");
                if (xIterator != -1 * radius) {
                    createCrossing(position + "R6", tileX + fitWidthHexagon - offsetCrossing, (tileY + 0.5 * fitHeightHexagon) - offsetCrossing, fitSizeCrossing);
                }
                if (xIterator != 0) {
                    createRoad(position + "R3", (tileX + 0.5 * fitWidthHexagon) - offsetWidthRoad, tileY - offsetHeightRoad, fitWidthRoad, fitHeightRoad, 0.0);
                }
            }
            // top right (5 o clock)
            radius = -1 * radius;
            for (int xIterator = 0; xIterator < -1 * radius; xIterator++) {
                double tileX = middleX - (0.75 * xIterator * fitWidthHexagon) - (0.75 * radius * fitWidthHexagon);
                double tileY = middleY - (0.5 * xIterator * fitHeightHexagon) + (0.5 * radius * fitHeightHexagon);
                String position = "X" + xIterator + "Y" + radius + "Z" + -1 * (radius + xIterator);
                position = position.replace("-", "_");
                createCrossing(position + "R0", tileX - offsetCrossing, (tileY + 0.5 * fitHeightHexagon) - offsetCrossing, fitSizeCrossing);
                createRoad(position + "R11", tileX, (tileY + 0.5 * fitHeightHexagon) + 2 * fitHeightRoad, fitWidthRoad, fitHeightRoad, 60.0);
            }
            // bottom right (7 o clock)
            for (int yIterator = 0; yIterator < -1 * radius; yIterator++) {
                double tileX = middleX - (0.75 * radius * fitWidthHexagon) - (0.75 * yIterator * fitWidthHexagon);
                double tileY = middleY - (0.5 * radius * fitHeightHexagon) + (0.5 * yIterator * fitHeightHexagon);
                String position = "X" + radius + "Y" + yIterator + "Z" + (radius + yIterator);
                position = position.replace("-", "_");
                createCrossing(position + "R0", tileX - offsetCrossing, (tileY + 0.5 * fitHeightHexagon) - offsetCrossing, fitSizeCrossing);
                if (yIterator != 0) {
                    createRoad(position + "R3", (tileX + 0.5 * fitWidthHexagon) - offsetWidthRoad, tileY - offsetHeightRoad, fitWidthRoad, fitHeightRoad, 0.0);
                }
            }
            // middle left (0 0 clock)
            for (int xIterator = mapRadius; xIterator >= 1; xIterator--) {
                int yIterator = mapRadius + 1 - xIterator;
                double tileX = middleX - (0.75 * xIterator * fitWidthHexagon) - (0.75 * yIterator * fitWidthHexagon);
                double tileY = middleY - (0.5 * xIterator * fitHeightHexagon) + (0.5 * yIterator * fitHeightHexagon);
                String position = "X" + xIterator + "Y" + yIterator + "Z" + -1 * (xIterator + yIterator);
                position = position.replace("-", "_");
                createCrossing(position + "R6", tileX + fitWidthHexagon - offsetCrossing, (tileY + 0.5 * fitHeightHexagon) - offsetCrossing, fitSizeCrossing);
                createRoad(position + "R7", tileX + 0.75 * fitWidthHexagon, tileY + fitHeightHexagon - 3 * fitHeightRoad, fitWidthRoad, fitHeightRoad, -60.0);
            }
            // middle right (6 o clock)
            for (int xIterator = -1 * mapRadius; xIterator <= -1; xIterator++) {
                int yIterator = -1 * mapRadius - 1 - xIterator;
                double tileX = middleX - (0.75 * xIterator * fitWidthHexagon) - (0.75 * yIterator * fitWidthHexagon);
                double tileY = middleY - (0.5 * xIterator * fitHeightHexagon) + (0.5 * yIterator * fitHeightHexagon);
                String position = "X" + xIterator + "Y" + yIterator + "Z" + -1 * (xIterator + yIterator);
                position = position.replace("-", "_");
                createCrossing(position + "R0", tileX - offsetCrossing, (tileY + 0.5 * fitHeightHexagon) - offsetCrossing, fitSizeCrossing);
                createRoad(position + "R11", tileX, (tileY + 0.5 * fitHeightHexagon) + 2 * fitHeightRoad, fitWidthRoad, fitHeightRoad, 60.0);
            }

            // generate harbors
            for (Harbor harbor : gameMap.harbors()) {
                double harborX = middleX - (0.75 * harbor.x() * fitWidthHexagon) - (0.75 * harbor.y() * fitWidthHexagon);
                double harborY = middleY - (0.5 * harbor.x() * fitHeightHexagon) + (0.5 * harbor.y() * fitHeightHexagon);
                ImageView imageView = new ImageView(harborImage);
                imageView.setFitHeight(2 * fitSizeCrossing);
                imageView.setFitWidth(2 * fitSizeCrossing);
                switch (harbor.side()) {
                    case 1 -> {
                        harborX -= fitSizeCrossing;
                        createLabel(harborX - 3 * fitSizeCrossing, harborY - 2 * fitSizeCrossing, harbor.type());
                    }
                    case 3 -> {
                        harborX += 0.5 * fitWidthHexagon - fitSizeCrossing;
                        harborY -= 2 * fitSizeCrossing;
                        createLabel(harborX + 2 * fitSizeCrossing, harborY, harbor.type());
                    }
                    case 5 -> {
                        harborX += fitWidthHexagon - fitSizeCrossing;
                        createLabel(harborX + 2 * fitSizeCrossing, harborY, harbor.type());
                    }
                    case 7 -> {
                        harborX += fitWidthHexagon - 1.5 * fitSizeCrossing;
                        harborY += fitHeightHexagon - 0.5 * fitSizeCrossing;
                        createLabel(harborX + 2 * fitSizeCrossing, harborY, harbor.type());
                    }
                    case 9 -> {
                        harborX += 0.5 * fitWidthHexagon - fitSizeCrossing;
                        harborY += fitHeightHexagon;
                        createLabel(harborX, harborY + 2 * fitSizeCrossing, harbor.type());
                    }
                    case 11 -> {
                        harborX -= fitSizeCrossing;
                        harborY += fitHeightHexagon - 1.5 * fitSizeCrossing;
                        createLabel(harborX - 3 * fitSizeCrossing, harborY - fitSizeCrossing, harbor.type());
                    }
                }
                imageView.setX(harborX);
                imageView.setY(harborY);
                tilePane.getChildren().add(imageView);
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
        Circle circle = new Circle(size / 2, Color.TRANSPARENT);
        circle.setId("building" + position + "Colour");
        circle.setLayoutX(coordinateX + 0.5 * size);
        circle.setLayoutY(coordinateY + 0.5 * size);
        crossingPane.getChildren().add(circle);
        ImageView imageView = new ImageView(emptyCrossing);
        imageView.setId("building" + position);
        imageView.setFitHeight(size);
        imageView.setFitWidth(size);
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
        ImageView imageView = new ImageView(emptyCrossing);
        imageView.setId("robber" + position);
        imageView.setFitHeight(size);
        imageView.setFitWidth(size);
        imageView.setX(coordinateX);
        imageView.setY(coordinateY);
        robberPane.getChildren().add(imageView);
    }

    public void createRoad(String position, double coordinateX, double coordinateY, double sizeX, double sizeY, double rotation) {
        Rectangle rectangle = new Rectangle(coordinateX, coordinateY, sizeX, sizeY);
        rectangle.setId("building" + position + "Colour");
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setRotate(rotation);
        roadPane.getChildren().add(rectangle);
        ImageView imageView = new ImageView(emptyRoad);
        imageView.setId("building" + position);
        imageView.setFitWidth(sizeX);
        imageView.setFitHeight(sizeY);
        imageView.setX(coordinateX);
        imageView.setY(coordinateY);
        imageView.setOnMouseClicked(this::onFieldClicked);
        imageView.preserveRatioProperty().setValue(true);
        imageView.setRotate(rotation);
        roadPane.getChildren().add(imageView);
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

    public HBox getVpBox() {
        return vpBox;
    }
}
