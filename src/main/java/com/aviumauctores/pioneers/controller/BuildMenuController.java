package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.ResourceBundle;

public class BuildMenuController implements Controller {
    private final ResourceBundle bundle;

    public BuildMenuController(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy(boolean closed) {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/buildMenu.fxml"), bundle);
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
}
