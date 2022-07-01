package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.PioneerService;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private final InGameController inGameController;
    private final PioneerService pioneerService;
    private final ResourceBundle bundle;
    private final HashMap<String, Integer> resources;
    private final Set<String> keys;
    public Button dropButton;
    public Spinner<Integer> woolSpinner;
    public Spinner<Integer> grainSpinner;
    public Spinner<Integer> oreSpinner;
    public Spinner<Integer> brickSpinner;
    public Spinner<Integer> lumberSpinner;
    public final SimpleIntegerProperty currentAmountProperty = new SimpleIntegerProperty();

    ChangeListener<Integer> listener = this::computeAmount;


    public DropMenuController(InGameController inGameController, PioneerService pioneerService, ResourceBundle bundle,
                              HashMap<String, Integer> resources) {
        this.inGameController = inGameController;
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
        woolSpinner.valueProperty().removeListener(listener);
        lumberSpinner.valueProperty().removeListener(listener);
        brickSpinner.valueProperty().removeListener(listener);
        oreSpinner.valueProperty().removeListener(listener);
        grainSpinner.valueProperty().removeListener(listener);
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
            lumberSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, resources.get(RESOURCE_LUMBER)));
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

        woolSpinner.valueProperty().addListener(listener);
        lumberSpinner.valueProperty().addListener(listener);
        brickSpinner.valueProperty().addListener(listener);
        oreSpinner.valueProperty().addListener(listener);
        grainSpinner.valueProperty().addListener(listener);

        int resourceAmount = 0;

        for (String key : keys) {
            resourceAmount += resources.get(key);
        }

        int dropLimit = resourceAmount / 2;

        dropButton.disableProperty().bind(
                Bindings.createBooleanBinding(() ->
                        currentAmountProperty.get() != dropLimit, currentAmountProperty)
        );

        return parent;
    }

    private void computeAmount(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        int currentSelectionAmount = 0;

        if (keys.contains(RESOURCE_WOOL)) {
            currentSelectionAmount += woolSpinner.getValue();
        }
        if (keys.contains(RESOURCE_LUMBER)) {
            currentSelectionAmount += lumberSpinner.getValue();
        }
        if (keys.contains(RESOURCE_BRICK)) {
            currentSelectionAmount += brickSpinner.getValue();
        }
        if (keys.contains(RESOURCE_ORE)) {
            currentSelectionAmount += oreSpinner.getValue();
        }
        if (keys.contains(RESOURCE_GRAIN)) {
            currentSelectionAmount += grainSpinner.getValue();
        }

        currentAmountProperty.set(currentSelectionAmount);
    }


    public void drop(ActionEvent event) {
        HashMap<String, Integer> droppedResources = new HashMap<>();

        if (keys.contains(RESOURCE_WOOL)) {
            droppedResources.put(RESOURCE_WOOL, -((int) woolSpinner.getValue()));
        }
        if (keys.contains(RESOURCE_LUMBER)) {
            droppedResources.put(RESOURCE_LUMBER, -((int) lumberSpinner.getValue()));
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

        //noinspection ResultOfMethodCallIgnored
        pioneerService.createMove(MOVE_DROP, null, droppedResources, null, null)
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> inGameController.closeDropMenu(false), throwable -> {
                });
    }
}
