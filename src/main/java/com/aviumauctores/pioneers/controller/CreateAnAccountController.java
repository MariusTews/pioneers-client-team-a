package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLOutput;

public class CreateAnAccountController implements Controller {

    @FXML public TextField textfieldUsername;
    @FXML public Button accountErstellenButton;
    @FXML public Button verlassenButton;
    @FXML public Button anzeigenButton;
    @FXML public TextField textfieldPasswort;

    public void init(){

    }

    public void destroy(){

    }

    public Parent render(){
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/registerScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return parent;
    }

    public void anzeigen(ActionEvent event) {

    }

    public void verlassen(ActionEvent event) {
    }

    public void accountErstellen(ActionEvent event) {
        String username = textfieldUsername.getText();
        String passwort = textfieldPasswort.getText();
        System.out.println(username + " " + passwort);
    }
}
