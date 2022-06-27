package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.service.ColorService;
import com.aviumauctores.pioneers.service.ErrorService;
import com.aviumauctores.pioneers.service.PioneerService;
import com.aviumauctores.pioneers.service.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.*;

public class TradingController implements Controller {
    private final InGameController inGameController;
    private final ResourceBundle bundle;
    @FXML
    public Spinner<Integer> tradeWood;
    @FXML
    public Spinner<Integer> requestWood;
    @FXML
    public Spinner<Integer> tradeBread;
    @FXML
    public Spinner<Integer> requestBread;
    @FXML
    public Spinner<Integer> tradeClay;
    @FXML
    public Spinner<Integer> requestClay;
    @FXML
    public Spinner<Integer> tradeStone;
    @FXML
    public Spinner<Integer> requestStone;
    @FXML
    public Spinner<Integer> tradeWool;
    @FXML
    public Spinner<Integer> requestWool;
    public Label woodLabel;
    public Label breadLabel;
    public Label clayLabel;
    public Label stoneLabel;
    public Label woolLabel;
    public Button cancelTradeButton;
    public ListView<HBox> requestList;
    public Button tradeButton;
    public VBox bankWool;
    public VBox bankStone;
    public VBox bankClay;
    public VBox bankBread;
    public VBox bankWood;
    private String userID;
    private final UserService userService;
    private final PioneerService pioneerService;
    private final ColorService colorService;
    private final ErrorService errorService;
    private final HashMap<ChangeListener<Integer>, Spinner<Integer>> listenerSpinnerHashMap;

    private CompositeDisposable disposables;

    private PlayerRequestsListController playerRequestsController;

    private boolean bankTrade;
    private int sumRequest;

    @Inject
    public TradingController(InGameController inGameController, ResourceBundle bundle, UserService userService,
                             PioneerService pioneerService, ColorService colorService, ErrorService errorService) {
        this.inGameController = inGameController;
        this.bundle = bundle;
        this.userService = userService;
        this.pioneerService = pioneerService;
        this.colorService = colorService;
        this.errorService = errorService;
        this.listenerSpinnerHashMap = new HashMap<>();
        this.sumRequest = 0;
    }

    @Override
    public void init() {
        userID = userService.getCurrentUserID();
        disposables = new CompositeDisposable();
    }

    @Override
    public void destroy(boolean closed) {
        if (playerRequestsController != null) {
            playerRequestsController.destroy(closed);
            playerRequestsController = null;
        }
        if (disposables != null) {
            disposables.dispose();
        }
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/tradingScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        this.initSpinners();
        playerRequestsController = new PlayerRequestsListController(this, pioneerService, userService, colorService);
        this.playerRequestsController.load(requestList, userID);
        return parent;
    }


    public void cancel(ActionEvent actionEvent) {
        inGameController.closeTradingMenu(false);
    }

    //trade with the bank
    public void tradeWithBank() {
        HashMap<String, Integer> resources = this.getSpinnerValues();
        this.sendBankTrade(resources);
    }

    private void sendBankTrade(HashMap<String, Integer> resources) {
        errorService.setErrorCodesTradeController();
        disposables.add(pioneerService.createMove("build", null, "684072366f72202b72406465", null, resources)
                .observeOn(FX_SCHEDULER).
                subscribe(move -> {
                }, errorService::handleError));
    }

    //send trade to bank or player
    public void enterTrade(ActionEvent actionEvent) {
        if (this.isBankTrade()) {
            this.tradeWithBank();
        } else {
            this.tradeWithPlayer();
        }
    }

    //trade with a player
    private void tradeWithPlayer() {
        errorService.setErrorCodesTradeController();
        String playerName = playerRequestsController.getSelectedPlayer();
        if (playerName != null) {
            Player selectedPlayer = null;
            for (Player p : pioneerService.listPlayers().blockingFirst()) {
                String getPlayer = userService.getUserName(p.userId()).blockingFirst();
                if (Objects.equals(playerName, getPlayer)) {
                    selectedPlayer = p;
                }
            }
        }
    }

    //change spinners, when user wants to trade with the bank

