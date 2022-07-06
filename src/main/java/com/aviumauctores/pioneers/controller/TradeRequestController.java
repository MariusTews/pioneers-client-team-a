package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.service.ErrorService;
import com.aviumauctores.pioneers.service.PioneerService;
import com.aviumauctores.pioneers.service.TradeService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.*;

public class TradeRequestController implements Controller {
    @FXML
    public Label tradeLumberLabel;
    @FXML
    public Label tradeGrainLabel;
    @FXML
    public Label tradeBrickLabel;
    @FXML
    public Label tradeOreLabel;
    @FXML
    public Label tradeWoolLabel;
    @FXML
    public Label getLumberLabel;
    @FXML
    public Label getGrainLabel;
    @FXML
    public Label getBrickLabel;
    @FXML
    public Label getOreLabel;
    @FXML
    public Label getWoolLabel;
    @FXML
    public ImageView playerAvatar;
    @FXML
    public Label playerLabel;
    @FXML
    public Button acceptButton;
    @FXML
    public Button declineButton;
    @FXML
    public Button counterproposalButton;
    @FXML
    public Spinner<Integer> tradeLumber;
    @FXML
    public Spinner<Integer> tradeWool;
    @FXML
    public Spinner<Integer> requestWool;
    @FXML
    public Spinner<Integer> requestOre;
    @FXML
    public Spinner<Integer> requestBrick;
    @FXML
    public Spinner<Integer> requestGrain;
    @FXML
    public Spinner<Integer> requestLumber;
    @FXML
    public Spinner<Integer> tradeGrain;
    @FXML
    public Spinner<Integer> tradeBrick;
    @FXML
    public Spinner<Integer> tradeOre;
    private final InGameController inGameController;
    private final ResourceBundle bundle;
    private final PioneerService pioneerService;
    private final ErrorService errorService;
    private final HashMap<String, Integer> tradeRessources;
    private final String tradePartner;
    private final String tradePartnerAvatarUrl;
    private String tradePartnerColor;
    private final String color;
    private final Player player;
    private final TradeService tradeService;
    private CompositeDisposable disposables;

    public TradeRequestController(InGameController inGameController,
                                  ResourceBundle bundle,
                                  PioneerService pioneerService,
                                  ErrorService errorService,
                                  HashMap<String, Integer> tradeRessources,
                                  String tradePartner,
                                  String tradePartnerAvatarUrl,
                                  String tradePartnerColor, String myColor, Player player, TradeService tradeService) {

        this.inGameController = inGameController;
        this.bundle = bundle;
        this.pioneerService = pioneerService;
        this.errorService = errorService;
        this.tradeRessources = tradeRessources;
        this.tradePartner = tradePartner;
        this.tradePartnerAvatarUrl = tradePartnerAvatarUrl;
        this.tradePartnerColor = tradePartnerColor;
        this.color = myColor;
        this.player = player;
        this.tradeService = tradeService;
    }


    @Override
    public void init() {
        disposables = new CompositeDisposable();

    }

    @Override
    public void destroy(boolean closed) {
        if (disposables != null) {
            disposables.dispose();
        }

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/tradeRequest.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        this.fillLabels();
        playerLabel.setText(tradePartner);

        //only for testing
        if (tradePartnerColor == null) {
            tradePartnerColor = "blue";
        }

        Image playerIcon = tradePartnerAvatarUrl == null ? new Image(Objects.requireNonNull(Main.class.getResource("icons/playerIcon_" + tradePartnerColor + ".png")).toString()) : new Image(tradePartnerAvatarUrl);
        playerAvatar.setImage(playerIcon);
        playerAvatar.setFitHeight(40.0);
        playerAvatar.setFitWidth(40.0);
        acceptButton.setStyle("-fx-background-color: " + color);
        declineButton.setStyle("-fx-background-color: " + color);

        HashMap<String, Integer> myResources = player.resources();
        for (Map.Entry<String, Integer> entry : tradeRessources.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (value > 0) {
                if (myResources.getOrDefault(key, -value) < value) {
                    acceptButton.setDisable(true);
                    break;
                }
            }
        }
        counterproposalButton.setStyle("-fx-background-color: " + color);
        return parent;
    }

    private void fillLabels() {
        for (Map.Entry<String, Integer> entry : tradeRessources.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            switch (key) {
                case RESOURCE_LUMBER -> this.fillLabel(tradeLumberLabel, getLumberLabel, value);
                case RESOURCE_BRICK -> this.fillLabel(tradeBrickLabel, getBrickLabel, value);
                case RESOURCE_GRAIN -> this.fillLabel(tradeGrainLabel, getGrainLabel, value);
                case RESOURCE_ORE -> this.fillLabel(tradeOreLabel, getOreLabel, value);
                case RESOURCE_WOOL -> this.fillLabel(tradeWoolLabel, getWoolLabel, value);
            }
        }
    }

