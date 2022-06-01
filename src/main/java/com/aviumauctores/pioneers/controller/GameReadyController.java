package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.error.ErrorResponse;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.*;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.*;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class GameReadyController extends PlayerListController {


    private final App app;
    private final GameService gameService;
    private final GameMemberService gameMemberService;
    private final ErrorService errorService;
    private final MessageService messageService;

    private final EventListener eventListener;
    private final ResourceBundle bundle;
    private final Provider<LobbyController> lobbyController;

    private Label deleteLabel;

    @FXML
    public Button startGameButton;

    @FXML public Button gameReadyButton;

    @FXML public Button leaveGameButton;

    @FXML public Label gameNameLabel;

    @FXML public Button sendMessageButton;

    @FXML public ScrollPane chatPane;

    @FXML public Tab allChatTab;

    @FXML public TitledPane playerListPane;

    @FXML
    public ListView<Parent> playerList;
    @FXML public TextField messageTextField;

    @FXML public ComboBox<Color> pickColourMenu;

    private int readyMembers;

    //list for storing message IDs of own messages to check whether a message can be deleted or not
    private final ArrayList<String> ownMessageIds = new ArrayList<>();

    private CompositeDisposable disposables;

    private final HashMap<String, String> errorCodes = new HashMap<>();

    private final HashMap<Color, String> colourIsTaken = new HashMap<>();

    private Color chosenColour;

    @Inject
    public GameReadyController(App app, UserService userService, GameService gameService, GameMemberService gameMemberService,
                               EventListener eventListener, ErrorService errorService,
                               ResourceBundle bundle, MessageService messageService, Provider<LobbyController> lobbyController){
        super(userService);
        this.app = app;
        this.gameService = gameService;
        this.gameMemberService = gameMemberService;
        this.errorService = errorService;
        this.eventListener = eventListener;
        this.bundle = bundle;
        this.messageService = messageService;
        this.lobbyController = lobbyController;
        gameMemberService.updateID();
    }

    public void init() {
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
                    VBox chatBox = (VBox) ((ScrollPane) this.allChatTab.getContent()).getContent();
                    //if message is sent by myself then ignore it as it is already displayed in the sendMessage method
                    if (event.event().endsWith(".created") && !(event.data().sender().equals(userService.getCurrentUserID()))) {
                        Label msgLabel = createMessageLabel(event.data());
                        chatBox.getChildren().add(msgLabel);
                    }
                    else if (event.event().endsWith(".deleted")) {
                        //search for the Label of the which will be deleted
                        for (Node l : chatBox.getChildren()) {
                            if (event.data()._id().equals(l.getId())) {
                                this.deleteLabel = (Label) l;
                            }
                        }
                        chatBox.getChildren().remove(this.deleteLabel);
                    }
                }));

        errorCodes.put("400", bundle.getString("validation.failed"));
        errorCodes.put("401", bundle.getString("invalid.token"));
        errorCodes.put("403", bundle.getString("change.membership.error"));
        errorCodes.put("404", bundle.getString("membership.not.found"));
        errorCodes.put("429", bundle.getString("limit.reached"));

        colourIsTaken.put(Color.BLUE, "");
        colourIsTaken.put(Color.RED, "");
        colourIsTaken.put(Color.GREEN, "");
        colourIsTaken.put(Color.YELLOW, "");
        colourIsTaken.put(Color.ORANGE, "");
        colourIsTaken.put(Color.VIOLET, "");
        colourIsTaken.put(Color.CYAN, "");
        colourIsTaken.put(Color.LIMEGREEN, "");
        colourIsTaken.put(Color.MAGENTA, "");
        colourIsTaken.put(Color.CHOCOLATE, "");
    }

    protected void onMemberEvent(EventDto<Member> eventDto) {
        String event = eventDto.event();
        Member member = eventDto.data();
        String memberID = member.userId();
        Color memberColor = member.color();
        // if the colour is valid
        if (colourIsTaken.get(memberColor) != null) {
            // check all colours
            for (Color colour : colourIsTaken.keySet()) {
                // if they are assigned to this user
                if (Objects.equals(colourIsTaken.get(colour), memberID)) {
                    // unassign the colour
                    colourIsTaken.replace(colour, "");
                }
            }
            // assign the new colour to this user
            colourIsTaken.replace(memberColor, memberID);
            // and update the combobox
            updateComboBox();
            pickColourMenu.setValue(memberColor);
        }
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

    public void destroy(boolean closed) {
        super.destroy(closed);
        playerListItemControllers.forEach((id, controller) -> controller.destroy(false));
        playerListItemControllers.clear();
    }

    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/gameReadyScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
            chatPane.setId("chatpane");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //press esc to leave
        leaveGameButton.setCancelButton(true);
        playerList.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ESCAPE) {
                leaveGame(new ActionEvent());
            }
        });

        playerList.setItems(playerItems);
        updatePlayerLabel();

        messageTextField.setOnKeyPressed(event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                sendMessage(null);
            }
        } );

        // add the colours to the combobox
        pickColourMenu.getItems().addAll(
                Color.BLUE,
                Color.RED,
                Color.GREEN,
                Color.YELLOW,
                Color.ORANGE,
                Color.VIOLET,
                Color.CYAN,
                Color.LIMEGREEN,
                Color.MAGENTA,
                Color.CHOCOLATE);
        updateComboBox();

        return parent;
    }

    void updateComboBox(){
        // create the Colour-icons
        pickColourMenu.setCellFactory(param -> new ListCell<>() {
            private final Circle circle;{
                circle = new Circle(10f);
            }

            @Override
            protected void updateItem(Color colour, boolean empty){
                super.updateItem(colour, empty);
                if(colour == null || empty){
                    setGraphic(null);
                } else {
                    circle.setFill(colour);
                    HBox hBox = new HBox();
                    hBox.setId("item_" + colour);
                    hBox.getChildren().add(circle);
                    if(!Objects.equals(colourIsTaken.get(colour), "")){
                        hBox.getChildren().add(new Label("X"));
                    }
                    setGraphic(hBox);
                }
            }
        });
        // and display the current colour-icon
        pickColourMenu.setButtonCell(pickColourMenu.getCellFactory().call(null));
    }

    public void startGame(ActionEvent actionEvent) {


    }

    public void gameReady(ActionEvent actionEvent) {
        gameMemberService.updateMember(userService.getCurrentUserID(),null) //maybe null is not correct
                .observeOn(FX_SCHEDULER)
                .subscribe(member -> {
                            String buttonText = member.ready() ? bundle.getString("ready") : bundle.getString("not.ready");
                            gameReadyButton.setText(buttonText);
                        }
                        ,throwable -> {
                            if (throwable instanceof HttpException ex) {
                                ErrorResponse response = errorService.readErrorMessage(ex);
                                String message = errorCodes.get(Integer.toString(response.statusCode()));
                                Platform.runLater(() -> app.showHttpErrorDialog(response.statusCode(), response.error(), message));
                            } else {
                                app.showErrorDialog(bundle.getString("connection.failed"), bundle.getString("limit.reached"));
                            }
                        });

    }

    public void leaveGame(ActionEvent actionEvent) {
        if (userService.getCurrentUserID().equals(gameService.getOwnerID())) {
            ButtonType proceedButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType(bundle.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert ownerAlert = new Alert(Alert.AlertType.CONFIRMATION, bundle.getString("delete.game.owner.alert"), proceedButton, cancelButton);
            ownerAlert.setTitle(bundle.getString("warning"));
            ownerAlert.setHeaderText(null);
            Optional<ButtonType> result = ownerAlert.showAndWait();
            if (result.get() == proceedButton) {
                gameService.deleteGame()
                        .observeOn(FX_SCHEDULER)
                        .subscribe();
            } else {
                ownerAlert.close();
                return;
            }
        }else{
            ButtonType proceedButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType(bundle.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert memberAlert = new Alert(Alert.AlertType.CONFIRMATION, bundle.getString("delete.game.alert"), proceedButton, cancelButton);
            memberAlert.setTitle(bundle.getString("warning"));
            memberAlert.setHeaderText(null);
            Optional<ButtonType> result = memberAlert.showAndWait();
            if(result.get() == proceedButton) {
                gameMemberService.deleteMember(userService.getCurrentUserID())
                        .observeOn(FX_SCHEDULER)
                        .subscribe();
            }else {
                return;
            }
        }
        gameService.setCurrentGameID(null);
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
                .subscribe(result -> {
                    ownMessageIds.add(result._id());
                    Label msgLabel = createMessageLabel(result);
                    VBox chatBox = (VBox) ((ScrollPane) this.allChatTab.getContent()).getContent();
                    chatBox.getChildren().add(msgLabel);
                });
    }

    public void changeColour(ActionEvent actionEvent){
        // if the chosen colour is valid
        if (colourIsTaken.get(pickColourMenu.getValue()) != null){
            // but already taken
            if (!Objects.equals(colourIsTaken.get(pickColourMenu.getValue()), "") || Objects.equals(colourIsTaken.get(pickColourMenu.getValue()), userService.getCurrentUserID())){
                // the new colour will not be selected
                pickColourMenu.getSelectionModel().clearSelection();
                pickColourMenu.setValue(chosenColour);
            // and free
            } else {
                // the hexcode is created
                String colour = "#" + pickColourMenu.getValue().toString().substring(2,8);
                // send to the server
                gameMemberService.updateMember(userService.getCurrentUserID(), colour)
                        .observeOn(FX_SCHEDULER)
                        .subscribe(member -> {
                            // and stored locally
                            chosenColour = pickColourMenu.getValue();
                        }
                        ,throwable -> {
                            if (throwable instanceof HttpException ex) {
                                ErrorResponse response = errorService.readErrorMessage(ex);
                                String message = errorCodes.get(Integer.toString(response.statusCode()));
                                Platform.runLater(() -> app.showHttpErrorDialog(response.statusCode(), response.error(), message));
                            } else {
                                app.showErrorDialog(bundle.getString("connection.failed"), bundle.getString("limit.reached"));
                            }
                        });
            }
        }
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
        Label label = (Label) event.getSource();
        if (ownMessageIds.contains(label.getId()) && event.getButton() == MouseButton.SECONDARY) {
            // Alert for the delete
            ButtonType proceedButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType(bundle.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, bundle.getString("delete.question"), proceedButton, cancelButton);
            alert.setTitle(bundle.getString("delete"));
            alert.setHeaderText(null);
            Optional<ButtonType> res = alert.showAndWait();
            // delete if "Ok" is clicked
            if (res.get() == proceedButton){
                this.deleteLabel = label;
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