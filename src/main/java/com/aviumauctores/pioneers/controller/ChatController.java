package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.*;
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

    private final ErrorService errorService;
    private final GroupService groupService;
    private final ResourceBundle bundle;

    private final EventListener eventListener;

    private final ObservableList<User> users = FXCollections.observableArrayList();

    private List<User> allUsers = new ArrayList<>();

    private List<String> usersIdList = new ArrayList<>();

    private Label deleteLabel;

    private Message deleteMessage;

    private Tab selectedTab;


    @FXML
    public TextField chatTextField;
    @FXML
    public Button sendButton;
    @FXML
    public ListView<Parent> onlinePlayerList;
    @FXML
    public Button leaveButton;
    @FXML
    public Label hintLabel;
    @FXML
    public Label onlinePlayerLabel;
    @FXML
    public Tab allTab;
    @FXML
    public TabPane chatTabPane;

    @FXML
    public VBox allChatVBox;

    @FXML
    public ScrollPane scrollPane;

    private final Map<String, Tab> chatTabsByUserID = new HashMap<>();

    @Inject
    public ChatController(App app, LoginService loginService,
                          Provider<LobbyController> lobbyController, MessageService messageService,
                          ErrorService errorService, EventListener eventListener, UserService userService, GroupService groupService,
                          ResourceBundle bundle) {
        super(loginService, userService);
        this.app = app;
        this.lobbyController = lobbyController;
        this.messageService = messageService;
        this.errorService = errorService;
        this.eventListener = eventListener;
        this.groupService = groupService;
        this.bundle = bundle;
    }


    public void init() {
        errorService.setErrorCodesMessages();
        disposables = new CompositeDisposable();
        //get all users for the usernames of the old messages
        disposables.add(userService.findAll().observeOn(FX_SCHEDULER).subscribe(res -> {
            allUsers = res;
            showOldMessages("global", ALLCHAT_ID, LocalDateTime.now().toString(), 100);
        }, errorService::handleError));


        disposables.add(userService.listOnlineUsers().observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    this.users.setAll(result);
                    usersIdList = getAllUserIDs(users);
                    usersIdList.add(userService.getCurrentUserID());
                    result.forEach(this::addPlayerToList);
                    if (onlinePlayerLabel != null) {
                        updatePlayerLabel();
                    }
                }));
        // listen for users and put them in the All-Group for the All-Chat
        disposables.add(eventListener.listen("users.*.updated", User.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    if (event.data().status().equals("online")) {
                        usersIdList.add(event.data()._id());
                    }
                    if (event.data().status().equals("offline")) {
                        usersIdList.remove(event.data()._id());
                    }
                    // Update user list
                    onUserEvent(event);
                }));
        // listen for incoming messages and show them as a Label
        disposables.add(eventListener.listen("*." + ALLCHAT_ID + ".messages.*.*", Message.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    if (event.event().endsWith(".created")) {
                        Label msgLabel = createMessageLabel(event.data());
                        ((VBox) ((ScrollPane) this.allTab.getContent()).getContent()).getChildren()
                                .add(msgLabel);
                    } else if (event.event().endsWith(".deleted")) {
                        //search for the Label of the which will be deleted
                        for (Node l : ((VBox) ((ScrollPane) this.allTab.getContent()).getContent()).getChildren()) {
                            if (event.data()._id().equals(l.getId())) {
                                this.deleteLabel = (Label) l;
                            }
                        }
                        ((VBox) ((ScrollPane) this.allTab.getContent()).getContent()).getChildren()
                                .remove(this.deleteLabel);

                    }
                }));
    }


    @Override
    protected void updatePlayerLabel() {
        onlinePlayerLabel.setText(String.format(bundle.getString("online.players") + " (%d)", playerItems.size()));
    }


    public void destroy(boolean closed) {
        super.destroy(closed);
        chatTabsByUserID.clear();
    }


    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/chatScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        //press esc to leave
        leaveButton.setCancelButton(true);
        onlinePlayerList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                leave();
            }
        });

        // Tab-structure
        allTab.setId(ALLCHAT_ID);
        allTab.setOnSelectionChanged(event -> {
            if (event.getTarget().equals(allTab)) {
                this.selectedTab = allTab;
            }
        });
        chatTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        this.selectedTab = allTab;
        sendButton.setOnAction(event -> sendMessage(selectedTab.getId()));
        leaveButton.setOnAction(event -> leave());

        //send Message with Enter
        chatTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
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
        String namespace = "groups";
        if (groupId.equals(ALLCHAT_ID)) {
            namespace = "global";
        }
        // send the message
        disposables.add(messageService.sendGroupMessage(namespace, message, groupId)
                .observeOn(FX_SCHEDULER)
                .subscribe(r -> {
                }, errorService::handleError));
    }


    // delete the message
    public void delete(String messageId, String groupId) {
        String namespace = "groups";
        if (groupId.equals(ALLCHAT_ID)) {
            namespace = "global";
        }
        disposables.add(messageService.deleteMessage(namespace, messageId, groupId)
                .observeOn(FX_SCHEDULER)
                .subscribe(r -> {
                }, errorService::handleError));
    }


    public void leave() {
        // back to LobbyScreen
        final LobbyController controller = lobbyController.get();
        app.show(controller);
    }


    @Override
    public void onPlayerItemClicked(User selectedUser) {
        Tab userTab = chatTabsByUserID.get(selectedUser._id());
        if (userTab != null) {
            // there was a chat tab, but it was closed
            reopenTab(userTab);
            // There is already a chat tab
            chatTabPane.getSelectionModel().select(userTab);
            return;
        }
        errorService.setErrorCodesGroups();
        disposables.add(groupService.getOrCreateGroup(List.of(userService.getCurrentUserID(), selectedUser._id()), disposables)
                .observeOn(FX_SCHEDULER)
                .subscribe(group -> {
                    disposables.add(eventListener.listen("groups." + group._id() + ".messages.*.*", Message.class)
                            .observeOn(FX_SCHEDULER)
                            .subscribe(event -> {
                                //check if the Group/Tab, were is the event happened is open
                                Tab tab = this.selectedTab;
                                boolean notOpen = true;
                                for (Tab t : chatTabPane.getTabs()) {
                                    //search the right tab
                                    if (t.getId().equals(group._id())) {
                                        tab = t;
                                        notOpen = false;
                                    }
                                }
                                if (notOpen) {
                                    return;
                                }
                                //add or remove the Message
                                if (event.event().endsWith(".created")) {
                                    Label msgLabel = createMessageLabel(event.data());
                                    ((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren()
                                            .add(msgLabel);
                                } else if (event.event().endsWith(".deleted")) {
                                    //search for the Label of the which will be deleted
                                    for (Node l : ((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren()) {
                                        if (event.data()._id().equals(l.getId())) {
                                            this.deleteLabel = (Label) l;
                                        }
                                    }
                                    ((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren()
                                            .remove(this.deleteLabel);
                                }
                            }));
                    Tab tab = createTab(group._id(), selectedUser);
                    chatTabPane.getTabs().add(tab);
                    chatTabPane.getSelectionModel().select(tab);
                    chatTabsByUserID.put(selectedUser._id(), tab);
                    showOldMessages("groups", tab.getId(), LocalDateTime.now().toString(), 100);
                }, errorService::handleError));
    }


    public Label createMessageLabel(Message message) {
        // get the username of the sender
        Label msgLabel = new Label();
        for (User u : allUsers) {
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
        errorService.setErrorCodesMessages();
        disposables.add(messageService.listMessages(namespace, pathId, createdBefore, limit)
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
                }, errorService::handleError));
    }


    public void onMessageClicked(MouseEvent event) {
        errorService.setErrorCodesMessages();
        if (event.getButton() == MouseButton.SECONDARY) {
            // Alert for removal of a message, only if you click on your own message
            Label msg = (Label) event.getSource();
            String namespace = "groups";
            if (selectedTab.getId().equals(ALLCHAT_ID)) {
                namespace = "global";
            }
            disposables.add(messageService.getMessage(namespace, selectedTab.getId(), msg.getId())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(res -> {
                        deleteMessage = res;
                        if (!userService.getCurrentUserID().equals(deleteMessage.sender())) {
                            return;
                        }
                        ButtonType proceedButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                        ButtonType cancelButton = new ButtonType(bundle.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, bundle.getString("delete.question"), proceedButton, cancelButton);
                        alert.setTitle(bundle.getString("delete"));
                        alert.setHeaderText(null);
                        Optional<ButtonType> result = alert.showAndWait();
                        // delete if "Ok" is clicked
                        if (result.isPresent()) {
                            if (result.get() == proceedButton) {
                                this.deleteLabel = (Label) event.getSource();
                                delete(this.deleteLabel.getId(), this.selectedTab.getId());
                                alert.close();
                            } else {
                                alert.close();
                            }
                        }
                    }, errorService::handleError));
        }

    }


    public Tab createTab(String groupId, User user) {
        Tab tab = new Tab(user.name());
        ScrollPane sp = new ScrollPane();
        sp.setContent(new VBox());
        tab.setContent(sp);
        tab.setId(groupId);
        tab.setClosable(true);
        tab.setOnCloseRequest(event -> {
            chatTabPane.getTabs().remove(tab);
            ((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren().clear();
        });
        tab.setOnSelectionChanged(event -> {
            if (event.getTarget().equals(tab)) {
                this.selectedTab = tab;
            }
        });
        return tab;
    }


    public void reopenTab(Tab userTab) {
        boolean notOpen = true;
        for (Tab t : chatTabPane.getTabs()) {
            if (t.equals(userTab)) {
                notOpen = false;
                break;
            }
        }
        if (notOpen) {
            chatTabPane.getTabs().add(userTab);
            showOldMessages("groups", userTab.getId(), LocalDateTime.now().toString(), 100);
        }
    }
}
