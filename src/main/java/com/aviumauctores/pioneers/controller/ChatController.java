package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.GroupService;
import com.aviumauctores.pioneers.service.MessageService;
import com.aviumauctores.pioneers.service.UserService;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.time.LocalDateTime;
import java.util.*;

import static com.aviumauctores.pioneers.Constants.ALLCHAT_ID;
import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;


public class ChatController extends PlayerListController {

    private final App app;
    private final Provider<LobbyController> lobbyController;
    private final MessageService messageService;

    private final UserService userService;

    private final GroupService groupService;
    private final ResourceBundle bundle;

    private final EventListener eventListener;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private final ObservableList<User> users = FXCollections.observableArrayList();

    private List<User> onlineUsers = new ArrayList<>();

    private List<User> allUsers = new ArrayList<>();

    private List<String> usersIdList  = new ArrayList<>();

    private User user;

    private Label deleteLabel;

    private Tab selectedTab;

    @FXML public TextField chatTextField;
    @FXML public Button sendButton;
    @FXML public ListView<Parent> onlinePlayerList;
    @FXML public Button leaveButton;
    @FXML public Label hintLabel;
    @FXML public Label onlinePlayerLabel;
    @FXML public Tab allTab;
    @FXML public TabPane chatTabPane;

    @FXML public VBox allChatVBox;

    @FXML public ScrollPane scrollPane;

    private final Map<String, Tab> chatTabsByUserID = new HashMap<>();

    @Inject
    public ChatController(App app, Provider<LobbyController> lobbyController, MessageService messageService,
                          EventListener eventListener, UserService userService, GroupService groupService,
                          ResourceBundle bundle) {
        this.app = app;
        this.lobbyController = lobbyController;
        this.messageService = messageService;
        this.eventListener = eventListener;
        this.userService = userService;
        this.groupService = groupService;
        this.bundle = bundle;
    }

