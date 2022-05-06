package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
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

public class JoinGameController implements Controller {

    private final App app;
    private final Provider<LobbyController> lobbyController;
    @FXML public Label gameNameLabel;
    @FXML public PasswordField passwordTextField;
    @FXML public TextField showPasswordTextField;
    @FXML public Button showPasswordButton;
    @FXML public Button joinGameButton;
    @FXML public Button leaveButton;

    @Inject
    public JoinGameController(App app, Provider<LobbyController> lobbyController) {
        this.app = app;
        this.lobbyController = lobbyController;
    }

    public void init(){

    }

    public void destroy(){

    }

    public Parent render(){
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/joinGameScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        passwordTextField.textProperty().bindBidirectional(showPasswordTextField.textProperty());
        showPasswordTextField.setManaged(false);
        return parent;
    }

    public void showPassword(ActionEvent actionEvent) {
        boolean passwordShowed = showPasswordTextField.isVisible();
        passwordTextField.setVisible(passwordShowed);
        passwordTextField.setManaged(passwordShowed);
        showPasswordTextField.setVisible(!passwordShowed);
        showPasswordTextField.setManaged(!passwordShowed);
    }

    public void joinGame(ActionEvent actionEvent) {

    }

    public void quit(ActionEvent actionEvent) {
        app.show(lobbyController.get());
    }
}
