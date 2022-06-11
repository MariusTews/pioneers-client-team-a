package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.model.State;
import com.aviumauctores.pioneers.service.*;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.util.*;



public class PlayerResourceListController {


    private final GameService gameService;
    private final UserService userService;
    private final PioneerService pioneerService;
    private final ColorService colorService;

    public VBox playerListVBox;

    private CompositeDisposable disposables;


    private ObservableList<Node> listElements;

    private  Observable<State> state;
    private String currentPlayerID;
    private HashMap<String, PlayerResourceListItemController> listItems = new HashMap<>();
    private Player player;

    @Inject
    public PlayerResourceListController(UserService userService, GameService gameService, PioneerService pioneerService,
                                        ColorService colorService)
    {
       this.userService = userService;
       this. gameService = gameService;
       this.pioneerService = pioneerService;
       this.colorService = colorService;
    }

    public void init(VBox node, String startingPlayer){
        disposables = new CompositeDisposable();
        this.playerListVBox = node;
        this.currentPlayerID = startingPlayer;
        for(Player p : pioneerService.listPlayers().blockingFirst()){
            createPlayerBox(p);
        }
        playerListVBox.setPadding(new Insets(10,0,2,20));
        playerListVBox.setSpacing(10.0);
        this.listElements = playerListVBox.getChildren();

    }

    public void createPlayerBox(Player player){

        String playerID = player.userId();
        String playerName = userService.getUserName(playerID).blockingFirst();
        String colorName = colorService.getColor(player.color());
        PlayerResourceListItemController controller = new PlayerResourceListItemController(player, playerName, colorName, userService);
        listItems.put(playerID, controller);
        playerListVBox.getChildren().add(controller.createBox());
        if(playerID.equals(this.currentPlayerID)){
            controller.showArrow();
        }
    }

    public void updateResourceList(){
        for (String playerID : listItems.keySet()){
            Player player = pioneerService.getPlayer(playerID).blockingFirst();
            updatePlayerLabel(player);
        }
    }

    public void updatePlayerLabel(Player player){
        listItems.get(player.userId()).setPlayer(player);
        listItems.get(player.userId()).updateResources(player);
    }

    public void updateOwnResources(Label[] labels, String[] resources){
        for(int i = 0 ; i < resources.length; i++){
            updateLabel(labels[i], resources[i]);
        }


    }

    public void updateLabel(Label label, String resource){
        HashMap<String, Integer> resources = player.resources();
        if(resources.containsKey(resource)){
            label.setText(Integer.toString(resources.get(resource)));
        }else{
            label.setText("0");
        }


    }

    public void setPlayer(Player player){
        this.player = player;
    }



    public void hideArrow(Player player){
        listItems.get(player.userId()).hideArrow();
    }

    public void showArrow(Player player){
        listItems.get(player.userId()).showArrow();
    }
}






