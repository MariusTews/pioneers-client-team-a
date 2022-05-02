package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;


public class ChatController implements Controller {

    private final App app;
    private final Provider<LobbyController> lobbyController;

    @FXML public TextField chatTextField;
    @FXML public Button sendButton;
    @FXML public ListView onlinePlayerList;
    @FXML public Button leaveButton;
    @FXML public ScrollBar chatScrollBar;
    @FXML public Label hintLabel;
    @FXML public Label onlinePlayerLabel;
    @FXML public Tab allTab;
    @FXML public TabPane chatTabPane;

    @Inject
    public ChatController(App app, Provider<LobbyController> lobbyController) {
        this.app = app;
        this.lobbyController = lobbyController;
    }

    public void init(){

    }

    public void destroy(){

    }

    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/ChatScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        sendButton.setOnAction(event -> sendMessage());
        leaveButton.setOnAction(event -> leave());

        return parent;
    }

    public void sendMessage() {
        // clear the field
        String message = chatTextField.getText();
        chatTextField.clear();
        // placeholder, later with websocket
        ((VBox)((ScrollPane)this.allTab.getContent()).getContent()).getChildren().add(new Label(message));


    }

    public void leave() {
        // back to LobbyScreen
        final LobbyController controller = lobbyController.get();
        app.show(controller);
    }

}
