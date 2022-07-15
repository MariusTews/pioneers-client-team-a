package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.service.ColorService;
import com.aviumauctores.pioneers.service.PioneerService;
import com.aviumauctores.pioneers.service.UserService;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Objects;


public class PlayerRequestsListController implements Controller {
    private final TradingController tradingController;
    private final PioneerService pioneerService;
    private final UserService userService;
    private final ColorService colorService;

    public ListView<HBox> playerList;
    private final HashMap<String, PlayerRequestsItemController> listItems = new HashMap<>();

    @Inject
    public PlayerRequestsListController(TradingController tradingController, PioneerService pioneerService, UserService userService, ColorService colorService) {
        this.tradingController = tradingController;

        this.pioneerService = pioneerService;
        this.userService = userService;
        this.colorService = colorService;
    }

    public void load(ListView<HBox> node, String self) {
        this.playerList = node;
        for (Player p : pioneerService.listPlayers().blockingFirst()) {
            if (!Objects.equals(p.userId(), self)) {
                createPlayerBox(p);
            }
        }
        playerList.setPadding(new Insets(10, 0, 10, 0));
        playerList.setOnMouseClicked(event -> {
            tradingController.removeListeners();
            tradingController.setBankTrade(false);
            tradingController.setRequestSpinnersReady();
            tradingController.setTradeSpinnersReady();
            tradingController.setSumRequest(0);
            tradingController.initSpinnersPrivateTrade();
            tradingController.enableButtons();
            tradingController.setAllBorderColorTransparent();
        });
    }

    private void createPlayerBox(Player player) {
        String playerID = player.userId();
        String playerName = userService.getUserName(playerID).blockingFirst();
        String colorName = colorService.getColor(player.color());
        PlayerRequestsItemController playerRequestsItemController = new PlayerRequestsItemController(player, playerName, colorName, userService);
        listItems.put(playerID, playerRequestsItemController);
        playerList.getItems().add(playerList.getItems().size(), playerRequestsItemController.createBox());

    }

    public void showRequestOpen(String playerID) {
        listItems.get(playerID).showRequestOpen();
    }

    public void showRequestAccepted(String playerID) {
        listItems.get(playerID).showRequestAccepted();
    }

    public void showRequestDeclined(String playerID) {
        listItems.get(playerID).showRequestDeclined();
    }

    public void showRequest(String playerID) {
        if (listItems.getOrDefault(playerID, null) != null) {
            listItems.get(playerID).showRequest();
        }

    }

    @Override
    public void init() {

    }

    @Override
    public void destroy(boolean closed) {
        for (PlayerRequestsItemController items : listItems.values()) {
            items.destroy(closed);
            items = null;
        }

    }

    @Override
    public Parent render() {
        return null;
    }

    public String getSelectedPlayer() {
        HBox hBox = playerList.getSelectionModel().getSelectedItem();
        if (hBox != null) {
            VBox vBox = (VBox) hBox.getChildren().get(2);
            Label name = (Label) vBox.getChildren().get(0);
            return name.getText();
        }
        return null;

    }
}