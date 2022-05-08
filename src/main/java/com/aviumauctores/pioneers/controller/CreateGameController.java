package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.GameService;
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
import java.io.IOException;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;


public class CreateGameController implements Controller {

    private final App app;


    public final SimpleStringProperty gameName = new SimpleStringProperty();
    public final SimpleStringProperty password = new SimpleStringProperty();

    private final Provider<LobbyController> lobbyController;

    private final GameService gameService;

    private boolean hidePassword = true;

    @FXML public Button showPasswordButton;

    @FXML public TextField gamePasswordText;

    @FXML public Button cancelButton;

    @FXML public Button createGameButton;

    @FXML public PasswordField gamePasswordInput;

    @FXML public TextField gameNameInput;

    @Inject
    public CreateGameController(App app, Provider<LobbyController> lobbyController,
                                GameService gameService){
        this.app = app;
        this.lobbyController = lobbyController;
        this.gameService = gameService;
    }

    public void init(){
    }



    public void destroy(){

    }

    @Override
    public Parent render(){
        //load createGame FXML
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/createGameScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        }catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        //check if one or both input fields are empty
        createGameButton.disableProperty().bind(
                Bindings.createBooleanBinding(() ->
                                gameNameInput.getText().trim().isEmpty(), gameNameInput.textProperty())
                        .or(Bindings.createBooleanBinding(() ->
                                gamePasswordInput.getText().trim().isEmpty(), gamePasswordInput.textProperty()))
        );

        //take username and password from Login screen
        gameNameInput.textProperty().bindBidirectional(gameName);
        gamePasswordInput.textProperty().bindBidirectional(password);
        return parent;
    }

    public void createGame(ActionEvent actionEvent){
        //create a new Game
        String name = gameNameInput.getText();
        String password = gamePasswordInput.getText();
        //check if name length is valid
        if(!(name.length() > 0 && name.length() <= 32)){
            return;
        }
        gameService.create(name, password)
                .observeOn(FX_SCHEDULER)
                .subscribe();
        //show gameReady Screen
        final GameReadyController controller = new GameReadyController(app,lobbyController);
        app.show(controller);
    }



    public void cancel(ActionEvent actionEvent) {
        //change view to LobbyScreen
        final LobbyController controller = lobbyController.get();
        app.show(controller);
    }

    public void showPassword(ActionEvent actionEvent) {
        //check button status
        if(gamePasswordInput.getText().isEmpty()){
            return;
        }
        Image image;
        //set source for Image and show/hide password depending on hidePassword
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
        //set Image to showPasswordButton
        ImageView view = new ImageView(image);
        view.setFitHeight(23.0);
        view.setFitWidth(25.0);
        showPasswordButton.setGraphic(view);
    }

}
