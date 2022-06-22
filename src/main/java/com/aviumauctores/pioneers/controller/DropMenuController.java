package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.PioneerService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.ResourceBundle;

public class DropMenuController implements Controller{
    private final PioneerService pioneerService;
    private final ResourceBundle bundle;
    public Button dropButton;

    public DropMenuController(PioneerService pioneerService, ResourceBundle bundle) {

        this.pioneerService = pioneerService;
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
        final FXMLLoader loader =
                new FXMLLoader(Main.class.getResource("views/dropMenu.fxml"), bundle);
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


    public void drop(ActionEvent event) {
    }
}