    private void setupBankTrade() {
        this.removeListeners();
        this.setRequestSpinnersReady();
        this.setTradeSpinnersReady();
        this.setBankTrade(true);
        this.setRequestSpinnersZero();
        this.setSumRequest(0);
        this.setListeners();
    }

    public void enterWoodVariables(MouseEvent mouseEvent) {
        this.setupBankTrade();
        requestWood.setDisable(true);
        String totrade = woodLabel.getText();
        if (totrade.charAt(0) == '4') {
            tradeWood.getValueFactory().setValue(4);
            this.setTradeSpinnersZeroAndDisable(tradeWool, tradeStone, tradeClay, tradeBread);
        } else if (totrade.charAt(0) == '3') {
            tradeWood.getValueFactory().setValue(3);
            this.setTradeSpinnersZeroAndDisable(tradeWool, tradeStone, tradeClay, tradeBread);
        } else {
            tradeWood.getValueFactory().setValue(2);
            this.setTradeSpinnersZeroAndDisable(tradeWool, tradeStone, tradeClay, tradeBread);
        }
    }

    public void enterBreadVariables(MouseEvent mouseEvent) {
        this.setupBankTrade();
        requestBread.setDisable(true);
        String totrade = breadLabel.getText();
        if (totrade.charAt(0) == '4') {
            tradeBread.getValueFactory().setValue(4);
            this.setTradeSpinnersZeroAndDisable(tradeWood, tradeWool, tradeStone, tradeClay);
        } else if (totrade.charAt(0) == '3') {
            tradeBread.getValueFactory().setValue(3);
            this.setTradeSpinnersZeroAndDisable(tradeWood, tradeWool, tradeStone, tradeClay);
        } else {
            tradeBread.getValueFactory().setValue(2);
            this.setTradeSpinnersZeroAndDisable(tradeWood, tradeWool, tradeStone, tradeClay);
        }
    }

    public void enterClayVariables(MouseEvent mouseEvent) {
        this.setupBankTrade();
        requestClay.setDisable(true);
        String totrade = clayLabel.getText();
        if (totrade.charAt(0) == '4') {
            tradeClay.getValueFactory().setValue(4);
            this.setTradeSpinnersZeroAndDisable(tradeWood, tradeWool, tradeStone, tradeBread);
        } else if (totrade.charAt(0) == '3') {
            tradeClay.getValueFactory().setValue(3);
            this.setTradeSpinnersZeroAndDisable(tradeWood, tradeWool, tradeStone, tradeBread);
        } else {
            tradeClay.getValueFactory().setValue(2);
            this.setTradeSpinnersZeroAndDisable(tradeWood, tradeWool, tradeStone, tradeBread);
        }
    }

    public void enterStoneVariables(MouseEvent mouseEvent) {
        this.setupBankTrade();
        requestStone.setDisable(true);
        String totrade = stoneLabel.getText();
        if (totrade.charAt(0) == '4') {
            tradeStone.getValueFactory().setValue(4);
            this.setTradeSpinnersZeroAndDisable(tradeWood, tradeWool, tradeClay, tradeBread);
        } else if (totrade.charAt(0) == '3') {
            tradeStone.getValueFactory().setValue(3);
            this.setTradeSpinnersZeroAndDisable(tradeWood, tradeWool, tradeClay, tradeBread);
        } else {
            tradeStone.getValueFactory().setValue(2);
            this.setTradeSpinnersZeroAndDisable(tradeWood, tradeWool, tradeClay, tradeBread);
        }
    }

    public void enterWoolVariables(MouseEvent mouseEvent) {
        this.setupBankTrade();
        requestWool.setDisable(true);
        String toTrade = woolLabel.getText();
        if (toTrade.charAt(0) == '4') {
            tradeWool.getValueFactory().setValue(4);
            this.setTradeSpinnersZeroAndDisable(tradeWood, tradeStone, tradeClay, tradeBread);
        } else if (toTrade.charAt(0) == '3') {
            tradeWool.getValueFactory().setValue(3);
            this.setTradeSpinnersZeroAndDisable(tradeWood, tradeStone, tradeClay, tradeBread);
        } else {
            tradeWool.getValueFactory().setValue(2);
            this.setTradeSpinnersZeroAndDisable(tradeWood, tradeStone, tradeClay, tradeBread);
        }
    }


