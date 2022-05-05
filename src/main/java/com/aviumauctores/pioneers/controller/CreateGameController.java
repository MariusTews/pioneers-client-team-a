package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.CreateGameService;
import com.aviumauctores.pioneers.service.LoginService;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;


public class CreateGameController implements Controller {

    private final App app;


    public final SimpleStringProperty gameName = new SimpleStringProperty();
    public final SimpleStringProperty password = new SimpleStringProperty();
    private final Provider<LobbyController> lobbyController;

    private boolean hidePassword = true;

    @FXML public TextField gamePasswordText;

    @FXML public Button cancelButton;

    @FXML public Button createGameButton;

    @FXML public PasswordField gamePasswordInput;

    @FXML public TextField gameNameInput;

    @Inject
    public CreateGameController(App app, Provider<LobbyController> lobbyController){
        this.app = app;
        this.lobbyController = lobbyController;

    }

    public void init(){


    }

    public void destroy(){

    }

    public Parent render(){
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/createGameScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        }catch(IOException e) {
            e.printStackTrace();
            return null;
        }

       //TODO: Disable Button if one or more input fields are empty
        return parent;

    }

    public void createGame(ActionEvent actionEvent){

        //TODO: Create a game and transmit it to the Server
        GameReadyController controller = new GameReadyController(app,lobbyController);
        app.show(controller);
    }

    public void cancel(ActionEvent actionEvent) {
        final LobbyController controller = lobbyController.get();
        app.show(controller);
    }

    public void showPassword(ActionEvent actionEvent) {
        if(gamePasswordInput.getText().isEmpty()){
            return;
        }
        if(hidePassword){
            String password = gamePasswordInput.getText();
            gamePasswordText.setVisible(true);
            gamePasswordInput.setVisible(false);
            gamePasswordText.setText(password);
            hidePassword = false;
            return;
        }
        gamePasswordText.setText("");
        gamePasswordText.setVisible(false);
        gamePasswordInput.setVisible(true);
        hidePassword = true;

    }
}
