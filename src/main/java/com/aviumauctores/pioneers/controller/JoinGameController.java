package com.aviumauctores.pioneers.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class JoinGameController implements Controller {

    @FXML public Label gameNameLabel;
    @FXML public PasswordField passwordTextField;
    @FXML public Button showPasswordButton;
    @FXML public Button joinGameButton;
    @FXML public Button leaveButton;

    public void init(){

    }

    public void destroy(){

    }

    public Parent render(){
        return null;
    }

    public void showPassword(ActionEvent actionEvent) {

    }

    public void joinGame(ActionEvent actionEvent) {

    }

    public void quit(ActionEvent actionEvent) {

    }
}
