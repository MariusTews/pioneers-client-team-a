package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.PioneerService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;

import static com.aviumauctores.pioneers.Constants.*;

public class DropMenuController implements Controller {
    private final PioneerService pioneerService;
    private final ResourceBundle bundle;
    private final HashMap<String, Integer> resources;

    private final Set<String> keys;
    public Button dropButton;
    public Spinner<Integer> woolSpinner;
    public Spinner<Integer> grainSpinner;
    public Spinner<Integer> oreSpinner;
    public Spinner<Integer> brickSpinner;
    public Spinner<Integer> woodSpinner;


    public DropMenuController(PioneerService pioneerService, ResourceBundle bundle, HashMap<String, Integer> resources) {
        this.pioneerService = pioneerService;
        this.bundle = bundle;
        this.resources = resources;
        keys = resources.keySet();
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

        if (keys.contains(RESOURCE_WOOL)) {
            woolSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, resources.get(RESOURCE_WOOL)));
        }
        if (keys.contains(RESOURCE_LUMBER)) {
            woodSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, resources.get(RESOURCE_LUMBER)));
        }
        if (keys.contains(RESOURCE_BRICK)) {
            brickSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, resources.get(RESOURCE_BRICK)));
        }
        if (keys.contains(RESOURCE_ORE)) {
            oreSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, resources.get(RESOURCE_ORE)));
        }
        if (keys.contains(RESOURCE_GRAIN)) {
            grainSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, resources.get(RESOURCE_GRAIN)));
        }

        return parent;
    }


    public void drop(ActionEvent event) {
        HashMap<String, Integer> droppedResources = new HashMap<>();

        if (keys.contains(RESOURCE_WOOL)) {
            droppedResources.put(RESOURCE_WOOL, -((int) woolSpinner.getValue()));
        }
        if (keys.contains(RESOURCE_LUMBER)) {
            droppedResources.put(RESOURCE_LUMBER, -((int) woodSpinner.getValue()));
        }
        if (keys.contains(RESOURCE_BRICK)) {
            droppedResources.put(RESOURCE_BRICK, -((int) brickSpinner.getValue()));
        }
        if (keys.contains(RESOURCE_ORE)) {
            droppedResources.put(RESOURCE_ORE, -((int) oreSpinner.getValue()));
        }
        if (keys.contains(RESOURCE_GRAIN)) {
            droppedResources.put(RESOURCE_GRAIN, -((int) grainSpinner.getValue()));
        }

        pioneerService.createMove(MOVE_DROP, null, droppedResources, null, null)
                .observeOn(FX_SCHEDULER)
                .subscribe();
    }
}
