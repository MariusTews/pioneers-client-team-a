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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;


public class CreateGameController implements Controller {

    private final App app;

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
        return parent;

    }

    public void createGame(ActionEvent actionEvent) {
    }

    public void cancel(ActionEvent actionEvent) {
        final LobbyController controller = new LobbyController(app);
        app.show(controller);
    }

    public void showPassword(ActionEvent actionEvent) {
        if(hidePassword){
            String password = gamePasswordInput.getText();
            gamePasswordText.setVisible(true);
            gamePasswordInput.setVisible(false);
            gamePasswordText.setText(password);
            System.out.println(password);
            hidePassword = false;
            return;
        }
        gamePasswordText.setText("");
        gamePasswordText.setVisible(false);
        gamePasswordInput.setVisible(true);
        hidePassword = true;

    }
}