    //setup spinners
    private void initSpinners() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 32);
        valueFactory.setValue(0);
        SpinnerValueFactory<Integer> valueFactory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 32);
        valueFactory1.setValue(0);
        SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 32);
        valueFactory2.setValue(0);
        SpinnerValueFactory<Integer> valueFactory3 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 32);
        valueFactory3.setValue(0);
        SpinnerValueFactory<Integer> valueFactory4 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 32);
        valueFactory4.setValue(0);
        SpinnerValueFactory<Integer> valueFactory5 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 32);
        valueFactory5.setValue(0);
        SpinnerValueFactory<Integer> valueFactory6 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 32);
        valueFactory6.setValue(0);
        SpinnerValueFactory<Integer> valueFactory7 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 32);
        valueFactory7.setValue(0);
        SpinnerValueFactory<Integer> valueFactory8 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 32);
        valueFactory8.setValue(0);
        SpinnerValueFactory<Integer> valueFactory9 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 32);
        valueFactory9.setValue(0);
        tradeWood.setValueFactory(valueFactory);
        requestWood.setValueFactory(valueFactory1);
        tradeBread.setValueFactory(valueFactory2);
        requestBread.setValueFactory(valueFactory3);
        tradeClay.setValueFactory(valueFactory4);
        requestClay.setValueFactory(valueFactory5);
        tradeStone.setValueFactory(valueFactory6);
        requestStone.setValueFactory(valueFactory7);
        tradeWool.setValueFactory(valueFactory8);
        requestWool.setValueFactory(valueFactory9);
    }

    public void setRequestSpinnersReady() {
        requestWood.setDisable(false);
        requestWool.setDisable(false);
        requestStone.setDisable(false);
        requestBread.setDisable(false);
        requestClay.setDisable(false);
    }

    private void setTradeSpinnersZeroAndDisable(Spinner<Integer> spinner, Spinner<Integer> spinner1, Spinner<Integer> spinner2, Spinner<Integer> spinner3) {
        spinner.getValueFactory().setValue(0);
        spinner.setDisable(true);
        spinner1.getValueFactory().setValue(0);
        spinner1.setDisable(true);
        spinner2.getValueFactory().setValue(0);
        spinner2.setDisable(true);
        spinner3.getValueFactory().setValue(0);
        spinner3.setDisable(true);
    }

    private void setRequestSpinnersZero() {
        requestWood.getValueFactory().setValue(0);
        requestStone.getValueFactory().setValue(0);
        requestClay.getValueFactory().setValue(0);
        requestBread.getValueFactory().setValue(0);
        requestWool.getValueFactory().setValue(0);
    }

    public void setTradeSpinnersReady() {
        tradeBread.setDisable(false);
        tradeClay.setDisable(false);
        tradeStone.setDisable(false);
        tradeWool.setDisable(false);
        tradeWood.setDisable(false);
    }

    //change listener for spinners

    private void setListeners() {
        ChangeListener<Integer> listener = this::onChangeListener;
        ChangeListener<Integer> listener1 = this::onChangeListener;
        ChangeListener<Integer> listener2 = this::onChangeListener;
        ChangeListener<Integer> listener3 = this::onChangeListener;
        ChangeListener<Integer> listener4 = this::onChangeListener;
        requestClay.valueProperty().addListener(listener);
        requestBread.valueProperty().addListener(listener1);
        requestStone.valueProperty().addListener(listener2);
        requestWool.valueProperty().addListener(listener3);
        requestWood.valueProperty().addListener(listener4);
        listenerSpinnerHashMap.put(listener, requestClay);
        listenerSpinnerHashMap.put(listener1, requestBread);
        listenerSpinnerHashMap.put(listener2, requestStone);
        listenerSpinnerHashMap.put(listener3, requestWool);
        listenerSpinnerHashMap.put(listener4, requestWood);
    }

    private void onChangeListener(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        if (newValue > oldValue) {
            sumRequest += 1;
            if (sumRequest > 1) {
                if (tradeWood.getValue() != 0) {
                    tradeWood.getValueFactory().setValue(tradeWood.getValue() + Integer.parseInt(String.valueOf(woodLabel.getText().charAt(0))));
                } else if (tradeBread.getValue() != 0) {
                    tradeBread.getValueFactory().setValue(tradeBread.getValue() + Integer.parseInt(String.valueOf(breadLabel.getText().charAt(0))));
                } else if (tradeWool.getValue() != 0) {
                    tradeWool.getValueFactory().setValue(tradeWool.getValue() + Integer.parseInt(String.valueOf(woolLabel.getText().charAt(0))));
                } else if (tradeClay.getValue() != 0) {
                    tradeClay.getValueFactory().setValue(tradeClay.getValue() + Integer.parseInt(String.valueOf(clayLabel.getText().charAt(0))));
                } else if (tradeStone.getValue() != 0) {
                    tradeStone.getValueFactory().setValue(tradeStone.getValue() + Integer.parseInt(String.valueOf(stoneLabel.getText().charAt(0))));
                }
            }
        } else {
            sumRequest -= 1;
            if (sumRequest >= 1) {
                if (tradeWood.getValue() != 0) {
                    tradeWood.getValueFactory().setValue(tradeWood.getValue() - Integer.parseInt(String.valueOf(woodLabel.getText().charAt(0))));
                } else if (tradeBread.getValue() != 0) {
                    tradeBread.getValueFactory().setValue(tradeBread.getValue() - Integer.parseInt(String.valueOf(breadLabel.getText().charAt(0))));
                } else if (tradeWool.getValue() != 0) {
                    tradeWool.getValueFactory().setValue(tradeWool.getValue() - Integer.parseInt(String.valueOf(woolLabel.getText().charAt(0))));
                } else if (tradeClay.getValue() != 0) {
                    tradeClay.getValueFactory().setValue(tradeClay.getValue() - Integer.parseInt(String.valueOf(clayLabel.getText().charAt(0))));
                } else if (tradeStone.getValue() != 0) {
                    tradeStone.getValueFactory().setValue(tradeStone.getValue() - Integer.parseInt(String.valueOf(stoneLabel.getText().charAt(0))));
                }
            }
        }

    }

    public void removeListeners() {
        for (Map.Entry<ChangeListener<Integer>, Spinner<Integer>> entry : listenerSpinnerHashMap.entrySet()) {
            Spinner<Integer> spinner = entry.getValue();
            ChangeListener<Integer> listener = entry.getKey();
            spinner.valueProperty().removeListener(listener);
        }
    }

    //getter and setter

    private HashMap<String, Integer> getSpinnerValues() {
        HashMap<String, Integer> resources = new HashMap<>();
        if (tradeWood.getValue() != 0) {
            resources.put(RESOURCE_LUMBER, -tradeWood.getValue());
        }
        if (tradeStone.getValue() != 0) {
            resources.put(RESOURCE_ORE, -tradeStone.getValue());
        }
        if (tradeWool.getValue() != 0) {
            resources.put(RESOURCE_WOOL, -tradeWool.getValue());
        }
        if (tradeClay.getValue() != 0) {
            resources.put(RESOURCE_BRICK, -tradeClay.getValue());
        }
        if (tradeBread.getValue() != 0) {
            resources.put(RESOURCE_GRAIN, -tradeBread.getValue());
        }
        if (requestWood.getValue() != 0) {
            resources.put(RESOURCE_LUMBER, requestWood.getValue());
        }
        if (requestStone.getValue() != 0) {
            resources.put(RESOURCE_ORE, requestStone.getValue());
        }
        if (requestWool.getValue() != 0) {
            resources.put(RESOURCE_WOOL, requestWool.getValue());
        }
        if (requestClay.getValue() != 0) {
            resources.put(RESOURCE_BRICK, requestClay.getValue());
        }
        if (requestBread.getValue() != 0) {
            resources.put(RESOURCE_GRAIN, requestBread.getValue());
        }
        return resources;
    }

    public boolean isBankTrade() {
        return bankTrade;
    }

    public void setBankTrade(boolean bankTrade) {
        this.bankTrade = bankTrade;
    }

    public void setSumRequest(int sum) {
        this.sumRequest = sum;
    }
}
