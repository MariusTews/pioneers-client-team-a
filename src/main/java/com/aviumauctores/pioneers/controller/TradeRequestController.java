package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.ErrorService;
import com.aviumauctores.pioneers.service.PioneerService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.*;

public class TradeRequestController implements Controller {
    public Label tradeWoodLabel;
    public Label tradeBreadLabel;
    public Label tradeClayLabel;
    public Label tradeStoneLabel;
    public Label tradeWoolLabel;
    public Label getWoodLabel;
    public Label getBreadLabel;
    public Label getClayLabel;
    public Label getStoneLabel;
    public Label getWoolLabel;
    public ImageView playerAvatar;
    public Label playerLabel;
    public Button acceptButton;
    public Button declineButton;
    private final InGameController inGameController;
    private final ResourceBundle bundle;
    private final PioneerService pioneerService;
    private ErrorService errorService;
    private final HashMap<String, Integer> tradeRessources;
    private final String tradePartner;
    private final String tradePartnerAvatarUrl;
    private final String tradePartnerColor;
    private CompositeDisposable disposables;

    public TradeRequestController(InGameController inGameController,
                                  ResourceBundle bundle,
                                  PioneerService pioneerService,
                                  ErrorService errorService,
                                  HashMap<String, Integer> tradeRessources,
                                  String tradePartner,
                                  String tradePartnerAvatarUrl,
                                  String tradePartnerColor) {

        this.inGameController = inGameController;
        this.bundle = bundle;
        this.pioneerService = pioneerService;
        this.errorService = errorService;
        this.tradeRessources = tradeRessources;
        this.tradePartner = tradePartner;
        this.tradePartnerAvatarUrl = tradePartnerAvatarUrl;
        this.tradePartnerColor = tradePartnerColor;
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
        Image playerIcon = tradePartnerAvatarUrl == null ? new Image(Objects.requireNonNull(Main.class.getResource("icons/playerIcon_" + tradePartnerColor + ".png")).toString()) : new Image(tradePartnerAvatarUrl);
        playerAvatar.setImage(playerIcon);
        playerAvatar.setFitHeight(40.0);
        playerAvatar.setFitWidth(40.0);
        return parent;
    }

    private void fillLabels() {
        for (Map.Entry<String, Integer> entry : tradeRessources.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            switch (key) {
                case RESOURCE_LUMBER -> this.fillLabel(tradeWoodLabel, getWoodLabel, value);
                case RESOURCE_BRICK -> this.fillLabel(tradeClayLabel, getClayLabel, value);
                case RESOURCE_GRAIN -> this.fillLabel(tradeBreadLabel, getBreadLabel, value);
                case RESOURCE_ORE -> this.fillLabel(tradeStoneLabel, getStoneLabel, value);
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
        errorService.setErrorCodesTradeController();
        HashMap<String, Integer> resources = tradeRessources;
        for (Map.Entry<String, Integer> entry : resources.entrySet()) {
            entry.setValue(-entry.getValue());
        }
        disposables.add(pioneerService.createMove("offer", null, resources, null, null)
                .observeOn(FX_SCHEDULER).
                subscribe(move -> {
                }, errorService::handleError));
        inGameController.closeRequestMenu(false);
    }

    public void declineRequest(ActionEvent actionEvent) {
        inGameController.closeRequestMenu(false);
    }
}
