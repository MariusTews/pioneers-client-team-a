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
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import com.aviumauctores.pioneers.ws.EventListener;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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

    private final EventListener eventListener;

    private CompositeDisposable disposable = new CompositeDisposable();

    private List<User> users = new ArrayList<>();

    private List<String> usersIdList  = new ArrayList<>();

    private User user;

    private String username;

    private Label deleteLabel;

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
        showOldMessages("groups", ALLCHAT_ID,LocalDateTime.now().toString() , 100);

        // get all users, their ids and update the All-Group
        userService.listOnlineUsers().observeOn(FX_SCHEDULER).subscribe(result -> { users = result;
            usersIdList = getAllUserIDs(users);
            usersIdList.add(user._id());
            groupService.updateGroup(ALLCHAT_ID, usersIdList).subscribe();
        });
        // listen for users and put them in the All-Group for the All-Chat
        disposable.add(eventListener.listen("users.*.updated", User.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    if (event.data().status().equals("online")) {
                        //update AllGroup
                        usersIdList.add(event.data()._id());
                        groupService.updateGroup(ALLCHAT_ID, usersIdList).subscribe();
                    }
                    if (event.data().status().equals("offline")) {
                        usersIdList.remove(event.data()._id());
                        groupService.updateGroup(ALLCHAT_ID, usersIdList).subscribe();
                    }
                }));
        // listen for incoming messages and show them as a Label
        disposable.add(eventListener.listen("groups.*.messages.*.*", Message.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    //Label msgLabel = createMessageLabel(event.data());
                    if (event.event().endsWith(".created")) {
                        Label msgLabel = createMessageLabel(event.data());
                        ((VBox)((ScrollPane)this.allTab.getContent()).getContent()).getChildren()
                                .add(msgLabel);
                    }
                    else if (event.event().endsWith(".deleted")) {
                        //search for the Label of the which will be deleted
                        for (Node l : ((VBox)((ScrollPane)this.allTab.getContent()).getContent()).getChildren()) {
                            if (event.data()._id().equals(l.getId())) {
                                this.deleteLabel = (Label) l;
                            }
                        }
                        ((VBox)((ScrollPane)this.allTab.getContent()).getContent()).getChildren()
                                .remove(this.deleteLabel);

                    }
                }));
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
        controller.setUser(user);
        app.show(controller);
    }



    // Function to create a new label for the message with the needed functions
    public Label createMessageLabel(Message message) {
        // get the username of the sender
        Label msgLabel = new Label();
        /*userService.getUserName(message.sender()).observeOn(FX_SCHEDULER).subscribe(result -> {
            username = result;
            msgLabel.setText(username + ": " + message.body());
        });*/
        msgLabel.setText(message.sender() + ": " + message.body());
        msgLabel.setOnMouseClicked(this::onMessageClicked);
        msgLabel.setId(message._id());
        return msgLabel;
    }



    public List<String> getAllUserIDs(List<User> users) {
        List<String> ids = new ArrayList<>();
        for (User u : users) {
            ids.add(u._id());
        }
        return ids;
    }

    public void showOldMessages(String namespace, String pathId, String createdBefore, int limit) {
        messageService.listMessages(namespace, pathId, createdBefore, limit)
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    for (Message m : result) {
                        //createMessageLabel(m);
                        ((VBox)((ScrollPane)this.allTab.getContent()).getContent()).getChildren()
                                .add(createMessageLabel(m));
                    }
                });
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void onMessageClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            // Alert for the delete
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete");
            alert.setHeaderText("Delete this Message?");
            alert.setContentText("Do you want to delete this message?");
            Optional<ButtonType> res = alert.showAndWait();
            // delete if "Ok" is clicked
            if (res.get() == ButtonType.OK){
                this.deleteLabel = (Label) event.getSource();
                delete(this.deleteLabel.getId());
                alert.close();
            } else {
                alert.close();
            }
        }

    }
}
