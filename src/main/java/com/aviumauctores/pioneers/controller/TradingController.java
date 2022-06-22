package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.rest.PioneersApiService;
import com.aviumauctores.pioneers.service.PioneerService;
import com.aviumauctores.pioneers.service.UserService;
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
import java.util.ResourceBundle;

public class TradingController implements Controller {
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
    public Button playerTradeButton;
    public Button bankTradeButton;
    public VBox bankWool;
    public VBox bankStone;
    public VBox bankClay;
    public VBox bankBread;
    public VBox bankWood;
    private String userID;
    private Player player;
    private final UserService userService;
    private final PioneerService pioneerService;

    private final PlayerRequestsController playerRequestsController;

    @Inject
    public TradingController(ResourceBundle bundle, UserService userService, PioneerService pioneerService, PlayerRequestsController playerRequestsController) {
        this.bundle = bundle;
        this.userService = userService;
        this.pioneerService = pioneerService;
        this.playerRequestsController = playerRequestsController;
    }

    @Override
    public void init() {
        userID = userService.getCurrentUserID();
        player = pioneerService.getPlayer(userID).blockingFirst();

    }

    @Override
    public void destroy(boolean closed) {

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
        this.playerRequestsController.load(requestList, userID);
        return parent;
    }


    public void cancel(ActionEvent actionEvent) {

    }

    private void initSpinners() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5);
        valueFactory.setValue(0);
        SpinnerValueFactory<Integer> valueFactory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5);
        valueFactory1.setValue(0);
        SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5);
        valueFactory2.setValue(0);
        SpinnerValueFactory<Integer> valueFactory3 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5);
        valueFactory3.setValue(0);
        SpinnerValueFactory<Integer> valueFactory4 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 35);
        valueFactory4.setValue(0);
        SpinnerValueFactory<Integer> valueFactory5 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5);
        valueFactory5.setValue(0);
        SpinnerValueFactory<Integer> valueFactory6 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5);
        valueFactory6.setValue(0);
        SpinnerValueFactory<Integer> valueFactory7 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5);
        valueFactory7.setValue(0);
        SpinnerValueFactory<Integer> valueFactory8 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5);
        valueFactory8.setValue(0);
        SpinnerValueFactory<Integer> valueFactory9 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5);
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

    public void tradeWithBank(ActionEvent actionEvent) {

    }

    public void tradeWithPlayer(ActionEvent actionEvent) {

    }

    public void enterWoodVariables(MouseEvent mouseEvent) {
        String totrade = woodLabel.getText();
        if (totrade.charAt(0) == '3') {
            tradeWood.getValueFactory().setValue(3);
            tradeWool.getValueFactory().setValue(0);
            tradeStone.getValueFactory().setValue(0);
            tradeClay.getValueFactory().setValue(0);
            tradeBread.getValueFactory().setValue(0);
        } else {
            tradeWood.getValueFactory().setValue(2);
            tradeWool.getValueFactory().setValue(0);
            tradeStone.getValueFactory().setValue(0);
            tradeClay.getValueFactory().setValue(0);
            tradeBread.getValueFactory().setValue(0);

        }
    }

    public void enterBreadVariables(MouseEvent mouseEvent) {
        String totrade = woodLabel.getText();
        if (totrade.charAt(0) == '3') {
            tradeWood.getValueFactory().setValue(0);
            tradeWool.getValueFactory().setValue(0);
            tradeStone.getValueFactory().setValue(0);
            tradeClay.getValueFactory().setValue(0);
            tradeBread.getValueFactory().setValue(3);
        } else {
            tradeWood.getValueFactory().setValue(0);
            tradeWool.getValueFactory().setValue(0);
            tradeStone.getValueFactory().setValue(0);
            tradeClay.getValueFactory().setValue(0);
            tradeBread.getValueFactory().setValue(2);

        }
    }

    public void enterClayVariables(MouseEvent mouseEvent) {
        String totrade = woodLabel.getText();
        if (totrade.charAt(0) == '3') {
            tradeWood.getValueFactory().setValue(0);
            tradeWool.getValueFactory().setValue(0);
            tradeStone.getValueFactory().setValue(0);
            tradeClay.getValueFactory().setValue(3);
            tradeBread.getValueFactory().setValue(0);
        } else {
            tradeWood.getValueFactory().setValue(0);
            tradeWool.getValueFactory().setValue(0);
            tradeStone.getValueFactory().setValue(0);
            tradeClay.getValueFactory().setValue(2);
            tradeBread.getValueFactory().setValue(0);

        }
    }

    public void enterStoneVariables(MouseEvent mouseEvent) {
        String totrade = woodLabel.getText();
        if (totrade.charAt(0) == '3') {
            tradeWood.getValueFactory().setValue(0);
            tradeWool.getValueFactory().setValue(0);
            tradeStone.getValueFactory().setValue(3);
            tradeClay.getValueFactory().setValue(0);
            tradeBread.getValueFactory().setValue(0);
        } else {
            tradeWood.getValueFactory().setValue(0);
            tradeWool.getValueFactory().setValue(0);
            tradeStone.getValueFactory().setValue(2);
            tradeClay.getValueFactory().setValue(0);
            tradeBread.getValueFactory().setValue(0);

        }
    }

    public void enterWoolVariables(MouseEvent mouseEvent) {
        String totrade = woodLabel.getText();
        if (totrade.charAt(0) == '3') {
            tradeWood.getValueFactory().setValue(0);
            tradeWool.getValueFactory().setValue(2);
            tradeStone.getValueFactory().setValue(0);
            tradeClay.getValueFactory().setValue(0);
            tradeBread.getValueFactory().setValue(0);
        } else {
            tradeWood.getValueFactory().setValue(0);
            tradeWool.getValueFactory().setValue(2);
            tradeStone.getValueFactory().setValue(0);
            tradeClay.getValueFactory().setValue(0);
            tradeBread.getValueFactory().setValue(0);

        }

    }
}
