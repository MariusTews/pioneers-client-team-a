package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import javax.inject.Inject;
import javax.inject.Provider;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class CreateGameController implements Controller {

    private final App app;


    public final SimpleStringProperty gameName = new SimpleStringProperty();
    public final SimpleStringProperty password = new SimpleStringProperty();

    private final Provider<LobbyController> lobbyController;

    private boolean hidePassword = true;

    @FXML public Button showPasswordButton;

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

    @Override
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
        createGameButton.disableProperty().bind(
                Bindings.createBooleanBinding(() ->
                                gameNameInput.getText().trim().isEmpty(), gameNameInput.textProperty())
                        .or(Bindings.createBooleanBinding(() ->
                                gamePasswordInput.getText().trim().isEmpty(), gamePasswordInput.textProperty()))
        );

        //take username and password from login screen
        gameNameInput.textProperty().bindBidirectional(gameName);
        gamePasswordInput.textProperty().bindBidirectional(password);
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

        Image image;
        if(hidePassword){
            image = new Image(Main.class.getResource("views/hidePassword.png").toString());
            String password = gamePasswordInput.getText();
            gamePasswordText.setVisible(true);
            gamePasswordInput.setVisible(false);
            gamePasswordText.setText(password);
            hidePassword = false;
        }else{
            image = new Image(Main.class.getResource("views/showPassword.png").toString());
            gamePasswordText.setText("");
            gamePasswordText.setVisible(false);
            gamePasswordInput.setVisible(true);
            hidePassword = true;
        }

        ImageView view = new ImageView(image);
        view.setFitHeight(23.0);
        view.setFitWidth(25.0);
        showPasswordButton.setGraphic(view);




    }
}
