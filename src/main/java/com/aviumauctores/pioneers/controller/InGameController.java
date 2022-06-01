package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ResourceBundle;

public class InGameController extends LoggedInController{
    private final App app;
    private final ResourceBundle bundle;

    @FXML public Label numSheepLabel;
    public Label numWoodLabel;
    public Label numBricksLabel;
    public Label numOreLabel;
    public Label numWheatLabel;
    public Button finishMoveButton;
    public Button rollButton;
    public Button leaveGameButton;
    public Label lastRollPlayerLabel;
    public Label lastRollLabel;

    @Inject
    public InGameController(App app, UserService userService, ResourceBundle bundle) {
        super(userService);
        this.app = app;
        this.bundle = bundle;
    }

    @Override
    public void init() {
        disposables = new CompositeDisposable();
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/ingameScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return parent;
    }

    public void finishMove(ActionEvent actionEvent) {

    }

    public void rollDice(ActionEvent actionEvent) {

    }

    public void leaveGame(ActionEvent actionEvent) {

    }
}