    public void init(){
        //get all users for the usernames of the old messages
        disposable.add(userService.findAll().observeOn(FX_SCHEDULER).subscribe(res -> {
            allUsers = res;
            showOldMessages("groups", ALLCHAT_ID,LocalDateTime.now().toString() , 100);
        }));


        // get all users, their ids and update the All-Group
        disposable.add(userService.listOnlineUsers().observeOn(FX_SCHEDULER).subscribe(result -> { onlineUsers = result;
            usersIdList = getAllUserIDs(onlineUsers);
            usersIdList.add(userService.getCurrentUserID());
            groupService.updateGroup(ALLCHAT_ID, usersIdList).subscribe();
        }));
        disposable.add(userService.listOnlineUsers().observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    this.users.setAll(result);
                    usersIdList = getAllUserIDs(users);
                    usersIdList.add(userService.getCurrentUserID());
                    groupService.updateGroup(ALLCHAT_ID, usersIdList).subscribe();
                    result.forEach(this::addPlayerToList);
                    if (onlinePlayerLabel != null) {
                        updatePlayerLabel();
                    }
                }));
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
                    // Update user list
                    onUserEvent(event);
                }));
        // listen for incoming messages and show them as a Label
        disposable.add(eventListener.listen("groups." + ALLCHAT_ID + ".messages.*.*", Message.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
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

    @Override
    protected void updatePlayerLabel() {
        onlinePlayerLabel.setText(String.format("Online Spieler (%d)", playerItems.size()));
    }

    public void destroy(){
        disposable.dispose();
        chatTabsByUserID.clear();
    }

    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/ChatScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        allTab.setId(ALLCHAT_ID);
        allTab.setOnSelectionChanged(event -> {
            if (event.getTarget().equals(allTab)) {
                this.selectedTab = allTab;
            }
        });
        this.selectedTab = allTab;
        sendButton.setOnAction(event -> sendMessage(selectedTab.getId()));
        leaveButton.setOnAction(event -> leave());
        //send Message with Enter
        chatTextField.setOnKeyPressed(event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                sendMessage(selectedTab.getId());
            }
        });
        onlinePlayerList.setItems(playerItems);
        updatePlayerLabel();
        return parent;
    }

    public void sendMessage(String groupId) {
        // clear the field
        String message = chatTextField.getText();
        chatTextField.clear();
        if (message.isBlank()) {
            return;
        }
        // send the message
        messageService.sendGroupMessage(message, groupId)
                .observeOn(FX_SCHEDULER)
                .subscribe();
    }

    // delete the message
    public void delete(String messageId, String groupId) {
        messageService.deleteMessage(messageId, groupId)
                .observeOn(FX_SCHEDULER)
                .subscribe();
    }



    public void leave() {
        // back to LobbyScreen
        final LobbyController controller = lobbyController.get();
        controller.setUser(user);
        app.show(controller);
    }

    @Override
    public void onPlayerItemClicked(User selectedUser) {
        Tab userTab = chatTabsByUserID.get(selectedUser._id());
        if (userTab != null) {
            // There is already a chat tab
            chatTabPane.getSelectionModel().select(userTab);
            return;
        }
        disposable.add(groupService.getOrCreateGroup(List.of(userService.getCurrentUserID(), selectedUser._id()), disposable)
                .observeOn(FX_SCHEDULER)
                .subscribe(group -> {
                    disposable.add(eventListener.listen("groups." + group._id() + ".messages.*.*", Message.class)
                            .observeOn(FX_SCHEDULER)
                            .subscribe(event -> {

                                for (Tab t: chatTabPane.getTabs()) {
                                    if (t.getId().equals(group._id())) {
                                        selectedTab = t;
                                    }
                                }
                                if (event.event().endsWith(".created")) {
                                    Label msgLabel = createMessageLabel(event.data());
                                    ((VBox)((ScrollPane)this.selectedTab.getContent()).getContent()).getChildren()
                                            .add(msgLabel);
                                }
                                else if (event.event().endsWith(".deleted")) {
                                    //search for the Label of the which will be deleted
                                    for (Node l : ((VBox)((ScrollPane)this.selectedTab.getContent()).getContent()).getChildren()) {
                                        if (event.data()._id().equals(l.getId())) {
                                            this.deleteLabel = (Label) l;
                                        }
                                    }
                                    ((VBox)((ScrollPane)this.selectedTab.getContent()).getContent()).getChildren()
                                            .remove(this.deleteLabel);

                                }
                            }));
                    Tab tab = new Tab(selectedUser.name());
                    ScrollPane sp = new ScrollPane();
                    sp.setContent(new VBox());
                    tab.setContent(sp);
                    tab.setId(group._id());
                    tab.setClosable(true);
                    tab.setOnSelectionChanged(event -> {
                        if (event.getTarget().equals(tab)) {
                            this.selectedTab = tab;
                        }
                    });
                    chatTabPane.getTabs().add(tab);
                    chatTabPane.getSelectionModel().select(tab);
                    chatTabsByUserID.put(selectedUser._id(), tab);
                    showOldMessages("groups",tab.getId(),LocalDateTime.now().toString(), 100);
                }));
    }


    public Label createMessageLabel(Message message) {
        // get the username of the sender
        Label msgLabel = new Label();
        for (User u : allUsers ) {
            if (u._id().equals(message.sender())) {
                msgLabel.setText(u.name() + ": " + message.body());
            }
        }
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
        disposable.add(messageService.listMessages(namespace, pathId, createdBefore, limit)
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    for (Message m : result) {
                        Label msgLabel = createMessageLabel(m);
                        // the label could be Blank if the user was deleted
                        if (!msgLabel.getText().isBlank()) {
                            ((VBox) ((ScrollPane) this.selectedTab.getContent()).getContent()).getChildren()
                                    .add(msgLabel);
                        }
                    }
                }));
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
                delete(this.deleteLabel.getId(), this.selectedTab.getId());
                alert.close();
            } else {
                alert.close();
            }
        }

    }

}