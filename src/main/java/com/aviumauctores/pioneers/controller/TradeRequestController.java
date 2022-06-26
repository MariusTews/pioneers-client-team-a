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
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

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
    private final ErrorService errorService;
    private CompositeDisposable disposables;

    public TradeRequestController(InGameController inGameController, ResourceBundle bundle, PioneerService pioneerService, ErrorService errorService) {

        this.inGameController = inGameController;
        this.bundle = bundle;
        this.pioneerService = pioneerService;
        this.errorService = errorService;
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
        return parent;
    }

    public void acceptRequest(ActionEvent actionEvent) {
        /*
        errorService.setErrorCodesTradeController();
        disposables.add(pioneerService.createMove("build", null, "62b6ca620fbbbb001440fad2", null, null)
                .observeOn(FX_SCHEDULER).
                subscribe(move -> System.out.println("Erfolgreich"), errorService::handleError
                ));
         */
        inGameController.closeRequestMenu(false);
    }

    public void declineRequest(ActionEvent actionEvent) {
        inGameController.closeRequestMenu(false);
    }
}
