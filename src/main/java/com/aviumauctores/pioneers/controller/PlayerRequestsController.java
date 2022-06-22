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
import java.util.ResourceBundle;


public class PlayerRequestsController implements Controller{
    private final PioneerService pioneerService;
    private final UserService userService;
    private final ColorService colorService;
    private final ResourceBundle bundle;

    public ListView<HBox> playerList;
    private String currentPlayerID;

    private PlayerRequestsItemController playerRequestsItemController;
    private final HashMap<String, PlayerRequestsItemController> listItems = new HashMap<>();
    @Inject
    public PlayerRequestsController(PioneerService pioneerService, UserService userService, ColorService colorService, ResourceBundle bundle){

        this.pioneerService = pioneerService;
        this.userService = userService;
        this.colorService = colorService;
        this.bundle = bundle;
    }

    public void load(ListView<HBox> node, String startingPlayer) {
        this.playerList = node;
        this.currentPlayerID = startingPlayer;
        for (Player p : pioneerService.listPlayers().blockingFirst()) {
            if (!Objects.equals(p.userId(), currentPlayerID)){
                createPlayerBox(p);
            }
        }
        playerList.setPadding(new Insets(10, 0, 10, 0));
    }

    private void createPlayerBox(Player player) {
        String playerID = player.userId();
        String playerName = userService.getUserName(playerID).blockingFirst();
        String colorName = colorService.getColor(player.color());
        playerRequestsItemController = new PlayerRequestsItemController(player, playerName, colorName, userService, bundle);
        listItems.put(playerID, playerRequestsItemController);
        playerList.getItems().add(playerList.getItems().size(), playerRequestsItemController.createBox());

    }

    @Override
    public void init() {

    }

    @Override
    public void destroy(boolean closed) {

    }

    @Override
    public Parent render() {
        return null;
    }

    public String getSelectedPlayer() {
        HBox hBox = playerList.getSelectionModel().getSelectedItem();
        if (hBox != null) {
            VBox vBox = (VBox) hBox.getChildren().get(1);
            Label name = (Label) vBox.getChildren().get(0);
            return name.getText();
        }
        return null;

    }
}
