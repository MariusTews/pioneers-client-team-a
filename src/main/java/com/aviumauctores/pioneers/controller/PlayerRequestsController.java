package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.service.ColorService;
import com.aviumauctores.pioneers.service.PioneerService;
import com.aviumauctores.pioneers.service.UserService;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.ResourceBundle;


public class PlayerRequestsController implements Controller{
    private PioneerService pioneerService;
    private UserService userService;
    private ColorService colorService;
    private ResourceBundle bundle;

    public ListView<HBox> playerList;
    private String currentPlayerID;
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
            createPlayerBox(p);
        }
        playerList.setPadding(new Insets(10, 0, 10, 0));
    }

    private void createPlayerBox(Player player) {
        String playerID = player.userId();
        String playerName = userService.getUserName(playerID).blockingFirst();
        String colorName = colorService.getColor(player.color());
        PlayerRequestsItemController controller = new PlayerRequestsItemController(player, playerName, colorName, userService, bundle);
        listItems.put(playerID, controller);
        playerList.getItems().add(playerList.getItems().size(), controller.createBox());

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
}
