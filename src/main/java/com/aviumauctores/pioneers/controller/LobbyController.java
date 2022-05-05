package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.LoginService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

public class LobbyController implements Controller {

    private final App app;
    private final Provider<LoginController> loginController;
    private final Provider<ChatController> chatController;
    private final Provider<CreateGameController> createGameController;

    @FXML public Label gameLabel;

    @FXML public ListView gameListView;

    @FXML public Label playerLabel;

    @FXML public ListView playerListView;

    @FXML public Button createGameButton;

    @FXML public Button chatButton;

    @FXML public Button quitButton;
    @Inject
    public LobbyController(App app, Provider<LoginController> loginController, Provider<ChatController> chatController, Provider<CreateGameController> createGameController){
        this.app = app;
        this.loginController = loginController;
        this.chatController = chatController;
        this.createGameController = createGameController;
    }




    public void init(){

    }

    public void destroy(){

    }

    public Parent render(){
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/lobbyScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        }catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        return parent;
    }

    public void toCreateGame(ActionEvent event) {
        final CreateGameController controller = createGameController.get();
        app.show(controller);
    }


    public void toChat(ActionEvent event) {

        final ChatController controller = chatController.get();
        app.show(controller);
    }

    public void quit(ActionEvent event) {
        //maybe dont go to login but instead directly quit the application?
        final LoginController controller = loginController.get();
        app.show(controller);
    }
}
