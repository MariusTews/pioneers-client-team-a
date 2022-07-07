package com.aviumauctores.pioneers.service;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import javax.inject.Inject;
import java.util.HashMap;

import static com.aviumauctores.pioneers.Constants.*;
import static com.aviumauctores.pioneers.Constants.RESOURCE_GRAIN;


public class TradeService {

    @Inject
    public TradeService() {
    }

    public HashMap<String, Integer> getSpinnerValues(Spinner<Integer> tradeLumber, Spinner<Integer> requestLumber, Spinner<Integer> tradeWool, Spinner<Integer> requestWool, Spinner<Integer> tradeOre, Spinner<Integer> requestOre, Spinner<Integer> tradeGrain, Spinner<Integer> requestGrain, Spinner<Integer> tradeBrick, Spinner<Integer> requestBrick) {
        HashMap<String, Integer> resources = new HashMap<>();
        if (tradeLumber.getValue() != 0) {
            resources.put(RESOURCE_LUMBER, -tradeLumber.getValue());
        }
        if (tradeOre.getValue() != 0) {
            resources.put(RESOURCE_ORE, -tradeOre.getValue());
        }
        if (tradeWool.getValue() != 0) {
            resources.put(RESOURCE_WOOL, -tradeWool.getValue());
        }
        if (tradeBrick.getValue() != 0) {
            resources.put(RESOURCE_BRICK, -tradeBrick.getValue());
        }
        if (tradeGrain.getValue() != 0) {
            resources.put(RESOURCE_GRAIN, -tradeGrain.getValue());
        }
        if (requestLumber.getValue() != 0) {
            resources.put(RESOURCE_LUMBER, requestLumber.getValue());
        }
        if (requestOre.getValue() != 0) {
            resources.put(RESOURCE_ORE, requestOre.getValue());
        }
        if (requestWool.getValue() != 0) {
            resources.put(RESOURCE_WOOL, requestWool.getValue());
        }
        if (requestBrick.getValue() != 0) {
            resources.put(RESOURCE_BRICK, requestBrick.getValue());
        }
        if (requestGrain.getValue() != 0) {
            resources.put(RESOURCE_GRAIN, requestGrain.getValue());
        }
        return resources;
    }

    public SpinnerValueFactory<Integer> createValueFactory(int maxValue) {
        return new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxValue);
    }
}
