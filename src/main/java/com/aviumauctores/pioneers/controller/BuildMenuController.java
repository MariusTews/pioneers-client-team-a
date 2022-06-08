package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.ResourceBundle;

public class BuildMenuController implements Controller {
    private final ResourceBundle bundle;
    private final String buildingType;

    public BuildMenuController(ResourceBundle bundle, String buildingType) {
        this.bundle = bundle;
        this.buildingType = buildingType;
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
        return parent;
    }
}
