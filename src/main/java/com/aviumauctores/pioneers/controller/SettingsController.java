package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ResourceBundle;

public class SettingsController implements Controller {

    private final ResourceBundle bundle;

    private final App app;

    private final Provider<LobbyController> lobbyController;


    @Inject
    public SettingsController(ResourceBundle bundle, App app, Provider<LobbyController> lobbyController) {
        this.bundle = bundle;
        this.app = app;
        this.lobbyController = lobbyController;
    }


    public void init() {

    }


    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/SettingsScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        return parent;
    }


    public void destroy(boolean closed) {
        //super.destroy(closed);
    }

    public void toLobby(ActionEvent event) {
        final LobbyController controller = lobbyController.get();
        app.show(controller);
    }
}
