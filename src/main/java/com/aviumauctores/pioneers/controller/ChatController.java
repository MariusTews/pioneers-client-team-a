package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.MessageService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.Optional;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;


public class ChatController implements Controller {

    private final App app;
    private final Provider<LobbyController> lobbyController;
    private final MessageService messageService;

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
    public ChatController(App app, Provider<LobbyController> lobbyController, MessageService messageService) {
        this.app = app;
        this.lobbyController = lobbyController;
        this.messageService = messageService;
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
        //send Message with Enter
        chatTextField.setOnKeyPressed(event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                sendMessage();
            }
        } );


        return parent;
    }

    public void sendMessage() {
        // clear the field
        String message = chatTextField.getText();
        chatTextField.clear();
        if (message.isBlank()) {
            return;
        }
        //
        /*messageService.send(message)
                .observeOn(FX_SCHEDULER)
                .subscribe(result ->
                // later with websocket
                ((VBox)((ScrollPane)this.allTab.getContent()).getContent()).getChildren().add(new Label(result)));
        */

        // add a new Label with the message
        Label msgLabel = createMessageLabel(message);
        ((VBox)((ScrollPane)this.allTab.getContent()).getContent()).getChildren().add(msgLabel);



    }

    // clicked Label will be removed
    public void delete(Label msgLabel) {
        ((VBox)((ScrollPane)this.allTab.getContent()).getContent()).getChildren().remove(msgLabel);
    }

    public void leave() {
        // back to LobbyScreen
        final LobbyController controller = lobbyController.get();
        app.show(controller);
    }

    // Function to create a new label for the message with the needed functions
    public Label createMessageLabel(String message) {
        Label msgLabel = new Label(message);
        msgLabel.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                // Alert: do you want to delete this message?
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete");
                alert.setHeaderText("Delete this Message?");
                alert.setContentText("Do you want to delete this message?");
                Optional<ButtonType> result = alert.showAndWait();
                // delete if "Ok" is clicked
                if (result.get() == ButtonType.OK){
                    delete(msgLabel);
                    alert.close();
                } else {
                    alert.close();
                }
            }
        });
        return msgLabel;
    }

}
