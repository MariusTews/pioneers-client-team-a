package com.aviumauctores.pioneers.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class LobbyController implements Controller {

    @FXML public Label gameLabel;

    @FXML public ListView gameListView;

    @FXML public Label playerLabel;

    @FXML public ListView playerListView;

    @FXML public Button createGameButton;

    @FXML public Button chatButton;

    @FXML public Button quitButton;


    public void init(){

    }

    public void destroy(){

    }

    public Parent render(){
        return new Parent(){
        };
    }

    public void toCreateGame(ActionEvent event) {

    }

    public void toChat(ActionEvent event) {

    }

    public void quit(ActionEvent event) {

    }
}
