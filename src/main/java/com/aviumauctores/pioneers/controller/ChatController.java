package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.GroupService;
import com.aviumauctores.pioneers.service.MessageService;
import com.aviumauctores.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import com.aviumauctores.pioneers.ws.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.aviumauctores.pioneers.Constants.ALLCHAT_ID;
import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;


public class ChatController implements Controller {

    private final App app;
    private final Provider<LobbyController> lobbyController;
    private final MessageService messageService;

    private final UserService userService;

    private final GroupService groupService;

    private final ObservableList<User> users = FXCollections.observableArrayList();

    private final EventListener eventListener;

    private CompositeDisposable disposable = new CompositeDisposable();

    private List<String> usersIdList  = new ArrayList<>();

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
    public ChatController(App app, Provider<LobbyController> lobbyController, MessageService messageService,
                          EventListener eventListener, UserService userService, GroupService groupService) {
        this.app = app;
        this.lobbyController = lobbyController;
        this.messageService = messageService;
        this.eventListener = eventListener;
        this.userService = userService;
        this.groupService = groupService;
    }

    public void init(){
        // get all users, their ids and update the All-Group
        userService.findAll().observeOn(FX_SCHEDULER).subscribe(result -> {this.users.setAll(result);
            usersIdList = getAllUserIDs(users);
            groupService.updateGroup(ALLCHAT_ID, usersIdList).subscribe();
        });
        // listen for users and put them in the All-Group for the All-Chat
        disposable.add(eventListener.listen("users.*.*", User.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    if (event.event().endsWith(".created")) {
                        //update AllGroup
                        usersIdList.add(event.data()._id());
                        groupService.updateGroup(ALLCHAT_ID, usersIdList).subscribe();
                    }
                }));

        // listen for incoming messages and show them as a Label

    }

    public void destroy(){
        disposable.dispose();
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
        // send the message
        messageService.sendAllChat(message)
                .observeOn(FX_SCHEDULER)
                .subscribe();
    }

    // delete the message
    public void delete(String id) {
        messageService.deleteMessage(id)
                .observeOn(FX_SCHEDULER)
                .subscribe();
    }



    public void leave() {
        // back to LobbyScreen
        final LobbyController controller = lobbyController.get();
        app.show(controller);
    }



    // Function to create a new label for the message with the needed functions
    public Label createMessageLabel(Message message) {
        // get the username of the sender
        String username = message.sender();
        Label msgLabel = new Label(username + ": " + message.body());
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
                    delete(message._id());
                    alert.close();
                } else {
                    alert.close();
                }
            }
        });
        return msgLabel;
    }


    //Get all IDs of the created users
    public List<String> getAllUserIDs(ObservableList<User> users) {
        List<String> IDs = new ArrayList<>();
        for (User u : users) {
            IDs.add(u._id());
        }
        return IDs;
    }
}
