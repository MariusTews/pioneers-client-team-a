package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Constants;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.BuildService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ResourceBundle;


public class GameOptionController implements Controller {

    public Pane paneOption;
    @FXML
    private Spinner<Integer> victoryPoints;
    @FXML
    private Spinner<Integer> mapSize;
    @FXML
    private Button cancelButton;
    @FXML
    private Button takeOverButton;

    private final GameReadyController gameReadyController;

    private SpinnerValueFactory<Integer> victoryValue;

    private SpinnerValueFactory<Integer> mapValue;

    public GameOptionController(ResourceBundle bundle, GameReadyController gameReadyController) {
        this.bundle = bundle;
        this.gameReadyController = gameReadyController;
    }

    private final ResourceBundle bundle;

    @Override
    public void init() {

    }

    @Override
    public void destroy(boolean closed) {

    }

    @Override
    public Parent render() {
        //load game option
        final FXMLLoader loader =
                new FXMLLoader(Main.class.getResource("views/gameOptionMenue.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // set default values
        gameReadyController.setMapsizeAndVictorypoints(10, 2);
        // Spinner has maximum and minimum Value
        victoryValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 15, 10);
        this.victoryPoints.setValueFactory(victoryValue);
        mapValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 2);
        this.mapSize.setValueFactory(mapValue);
        gameReadyController.setMapsizeAndVictorypoints(10, 2);

        return parent;
    }

    public void leaveOption(ActionEvent actionEvent) {
        gameReadyController.closeGameOptionMenu(true);
        gameReadyController.paneOption.setVisible(false);

    }

    public void takeOverOption(ActionEvent actionEvent) {
        gameReadyController.setMapsizeAndVictorypoints(victoryValue.getValue(), mapValue.getValue());
        gameReadyController.closeGameOptionMenu(true);
        gameReadyController.paneOption.setVisible(false);

    }


}
