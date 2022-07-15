package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.service.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import static com.aviumauctores.pioneers.Constants.*;

public class StatController extends LoggedInController{

    private final App app;
    private final GameService gameService;
    private  List<Player> players;
    private final ResourceBundle bundle;
    private final Provider<PostGameController> postGameController;
    private final StatService statService;
    private final PioneerService pioneerService;
    private int playerAmount;
    private int firstPlayerIndex;
    private int lastPlayerIndex;
    private VBox[] containers;
    public VBox statNameContainer;
    public VBox playerContainer1;
    public VBox playerContainer2;
    public VBox playerContainer3;
    public VBox playerContainer4;
    public Button goLeftButton;
    public Button goRightButton;
    public Button leaveButton;

    @Inject
    public StatController(App app, UserService userService, LoginService loginService, GameService gameService,
                          ResourceBundle bundle, Provider<PostGameController> postGameController, StatService statService,
                          PioneerService pioneerService){
        super(loginService, userService);
        this.app = app;
        this.gameService = gameService;
        this.bundle = bundle;
        this.postGameController = postGameController;
        this.statService = statService;
        this.pioneerService = pioneerService;
    }

    @Override
    public void init() {
        playerAmount = gameService.getCurrentGame().blockingFirst().members();
        players = pioneerService.listPlayers().blockingFirst();
        firstPlayerIndex = 0;
        lastPlayerIndex = playerAmount < 4 ? playerAmount : 3;
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/statScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        containers = new VBox[]{playerContainer1, playerContainer2, playerContainer3, playerContainer4};
        for (int i = 0; i < lastPlayerIndex; i++){
            Player player = players.get(i);
            loadStats(player.userId(), containers[i]);
        }

        updateButtons();
        return parent;
    }

    public void toLeft(ActionEvent event) {
        buttonPressed(-1);
    }

    public void goRight(ActionEvent event) {
        buttonPressed(1);
    }

    public void buttonPressed(int direction){
        firstPlayerIndex += direction;
        lastPlayerIndex += direction;
        updateButtons();
        shift(direction);
    }

    private void shift(int direction){
        //shift to left = -1, shift to right = 1
        //reload player stats into the right VBox
        switch(direction){
            case 1 -> {
                for(int i = lastPlayerIndex -1 ,k = 2; i >= firstPlayerIndex; i--, k--){
                    loadStats(Integer.toString(i), containers[k]);
                }
                loadStats(Integer.toString(lastPlayerIndex), containers[3]);
            }
            case -1 -> {
                for (int i = firstPlayerIndex + 1, k = 1; i <= lastPlayerIndex; i++, k++){
                    loadStats(Integer.toString(i), containers[k]);
                }
                loadStats(Integer.toString(firstPlayerIndex), containers[0]);
            }
        }
    }

    public void loadStats(String playerId, VBox container){
        //each VBox has 7 elements, with the first being the name label
        HashMap<String, Integer> stats = statService.getStats(playerId);
        int k = 0;
        for (Node node : container.getChildren()) {
            Label label = (Label) node;
            if (label.getId().startsWith("player")) {
                label.setText(userService.getUserName(playerId).blockingFirst());
                continue;
            }
            label.setText(Integer.toString(stats.get(ALL_STAT_NAMES[k])));
            k++;
        }
    }

    public void loadStatsTest(String playerID, VBox container){
        HashMap<String, Integer> stats = statService.getStats(playerID);
        int x = 0;
        for (Node node : container.getChildren()) {
            Label label = (Label) node;
            if (label.getText().startsWith("player")) {
                label.setText("player");
                continue;
            }
            label.setText(Integer.toString(stats.get(ALL_STAT_NAMES[x])));
            x++;
        }
    }


    public void leave(ActionEvent event) {
        app.show(postGameController.get());
    }

    private void updateButtons(){
        //If a shift to a direction is possible, enable the responsible Button, else disable it
        boolean disableLeft = firstPlayerIndex == 0 || playerAmount <= 4;
        goLeftButton.setDisable(disableLeft);
        boolean disableRight = lastPlayerIndex == playerAmount -1 || playerAmount <= 4;
        goRightButton.setDisable(disableRight);
    }
}
