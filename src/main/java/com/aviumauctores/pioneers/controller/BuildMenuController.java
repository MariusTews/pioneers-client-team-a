package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Constants;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.BuildService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.ResourceBundle;

public class BuildMenuController implements Controller {
    private final BuildService buildService;
    private final ResourceBundle bundle;
    private final String buildingType;

    private final Boolean enableButton;

    @FXML
    private Button buildButton;


    public BuildMenuController(Boolean enableButton, BuildService buildService, ResourceBundle bundle, String buildingType) {
        this.buildService = buildService;
        this.bundle = bundle;
        this.buildingType = buildingType;
        this.enableButton = enableButton;

    }

    @Override
    public void init() {

    }

    @Override
    public void destroy(boolean closed) {

    }

    @Override
    public Parent render() {
        // Capitalize the building type string
        final String capitalizedBuildingType = buildingType.substring(0, 1).toUpperCase() + buildingType.substring(1);
        final FXMLLoader loader =
                new FXMLLoader(Main.class.getResource("views/buildMenu" + capitalizedBuildingType + ".fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        buildButton.setDisable(!enableButton);

        return parent;
    }


    public void build(ActionEvent actionEvent) {
        buildService.setCurrentAction("build");
        buildService.build();
        buildButton.setDisable(true);
    }
}
