package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Constants;
import com.aviumauctores.pioneers.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

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
    public Pane roadAndCrossingPane;
    @FXML
    public Pane crossingPane;
    @FXML
    public Pane roadPane;
    @FXML
    public HBox vpBox;
    Image desert;
    Image fields;
    Image hills;
    Image mountains;
    Image forest;
    Image pasture;

    public int mapRadius;

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
        double fitWidthHexagon = WIDTH_HEXAGON / (1 + 2 * mapRadius);
        double fitHeightHexagon = HEIGHT_HEXAGON / (1 + 2 * mapRadius);
        double middleX = MAIN_PAIN_MIDDLE_X - (fitWidthHexagon / 2);
        double middleY = MAIN_PAIN_MIDDLE_Y - (fitHeightHexagon / 2);
        return parent;
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
}
