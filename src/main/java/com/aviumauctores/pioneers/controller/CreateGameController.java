package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.CreateGameService;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.UserService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;


public class CreateGameController implements Controller {

    private final App app;


    public final SimpleStringProperty gameName = new SimpleStringProperty();
    public final SimpleStringProperty password = new SimpleStringProperty();

    private boolean hidePassword = true;

    @FXML public TextField gamePasswordText;

    @FXML public Button cancelButton;

    @FXML public Button createGameButton;

    @FXML public PasswordField gamePasswordInput;

    @FXML public TextField gameNameInput;

    public CreateGameController(App app){
        this.app = app;

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
        GameReadyController controller = new GameReadyController(app);
        app.show(controller);
    }

    public void cancel(ActionEvent actionEvent) {
        final LobbyController controller = new LobbyController(app);
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
