package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.service.ColorService;
import com.aviumauctores.pioneers.service.PioneerService;
import com.aviumauctores.pioneers.service.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
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
import java.util.Objects;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.*;

public class TradingController implements Controller {
    private InGameController inGameController;
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
    private final ColorService colorService;

    private CompositeDisposable disposables;

    private PlayerRequestsController playerRequestsController;

    @Inject
    public TradingController(InGameController inGameController, ResourceBundle bundle, UserService userService, PioneerService pioneerService, ColorService colorService) {
        this.inGameController = inGameController;
        this.bundle = bundle;
        this.userService = userService;
        this.pioneerService = pioneerService;
        this.colorService = colorService;
    }

    @Override
    public void init() {
        userID = userService.getCurrentUserID();
        player = pioneerService.getPlayer(userID).blockingFirst();
        disposables = new CompositeDisposable();

    }

    @Override
    public void destroy(boolean closed) {
        if (playerRequestsController != null) {
            playerRequestsController.destroy(closed);
            playerRequestsController = null;
        }
        disposables.dispose();

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
        playerRequestsController = new PlayerRequestsController(pioneerService,userService,colorService, bundle);
        this.playerRequestsController.load(requestList, userID);
        return parent;
    }


    public void cancel(ActionEvent actionEvent) {
        inGameController.closeTradingMenu(false);


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

        HashMap<String, Integer> resources = new HashMap<>();
        resources.put(RESOURCE_BRICK, 1);
        resources.put(RESOURCE_GRAIN, -4);
        disposables.add(pioneerService.createMove("build", null, "684072366f72202b72406465", null, resources )
                .observeOn(FX_SCHEDULER).
                subscribe(move -> {
                    System.out.println("test");
                }, error -> {
                    System.out.println(error.getMessage());
                }));
        if (tradeWood.getValue() != 0){
            int toTrade = tradeWood.getValue();
            if(requestWool.getValue() != 0){
                //TODO trade
            } else if (requestWood.getValue() != 0) {
                //TODO trade
            } else if (requestStone.getValue() != 0) {
                //TODO trade
            } else if (requestClay.getValue() != 0) {
                //TODO trade
            } else if (requestBread.getValue() != 0) {
                //TODO trade
            }
        }

        if (tradeBread.getValue() != 0){
            int toTrade = tradeBread.getValue();
            if(requestWool.getValue() != 0){
                //TODO trade
            } else if (requestWood.getValue() != 0) {
                //TODO trade
            } else if (requestStone.getValue() != 0) {
                //TODO trade
            } else if (requestClay.getValue() != 0) {
                //TODO trade
            } else if (requestBread.getValue() != 0) {
                //TODO trade
            }
        }

        if (tradeWool.getValue() != 0){
            int toTrade = tradeWool.getValue();
            if(requestWool.getValue() != 0){
                //TODO trade
            } else if (requestWood.getValue() != 0) {
                //TODO trade
            } else if (requestStone.getValue() != 0) {
                //TODO trade
            } else if (requestClay.getValue() != 0) {
                //TODO trade
            } else if (requestBread.getValue() != 0) {
                //TODO trade
            }
        }

        if (tradeClay.getValue() != 0){
            int toTrade = tradeClay.getValue();
            if(requestWool.getValue() != 0){
                //TODO trade
            } else if (requestWood.getValue() != 0) {
                //TODO trade
            } else if (requestStone.getValue() != 0) {
                //TODO trade
            } else if (requestClay.getValue() != 0) {
                //TODO trade
            } else if (requestBread.getValue() != 0) {
                //TODO trade
            }
        }

        if (tradeStone.getValue() != 0){
            int toTrade = tradeStone.getValue();
            if(requestWool.getValue() != 0){
                //TODO trade
            } else if (requestWood.getValue() != 0) {
                //TODO trade
            } else if (requestStone.getValue() != 0) {
                //TODO trade
            } else if (requestClay.getValue() != 0) {
                //TODO trade
            } else if (requestBread.getValue() != 0) {
                //TODO trade
            }
        }

    }

    public void tradeWithPlayer(ActionEvent actionEvent) {
        String playerName = playerRequestsController.getSelectedPlayer();
        if (playerName != null){
            Player selectedPlayer = null;
            for (Player p : pioneerService.listPlayers().blockingFirst()) {
                String getPlayer = userService.getUserName(p.userId()).blockingFirst();
                if (Objects.equals(playerName, getPlayer)){
                    selectedPlayer = p;
                }
            }
            //TODO trade with player
            HashMap<String, Integer> resources = new HashMap<>();
            resources.put(RESOURCE_BRICK, 1);
            resources.put(RESOURCE_GRAIN, -1);
            System.out.println(selectedPlayer.userId());
            disposables.add(pioneerService.createMove("build", null, selectedPlayer.userId(), null, resources )
                    .observeOn(FX_SCHEDULER).
                    subscribe(move -> {
                        System.out.println("test");
                    }, error -> {
                        System.out.println(error.getMessage());
                    }));

        }

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
        System.out.println(totrade.charAt(0));
        if (totrade.charAt(0) == '3') {
            tradeWood.getValueFactory().setValue(0);
            tradeWool.getValueFactory().setValue(3);
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
