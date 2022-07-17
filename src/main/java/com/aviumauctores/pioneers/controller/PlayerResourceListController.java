package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.service.ColorService;
import com.aviumauctores.pioneers.service.GameMemberService;
import com.aviumauctores.pioneers.service.PioneerService;
import com.aviumauctores.pioneers.service.UserService;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.ResourceBundle;


@Singleton
public class PlayerResourceListController {

    private final UserService userService;
    private final PioneerService pioneerService;
    private final ColorService colorService;
    private final ResourceBundle bundle;


    private final GameMemberService gameMemberService;
    public ListView<HBox> playerList;
    private String currentPlayerID;
    private final HashMap<String, PlayerResourceListItemController> listItems = new HashMap<>();
    private Player player;
    private Player longestRoadPlayer;

    @Inject
    public PlayerResourceListController(UserService userService, PioneerService pioneerService, ColorService colorService, ResourceBundle bundle, GameMemberService gameMemberService) {
        this.userService = userService;
        this.pioneerService = pioneerService;
        this.colorService = colorService;
        this.bundle = bundle;
        this.gameMemberService = gameMemberService;
    }

    public void init(ListView<HBox> node, String startingPlayer) {
        this.playerList = node;
        this.currentPlayerID = startingPlayer;
        for (Player p : pioneerService.listPlayers().blockingFirst()) {
            createPlayerBox(p);
        }

        for (Member m : gameMemberService.listCurrentGameMembers().blockingFirst()) {
            if (m.spectator()) {
                createSpectatorBox(m);
            }
        }
        playerList.setPadding(new Insets(10, 0, 10, 0));
    }

    public void createPlayerBox(Player player) {
        String playerID = player.userId();
        String playerName = userService.getUserName(playerID).blockingFirst();
        String colorName = colorService.getColor(player.color());
        PlayerResourceListItemController controller = new PlayerResourceListItemController(player, playerName, colorName, userService, bundle);
        listItems.put(playerID, controller);
        playerList.getItems().add(playerList.getItems().size(), controller.createBox());

        if (playerID.equals(this.currentPlayerID)) {
            controller.showArrow();
        }

    }

    public void createSpectatorBox(Member member) {
        String playerID = member.userId();
        String playerName = userService.getUserName(playerID).blockingFirst();
        String colorName = colorService.getColor(member.color());

        PlayerResourceListItemController controller = new PlayerResourceListItemController(member, playerName, colorName, userService, bundle);

        playerList.getItems().add(playerList.getItems().size(), controller.createSpectatorBox());

        listItems.put(playerID, controller);

    }


    public void updatePlayerLabel(Player player) {
        PlayerResourceListItemController controller = listItems.get(player.userId());
        controller.setPlayer(player);
        controller.updateResources();
    }

    public void updateOwnResources(Label[] labels, String[] resources) {
        for (int i = 0; i < resources.length; i++) {
            updateLabel(labels[i], resources[i]);
        }
    }

    public void updateLabel(Label label, String resource) {
        HashMap<String, Integer> resources = player.resources();
        if (resources.containsKey(resource)) {
            label.setText(Integer.toString(resources.get(resource)));
        } else {
            label.setText("0");
        }
    }

    public void onPlayerTurn() {
        //Sets current player at the top of the list. List is sorted in move order
        int size = playerList.getItems().size();
        HBox currentPlayerBox = listItems.get(currentPlayerID).getPlayerBox();
        HBox puffer = playerList.getItems().set(0, currentPlayerBox);
        for (int i = size - 1; i > 0; i--) {
            puffer = playerList.getItems().set(i, puffer);
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void hideArrow(String playerID) {
        listItems.get(playerID).hideArrow();
    }

    public void showArrow(String playerID) {
        listItems.get(playerID).showArrow();
    }

    public void setCurrentPlayerID(String currentPlayerID) {
        this.currentPlayerID = currentPlayerID;
    }

    public int getResource(HashMap<String, Integer> resources, String resourceName) {
        return resources.getOrDefault(resourceName, 0);
    }

    public void setLongestRoad(Player player) {
        if (longestRoadPlayer != null) {
            listItems.get(longestRoadPlayer.userId()).setLongestRoadViewInvisible();
        }
        listItems.get(player.userId()).setLongestRoadViewVisible();
        longestRoadPlayer = player;
    }

    public int getResources(String playerId){
        return listItems.get(playerId).getAllResources();
    }

    public int getPreviousResources(String playerId){
        return listItems.get(playerId).getPreviousResources();
    }

}





