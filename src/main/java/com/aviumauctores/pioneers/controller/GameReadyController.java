package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.GameMemberService;
import com.aviumauctores.pioneers.service.GameService;
import com.aviumauctores.pioneers.service.MessageService;
import com.aviumauctores.pioneers.service.UserService;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class GameReadyController extends PlayerListController {


    private final App app;
    private final UserService userService;
    private final GameService gameService;
    private final GameMemberService gameMemberService;
    private final EventListener eventListener;
    private final ResourceBundle bundle;
    private final MessageService messageService;
    private final Provider<LobbyController> lobbyController;

    private User user;

    private Label deleteLabel;

    @FXML public Button startGameButton;

    @FXML public Button gameReadyButton;

    @FXML public Button leaveGameButton;

    @FXML public Label gameNameLabel;

    @FXML public Button sendMessageButton;

    @FXML public ScrollPane chatPane;

    @FXML public Tab allChatTab;

    @FXML public TitledPane playerListPane;

    @FXML public ListView<Parent> playerList;
    @FXML public TextField messageTextField;

    private int readyMembers;

    private CompositeDisposable disposables;

    @Inject
    public GameReadyController(App app, UserService userService, GameService gameService, GameMemberService gameMemberService,
                               EventListener eventListener,
                               ResourceBundle bundle, MessageService messageService, Provider<LobbyController> lobbyController){
        this.app = app;
        this.userService = userService;
        this.gameService = gameService;
        this.gameMemberService = gameMemberService;
        this.eventListener = eventListener;
        this.bundle = bundle;
        this.messageService = messageService;
        this.lobbyController = lobbyController;
    }

    public void init(){
        disposables = new CompositeDisposable();
        // Get member list via REST
        disposables.add(gameMemberService.listCurrentGameMembers()
                        .observeOn(FX_SCHEDULER)
                        .subscribe(members -> {
                            for (Member member : members) {
                                addMemberToList(member);
                            }
                            if (playerListPane != null) {
                                updatePlayerLabel();
                            }
                        }));
        // Listen to game member events
        disposables.add(eventListener.listen(
                        "games." + gameService.getCurrentGameID() + ".members.*.*",
                        Member.class
                )
                .observeOn(FX_SCHEDULER)
                .subscribe(this::onMemberEvent));

        //listen to chat messages
        disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".messages.*.*", Message.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    if (event.event().endsWith(".created")) {
                        Label msgLabel = createMessageLabel(event.data());
                        ((VBox)((ScrollPane)this.allChatTab.getContent()).getContent()).getChildren()
                                .add(msgLabel);
                    }
                    else if (event.event().endsWith(".deleted")) {
                        //search for the Label of the which will be deleted
                        for (Node l : ((VBox)((ScrollPane)this.allChatTab.getContent()).getContent()).getChildren()) {
                            if (event.data()._id().equals(l.getId())) {
                                this.deleteLabel = (Label) l;
                            }
                        }
                        ((VBox)((ScrollPane)this.allChatTab.getContent()).getContent()).getChildren()
                                .remove(this.deleteLabel);

                    }
                }));
    }

    protected void onMemberEvent(EventDto<Member> eventDto) {
        String event = eventDto.event();
        Member member = eventDto.data();
        if (event.endsWith("created")) {
            addMemberToList(member);
        } else if (event.endsWith("updated")) {
            PlayerListItemController controller = playerListItemControllers.get(member.userId());
            if (controller != null) {
                readyMembers += controller.onGameMemberUpdated(member);
            } else {
                addMemberToList(member);
            }
        } else if (event.endsWith("deleted")) {
            PlayerListItemController controller = playerListItemControllers.get(member.userId());
            if (controller != null) {
                removePlayerFromList(member.userId(), controller);
            }
        }
        updatePlayerLabel();
    }

    @Override
    protected void addMemberToList(Member gameMember) {
        String userId = gameMember.userId();
        disposables.add(userService.getUserByID(userId)
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    addPlayerToList(user, gameMember);
                    // Listen to user events
                    disposables.add(eventListener.listen("users." + userId + ".*", User.class)
                            .observeOn(FX_SCHEDULER)
                            .subscribe(this::onUserEvent));
                }));
        if (gameMember.ready()) {
            readyMembers++;
        }
    }

    @Override
    protected void updatePlayerLabel() {
        playerListPane.setText(String.format(bundle.getString("player.in.game"), readyMembers));
    }

    @Override
    protected void removePlayerFromList(String userID, PlayerListItemController controller) {
        super.removePlayerFromList(userID, controller);
        readyMembers--;
    }

    public void destroy(){
        if (disposables != null) {
            disposables.dispose();
            disposables = null;
        }
        playerListItemControllers.forEach((id, controller) -> controller.destroy());
        playerListItemControllers.clear();
    }

    public Parent render(){
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/gameReadyScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        }catch(IOException e) {
            e.printStackTrace();
            return null;
        }

        playerList.setItems(playerItems);
        updatePlayerLabel();

        messageTextField.setOnKeyPressed(event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                sendMessage(null);
            }
        } );

        return parent;
    }

    public void startGame(ActionEvent actionEvent) {


    }

    public void gameReady(ActionEvent actionEvent) {
    }

    public void leaveGame(ActionEvent actionEvent) {
        final LobbyController controller = lobbyController.get();
        app.show(controller);
    }

    public void sendMessage(ActionEvent actionEvent) {
        String message = messageTextField.getText();
        if (message.isBlank()) {
            return;
        }
        messageTextField.clear();
        messageService.sendGameMessage(message, gameService.getCurrentGameID())
                .observeOn(FX_SCHEDULER)
                .subscribe();
    }

    public Label createMessageLabel(Message message) {
        Label msgLabel = new Label();
        userService.getUserName(message.sender())
                .observeOn(FX_SCHEDULER)
                .subscribe(
                        result -> msgLabel.setText(result + ": " + message.body())
                );
        msgLabel.setOnMouseClicked(this::onMessageClicked);
        msgLabel.setId(message._id());
        return msgLabel;
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

    public void delete(String messageId) {
        messageService.deleteGameMessage(messageId, gameService.getCurrentGameID())
                .observeOn(FX_SCHEDULER)
                .subscribe();
    }
}
