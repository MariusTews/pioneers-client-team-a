package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.service.*;
import com.aviumauctores.pioneers.util.PannableCanvas;
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
    private final AchievementsService achievementsService;
    @FXML
    public Spinner<Integer> tradeLumber;
    @FXML
    public Spinner<Integer> requestLumber;
    @FXML
    public Spinner<Integer> tradeGrain;
    @FXML
    public Spinner<Integer> requestGrain;
    @FXML
    public Spinner<Integer> tradeBrick;
    @FXML
    public Spinner<Integer> requestBrick;
    @FXML
    public Spinner<Integer> tradeOre;
    @FXML
    public Spinner<Integer> requestOre;
    @FXML
    public Spinner<Integer> tradeWool;
    @FXML
    public Spinner<Integer> requestWool;
    @FXML public Label lumberLabel;
    @FXML public Label grainLabel;
    @FXML public Label brickLabel;
    @FXML public Label oreLabel;
    @FXML public Label woolLabel;
    @FXML public Button cancelTradeButton;
    @FXML public ListView<HBox> requestList;
    @FXML public Button tradeButton;
    @FXML public VBox bankWool;
    @FXML public VBox bankOre;
    @FXML public VBox bankBrick;
    @FXML public VBox bankGrain;
    @FXML public VBox bankLumber;
    private String userID;
    private final UserService userService;
    private final PioneerService pioneerService;
    private final ColorService colorService;
    private final ErrorService errorService;
    private Player player;
    private final HashMap<String, Integer> resourceRatio;
    private final TradeService tradeService;
    private final HashMap<ChangeListener<Integer>, Spinner<Integer>> listenerSpinnerHashMap;

    private CompositeDisposable disposables;

    private PlayerRequestsListController playerRequestsController;

    private boolean bankTrade;
    private int sumRequest;
    private String partnerID;
    private HashMap<String, Integer> sendResources;

    @Inject
    public TradingController(InGameController inGameController, ResourceBundle bundle, AchievementsService achievementsService, UserService userService,
                             PioneerService pioneerService, ColorService colorService, ErrorService errorService, Player player, HashMap<String, Integer> resourceRatio, TradeService tradeService) {
        this.inGameController = inGameController;
        this.bundle = bundle;
        this.achievementsService = achievementsService;
        this.userService = userService;
        this.pioneerService = pioneerService;
        this.colorService = colorService;
        this.errorService = errorService;
        this.player = player;
        this.resourceRatio = resourceRatio;
        this.tradeService = tradeService;
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
        this.initSpinnersBankTrade();
        playerRequestsController = new PlayerRequestsListController(this, pioneerService, userService, colorService);
        this.playerRequestsController.load(requestList, userID);
        this.setCorrectTradeRatio(resourceRatio);
        tradeButton.setDisable(true);

        tradeButton.setStyle("-fx-background-color: " + player.color());
        cancelTradeButton.setStyle("-fx-background-color: " + player.color());

        PannableCanvas canvas = new PannableCanvas();
        canvas.getChildren().add(parent);

        return canvas;
    }

    public void updatePlayer(Player player) {
        this.player = player;
    }


    public void cancel(ActionEvent actionEvent) {
        if (Objects.equals(cancelTradeButton.getText(), bundle.getString("decline"))) {
            disposables.add(pioneerService.createMove("accept", null, null, null, null)
                    .observeOn(FX_SCHEDULER).
                    subscribe(success -> {
                                this.enableCancelButton();
                                tradeButton.setText(bundle.getString("trading"));
                                cancelTradeButton.setText(bundle.getString("cancel"));
                                this.showRequestDeclined(partnerID);
                            },
                            error -> {
                                errorService.handleError(error);
                                this.enableCancelButton();
                            }
                    ));
        }
        else {
            inGameController.closeTradingMenu(false);
        }
    }

    //trade with the bank
    public void tradeWithBank() {
        HashMap<String, Integer> resources = tradeService.getSpinnerValues(
                tradeLumber, requestLumber,
                tradeWool, requestWool,
                tradeOre, requestOre,
                tradeGrain, requestGrain,
                tradeBrick, requestBrick);
        this.sendBankTrade(resources);
    }

    private void sendBankTrade(HashMap<String, Integer> resources) {
        errorService.setErrorCodesTrading();
        disposables.add(pioneerService.createMove("build", null, resources, "684072366f72202b72406465", null)
                .observeOn(FX_SCHEDULER).
                subscribe(move -> {
                    tradeButton.setDisable(true);
                    this.setAllBorderColorTransparent();
                }, errorService::handleError));
    }

    //send trade to bank or player
    public void enterTrade(ActionEvent actionEvent) {
        if (Objects.equals(tradeButton.getText(), bundle.getString("accept"))){
            this.acceptTrade();
        }
        else if (this.isBankTrade()) {
            this.tradeWithBank();
        } else {
            this.tradeWithPlayer();
        }
    }

    private void acceptTrade() {
        disposables.add(pioneerService.createMove("accept", null, null, partnerID, null)
                .observeOn(FX_SCHEDULER).
                subscribe(success -> {
                            this.enableCancelButton();
                            tradeButton.setText(bundle.getString("trading"));
                            cancelTradeButton.setText(bundle.getString("cancel"));
                            tradeButton.setDisable(true);
                            this.showRequestAccepted(partnerID);
                            this.setTradeSpinnersReady();
                            this.setRequestSpinnersReady();
                        },
                        error -> {
                            errorService.handleError(error);
                            tradeButton.setDisable(true);
                            this.enableCancelButton();
                            this.setTradeSpinnersReady();
                            this.setRequestSpinnersReady();
                        }
                ));
    }

    //trade with a player
    private void tradeWithPlayer() {
        errorService.setErrorCodesTrading();
        //get the correct player out of the list
        String playerName = playerRequestsController.getSelectedPlayer();
        if (playerName != null) {
            Player selectedPlayer = null;
            for (Player p : pioneerService.listPlayers().blockingFirst()) {
                String getPlayer = userService.getUserName(p.userId()).blockingFirst();
                if (Objects.equals(playerName, getPlayer)) {
                    selectedPlayer = p;
                    break;
                }
            }

            //Trade with player
            HashMap<String, Integer> resources = tradeService.getSpinnerValues(
                    tradeLumber, requestLumber,
                    tradeWool, requestWool,
                    tradeOre, requestOre,
                    tradeGrain, requestGrain,
                    tradeBrick, requestBrick);
            sendResources = resources;
            if (selectedPlayer != null && !this.checkSpinnersEmpty()) {
                Player finalSelectedPlayer = selectedPlayer;
                disposables.add(pioneerService.createMove("build", null, resources, selectedPlayer.userId(), null)
                        .observeOn(FX_SCHEDULER).
                        subscribe(move -> {
                                    showRequestOpen(finalSelectedPlayer.userId());
                                    tradeButton.setDisable(true);
                                    cancelTradeButton.setDisable(true);
                                }, errorService::handleError
                        ));
            }

        }
    }

    private boolean checkSpinnersEmpty() {
        return tradeLumber.getValueFactory().getValue() == 0
                && requestLumber.getValueFactory().getValue() == 0
                && tradeBrick.getValueFactory().getValue() == 0
                && requestBrick.getValueFactory().getValue() == 0
                && tradeWool.getValueFactory().getValue() == 0
                && requestWool.getValueFactory().getValue() == 0
                && tradeOre.getValueFactory().getValue() == 0
                && requestOre.getValueFactory().getValue() == 0
                && tradeGrain.getValueFactory().getValue() == 0
                && requestGrain.getValueFactory().getValue() == 0;
    }

    //change spinners, when user wants to trade with the bank
    private void setupBankTrade() {
        this.enableButtons();
        this.removeListeners();
        this.initSpinnersBankTrade();
        this.setRequestSpinnersReady();
        this.setTradeSpinnersReady();
        this.setBankTrade(true);
        this.setSumRequest(0);
        this.setListeners();
    }

    public void enterLumberVariables(MouseEvent mouseEvent) {
        this.enterBankVariables(lumberLabel, requestLumber, tradeLumber, tradeBrick, tradeOre, tradeGrain, tradeWool);
        this.setBorderColorTransparent(bankBrick, bankGrain, bankOre, bankWool);
        bankLumber.setStyle("-fx-border-color: blue");
    }

    public void enterGrainVariables(MouseEvent mouseEvent) {
        this.enterBankVariables(grainLabel, requestGrain, tradeGrain, tradeLumber, tradeOre, tradeWool, tradeBrick);
        this.setBorderColorTransparent(bankBrick, bankWool, bankOre, bankLumber);
        bankGrain.setStyle("-fx-border-color: blue");
    }

    public void enterBrickVariables(MouseEvent mouseEvent) {
        this.enterBankVariables(brickLabel, requestBrick, tradeBrick, tradeGrain, tradeLumber, tradeWool, tradeOre);
        this.setBorderColorTransparent(bankWool, bankGrain, bankOre, bankLumber);
        bankBrick.setStyle("-fx-border-color: blue");
    }

    public void enterOreVariables(MouseEvent mouseEvent) {
        this.enterBankVariables(oreLabel, requestOre, tradeOre, tradeGrain, tradeLumber, tradeBrick, tradeWool);
        this.setBorderColorTransparent(bankBrick, bankGrain, bankWool, bankLumber);
        bankOre.setStyle("-fx-border-color: blue");
    }

    public void enterWoolVariables(MouseEvent mouseEvent) {
        this.enterBankVariables(woolLabel, requestWool, tradeWool, tradeGrain, tradeOre, tradeBrick, tradeLumber);
        this.setBorderColorTransparent(bankBrick, bankGrain, bankOre, bankLumber);
        bankWool.setStyle("-fx-border-color: blue");

    }

    public void setBorderColorTransparent(VBox bank1, VBox bank2, VBox bank3, VBox bank4) {
        bank1.setStyle("-fx-border-color: Transparent");
        bank2.setStyle("-fx-border-color: Transparent");
        bank3.setStyle("-fx-border-color: Transparent");
        bank4.setStyle("-fx-border-color: Transparent");
    }

    public void setAllBorderColorTransparent() {
        bankGrain.setStyle("-fx-border-color: Transparent");
        bankLumber.setStyle("-fx-border-color: Transparent");
        bankBrick.setStyle("-fx-border-color: Transparent");
        bankWool.setStyle("-fx-border-color: Transparent");
        bankOre.setStyle("-fx-border-color: Transparent");
    }


    private void enterBankVariables(Label resourceLabel, Spinner<Integer> request, Spinner<Integer> trade, Spinner<Integer> notClicked1, Spinner<Integer> notClicked2, Spinner<Integer> notClicked3, Spinner<Integer> notClicked4) {
        this.setupBankTrade();
        request.setDisable(true);
        trade.getValueFactory().setValue(Integer.parseInt(String.valueOf(resourceLabel.getText().charAt(0))));
        this.setTradeSpinnersZeroAndDisable(notClicked1, notClicked2, notClicked3, notClicked4);
    }


    //setup spinners
    public void initSpinnersPrivateTrade() {
        tradeLumber.setValueFactory(tradeService.createValueFactory(player.resources().getOrDefault(RESOURCE_LUMBER, 0)));
        requestLumber.setValueFactory(tradeService.createValueFactory(32));
        tradeGrain.setValueFactory(tradeService.createValueFactory(player.resources().getOrDefault(RESOURCE_GRAIN, 0)));
        requestGrain.setValueFactory(tradeService.createValueFactory(32));
        tradeBrick.setValueFactory(tradeService.createValueFactory(player.resources().getOrDefault(RESOURCE_BRICK, 0)));
        requestBrick.setValueFactory(tradeService.createValueFactory(32));
        tradeOre.setValueFactory(tradeService.createValueFactory(player.resources().getOrDefault(RESOURCE_ORE, 0)));
        requestOre.setValueFactory(tradeService.createValueFactory(32));
        tradeWool.setValueFactory(tradeService.createValueFactory(player.resources().getOrDefault(RESOURCE_WOOL, 0)));
        requestWool.setValueFactory(tradeService.createValueFactory(32));
    }

    private void initSpinnersBankTrade() {
        tradeLumber.setValueFactory(tradeService.createValueFactory(32));
        requestLumber.setValueFactory(tradeService.createValueFactory(32));
        tradeGrain.setValueFactory(tradeService.createValueFactory(32));
        requestGrain.setValueFactory(tradeService.createValueFactory(32));
        tradeBrick.setValueFactory(tradeService.createValueFactory(32));
        requestBrick.setValueFactory(tradeService.createValueFactory(32));
        tradeOre.setValueFactory(tradeService.createValueFactory(32));
        requestOre.setValueFactory(tradeService.createValueFactory(32));
        tradeWool.setValueFactory(tradeService.createValueFactory(32));
        requestWool.setValueFactory(tradeService.createValueFactory(32));
    }

    public void setRequestSpinnersReady() {
        requestLumber.setDisable(false);
        requestWool.setDisable(false);
        requestOre.setDisable(false);
        requestGrain.setDisable(false);
        requestBrick.setDisable(false);
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

    public void setTradeSpinnersReady() {
        tradeGrain.setDisable(false);
        tradeBrick.setDisable(false);
        tradeOre.setDisable(false);
        tradeWool.setDisable(false);
        tradeLumber.setDisable(false);
    }

    private void fillSpinners(String resource, Spinner<Integer> tradeSpinner, Spinner<Integer> requestSpinner, HashMap<String, Integer> resources) {
        int value = resources.getOrDefault(resource, 0);
        if (value < 0) {
            requestSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-value, -value));
            requestSpinner.getValueFactory().setValue(-value);
            requestSpinner.setDisable(false);
        } else if (value > 0) {
            tradeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(value, value));
            tradeSpinner.getValueFactory().setValue(value);
            tradeSpinner.setDisable(false);
        }
    }
    private void fillSpinnersAndDisable(HashMap<String, Integer> resources) {
        tradeLumber.setDisable(true);
        tradeGrain.setDisable(true);
        tradeBrick.setDisable(true);
        tradeOre.setDisable(true);
        tradeWool.setDisable(true);
        requestLumber.setDisable(true);
        requestGrain.setDisable(true);
        requestBrick.setDisable(true);
        requestOre.setDisable(true);
        requestWool.setDisable(true);
        this.fillSpinners(RESOURCE_LUMBER, tradeLumber, requestLumber, resources);
        this.fillSpinners(RESOURCE_BRICK, tradeBrick, requestBrick, resources);
        this.fillSpinners(RESOURCE_GRAIN, tradeGrain, requestGrain, resources);
        this.fillSpinners(RESOURCE_ORE, tradeOre, requestOre, resources);
        this.fillSpinners(RESOURCE_WOOL, tradeWool, requestWool, resources);

    }

    //change listener for spinners

    private void setListeners() {
        ChangeListener<Integer> listener = this::onChangeListener;
        ChangeListener<Integer> listener1 = this::onChangeListener;
        ChangeListener<Integer> listener2 = this::onChangeListener;
        ChangeListener<Integer> listener3 = this::onChangeListener;
        ChangeListener<Integer> listener4 = this::onChangeListener;
        requestBrick.valueProperty().addListener(listener);
        requestGrain.valueProperty().addListener(listener1);
        requestOre.valueProperty().addListener(listener2);
        requestWool.valueProperty().addListener(listener3);
        requestLumber.valueProperty().addListener(listener4);
        listenerSpinnerHashMap.put(listener, requestBrick);
        listenerSpinnerHashMap.put(listener1, requestGrain);
        listenerSpinnerHashMap.put(listener2, requestOre);
        listenerSpinnerHashMap.put(listener3, requestWool);
        listenerSpinnerHashMap.put(listener4, requestLumber);
    }

    private void onChangeListener(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        if (newValue > oldValue) {
            sumRequest += 1;
            if (sumRequest > 1) {
                if (tradeLumber.getValue() != 0) {
                    tradeLumber.getValueFactory().setValue(tradeLumber.getValue() + Integer.parseInt(String.valueOf(lumberLabel.getText().charAt(0))));
                } else if (tradeGrain.getValue() != 0) {
                    tradeGrain.getValueFactory().setValue(tradeGrain.getValue() + Integer.parseInt(String.valueOf(grainLabel.getText().charAt(0))));
                } else if (tradeWool.getValue() != 0) {
                    tradeWool.getValueFactory().setValue(tradeWool.getValue() + Integer.parseInt(String.valueOf(woolLabel.getText().charAt(0))));
                } else if (tradeBrick.getValue() != 0) {
                    tradeBrick.getValueFactory().setValue(tradeBrick.getValue() + Integer.parseInt(String.valueOf(brickLabel.getText().charAt(0))));
                } else if (tradeOre.getValue() != 0) {
                    tradeOre.getValueFactory().setValue(tradeOre.getValue() + Integer.parseInt(String.valueOf(oreLabel.getText().charAt(0))));
                }
            }
        } else {
            sumRequest -= 1;
            if (sumRequest >= 1) {
                if (tradeLumber.getValue() != 0) {
                    tradeLumber.getValueFactory().setValue(tradeLumber.getValue() - Integer.parseInt(String.valueOf(lumberLabel.getText().charAt(0))));
                } else if (tradeGrain.getValue() != 0) {
                    tradeGrain.getValueFactory().setValue(tradeGrain.getValue() - Integer.parseInt(String.valueOf(grainLabel.getText().charAt(0))));
                } else if (tradeWool.getValue() != 0) {
                    tradeWool.getValueFactory().setValue(tradeWool.getValue() - Integer.parseInt(String.valueOf(woolLabel.getText().charAt(0))));
                } else if (tradeBrick.getValue() != 0) {
                    tradeBrick.getValueFactory().setValue(tradeBrick.getValue() - Integer.parseInt(String.valueOf(brickLabel.getText().charAt(0))));
                } else if (tradeOre.getValue() != 0) {
                    tradeOre.getValueFactory().setValue(tradeOre.getValue() - Integer.parseInt(String.valueOf(oreLabel.getText().charAt(0))));
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

    //get counterpropasal
    public void handleRequest(HashMap<String, Integer> resources, String userId) {
        HashMap<String, Integer> sendResourcesInverse = sendResources;
        for (Map.Entry<String, Integer> entry : sendResourcesInverse.entrySet()) {
            entry.setValue(-entry.getValue());
        }
        int ressourceSum = 0;
        if (Objects.equals(resources, sendResourcesInverse)) {
            disposables.add(pioneerService.createMove("accept", null, null, userId, null)
                    .observeOn(FX_SCHEDULER).
                    subscribe(move -> {
                        this.showRequestAccepted(userId);
                        this.enableCancelButton();
                    }, error -> {
                        errorService.handleError(error);
                        this.enableCancelButton();
                    }));
            for (String key: sendResourcesInverse.keySet()) {
                ressourceSum += sendResourcesInverse.get(key);
            }
        }
        else {
            partnerID = userId;
            this.showRequest(userId);
            this.fillSpinnersAndDisable(resources);
            tradeButton.setText(bundle.getString("accept"));
            cancelTradeButton.setText(bundle.getString("decline"));
            this.enableButtons();
            for (Map.Entry<String, Integer> entry : resources.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                if (value > 0) {
                    if (player.resources().getOrDefault(key, -value) < value) {
                        tradeButton.setDisable(true);
                        break;
                    }
                }
            }
        }

        if (ressourceSum >= 5) {
            disposables.add(achievementsService.putAchievement(ACHIEVEMENT_TRADE, 1)
                    .observeOn(FX_SCHEDULER)
                    .subscribe());
        }
    }
    //getter and setter

    public boolean isBankTrade() {
        return bankTrade;
    }

    public void setBankTrade(boolean bankTrade) {
        this.bankTrade = bankTrade;
    }

    public void setSumRequest(int sum) {
        this.sumRequest = sum;
    }

    public void setCorrectTradeRatio(HashMap<String, Integer> ratio) {
        lumberLabel.setText(ratio.get(RESOURCE_LUMBER).toString() + ":1");
        oreLabel.setText(ratio.get(RESOURCE_ORE).toString() + ":1");
        brickLabel.setText(ratio.get(RESOURCE_BRICK).toString() + ":1");
        woolLabel.setText(ratio.get(RESOURCE_WOOL).toString() + ":1");
        grainLabel.setText(ratio.get(RESOURCE_GRAIN).toString() + ":1");

    }

    public void enableButtons() {
        this.cancelTradeButton.setDisable(false);
        this.tradeButton.setDisable(false);
    }

    public void enableCancelButton() {
        this.cancelTradeButton.setDisable(false);
    }

    public void showRequestOpen(String playerID) {
        playerRequestsController.showRequestOpen(playerID);
    }

    public void showRequestAccepted(String playerID) {
        playerRequestsController.showRequestAccepted(playerID);
    }

    public void showRequestDeclined(String playerID) {
        playerRequestsController.showRequestDeclined(playerID);
    }

    public void showRequest(String playerID){ playerRequestsController.showRequest(playerID);}
}