    private void fillLabel(Label tradeLabel, Label getLabel, Integer value) {
        if (value < 0) {
            getLabel.setText(String.valueOf(-value));
        } else {
            tradeLabel.setText(String.valueOf(value));
        }
    }

    public void acceptRequest(ActionEvent actionEvent) {
        errorService.setErrorCodesTrading();
        HashMap<String, Integer> resources = new HashMap<>();
        if (Objects.equals(acceptButton.getText(), bundle.getString("accept"))) {
            resources = tradeRessources;
            for (Map.Entry<String, Integer> entry : resources.entrySet()) {
                entry.setValue(-entry.getValue());
            }
        } else if (Objects.equals(acceptButton.getText(), bundle.getString("suggest"))) {
            resources = tradeService.getSpinnerValues(
                    tradeLumber, requestLumber,
                    tradeWool, requestWool,
                    tradeOre, requestOre,
                    tradeGrain, requestGrain,
                    tradeBrick, requestBrick);
        }
        disposables.add(pioneerService.createMove("offer", null, resources, null, null)
                .observeOn(FX_SCHEDULER).
                subscribe(move -> inGameController.closeRequestMenu(false), error -> {
                    errorService.handleError(error);
                    inGameController.closeRequestMenu(false);
                }));
    }

    public void declineRequest(ActionEvent actionEvent) {
        errorService.setErrorCodesTrading();
        disposables.add(pioneerService.createMove("offer", null, null, null, null)
                .observeOn(FX_SCHEDULER).
                subscribe(move -> inGameController.closeRequestMenu(false), error -> {
                    errorService.handleError(error);
                    inGameController.closeRequestMenu(false);
                }));
    }

    public void setupCounterproposal(ActionEvent actionEvent) {
        this.setupSpinners();
        this.makeLabelsInvisible();
        this.showSpinners();
        acceptButton.setText(this.bundle.getString("suggest"));

    }

    private void makeLabelsInvisible() {
        tradeLumberLabel.setVisible(false);
        getLumberLabel.setVisible(false);
        tradeBrickLabel.setVisible(false);
        getBrickLabel.setVisible(false);
        tradeGrainLabel.setVisible(false);
        getGrainLabel.setVisible(false);
        tradeOreLabel.setVisible(false);
        getOreLabel.setVisible(false);
        tradeWoolLabel.setVisible(false);
        getWoolLabel.setVisible(false);
    }

    private void showSpinners() {
        tradeLumber.setVisible(true);
        requestLumber.setVisible(true);
        tradeWool.setVisible(true);
        requestWool.setVisible(true);
        tradeGrain.setVisible(true);
        requestGrain.setVisible(true);
        tradeOre.setVisible(true);
        requestOre.setVisible(true);
        tradeBrick.setVisible(true);
        requestBrick.setVisible(true);
    }

    private void setupSpinners() {
        tradeLumber.setValueFactory(tradeService.createValueFactory(player.resources().getOrDefault(RESOURCE_LUMBER, 0)));
        tradeOre.setValueFactory(tradeService.createValueFactory(player.resources().getOrDefault(RESOURCE_ORE, 0)));
        tradeBrick.setValueFactory(tradeService.createValueFactory(player.resources().getOrDefault(RESOURCE_BRICK, 0)));
        tradeGrain.setValueFactory(tradeService.createValueFactory(player.resources().getOrDefault(RESOURCE_GRAIN, 0)));
        tradeWool.setValueFactory(tradeService.createValueFactory(player.resources().getOrDefault(RESOURCE_WOOL, 0)));

        requestLumber.setValueFactory(tradeService.createValueFactory(32));
        requestOre.setValueFactory(tradeService.createValueFactory(32));
        requestBrick.setValueFactory(tradeService.createValueFactory(32));
        requestGrain.setValueFactory(tradeService.createValueFactory(32));
        requestWool.setValueFactory(tradeService.createValueFactory(32));

        this.setSpinnerValues();
    }

    private void setSpinnerValues() {
        this.fillSpinners(RESOURCE_LUMBER, tradeLumber, requestLumber);
        this.fillSpinners(RESOURCE_BRICK, tradeBrick, requestBrick);
        this.fillSpinners(RESOURCE_GRAIN, tradeGrain, requestGrain);
        this.fillSpinners(RESOURCE_ORE, tradeOre, requestOre);
        this.fillSpinners(RESOURCE_WOOL, tradeWool, requestWool);
    }

    private void fillSpinners(String resource, Spinner<Integer> tradeSpinner, Spinner<Integer> requestSpinner) {
        int value = tradeRessources.getOrDefault(resource, 0);
        if (value < 0) {
            requestSpinner.getValueFactory().setValue(-value);
        } else if (value > 0) {
            tradeSpinner.getValueFactory().setValue(value);
        }
    }
}