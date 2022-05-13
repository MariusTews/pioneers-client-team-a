package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.groups.CreateGroupDto;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.service.GameMemberService;
import com.aviumauctores.pioneers.service.GameService;
import com.aviumauctores.pioneers.service.GroupService;
import io.reactivex.rxjava3.core.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javax.inject.Provider;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameReadyController implements Controller {


    private final App app;
    private final Provider<LobbyController> lobbyController;

    private String ownerID;

    public List<String> player = new ArrayList();

    private final GameService gameService;

    private Observable<Game> game;

    public GroupService groupService;

    @FXML public Button startGameButton;

    @FXML public Button gameReadyButton;

    @FXML public Button leaveGameButton;

    @FXML public Label gameNameLabel;

    @FXML public Button sendMessageButton;

    @FXML public AnchorPane chatPane;

    @FXML public Tab allChatTab;

    @FXML public TitledPane playerListPane;

    @Inject
    public GameReadyController(App app, Provider<LobbyController> lobbyController,
                               GameService gameService, GroupService groupService){

        this.app = app;
        this.lobbyController = lobbyController;
        this.gameService = gameService;
        this.groupService = groupService;
        this.game = gameService.getCurrentGame();
        player.add(gameService.getCurrentGame().blockingFirst().owner());
        groupService.createGroup(player);
        System.out.println(player);
    }

    public void init(){
        
    }

    public void destroy(){

    }

    public Parent render(){
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/gameReadyScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        }catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        gameReadyButton.setText("Ready");
        return parent;
    }

    public void startGame(ActionEvent actionEvent) {
        //TODO: check if the Game has 4 members
        //TODO: check if all players are ready
        //TODO: start the game


    }

    public void gameReady(ActionEvent actionEvent) {
        //TODO: get the User clicking on the button
        //TODO: check his ready status
        //TODO: change the status
    }

    public void leaveGame(ActionEvent actionEvent) {
        //TODO: check if the Person clicking the button is the Owner
        //TODO: if it is the owner, show an alert
        //TODO: else leave the Game
        final LobbyController controller = lobbyController.get();
        app.show(controller);
    }

    public void sendMessage(ActionEvent actionEvent) {

    }
}
