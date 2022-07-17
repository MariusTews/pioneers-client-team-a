package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.error.ErrorResponse;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.*;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    private final Provider<InGameController> inGameController;
    public Label spectatorText;

    private Label deleteLabel;

    @FXML
    public Button startGameButton;

    @FXML
    public Button gameReadyButton;

    @FXML
    public Button leaveGameButton;

    @FXML
    public Label gameNameLabel;

    @FXML
    public Button sendMessageButton;

    @FXML
    public ScrollPane chatPane;

    @FXML
    public Tab allChatTab;

    @FXML
    public TitledPane playerListPane;

    @FXML
    public ListView<Parent> playerList;
    @FXML
    public TextField messageTextField;

    @FXML
    public ComboBox<Color> pickColourMenu;

    @FXML
    public Button gameOptionButton;

    @FXML
    public ToggleButton onButton;

    @FXML
    public ToggleButton offButton;

    private int readyMembers;
    private int allMembers;

    private GameOptionController gameOptionController;

    private Parent gameOption;

    @FXML
    public Pane paneOption;

    //list for storing message IDs of own messages to check whether a message can be deleted or not
    private final ArrayList<String> ownMessageIds = new ArrayList<>();

    private final HashMap<String, String> errorCodes = new HashMap<>();

    private final HashMap<Color, String> colourIsTaken = new HashMap<>();

    private Color chosenColour;

    private boolean comingFromIngame = false;
    private boolean rejoinFromLobby = false;

    private boolean onlyspectator;
    private boolean spectator;


    @Inject
    public GameReadyController(App app,
                               LoginService loginService, UserService userService,
                               GameService gameService, GameMemberService gameMemberService,
                               EventListener eventListener, ErrorService errorService,
                               ResourceBundle bundle, MessageService messageService,
                               Provider<LobbyController> lobbyController, Provider<InGameController> inGameController) {
        super(loginService, userService);
        this.app = app;
        this.gameService = gameService;
        this.gameMemberService = gameMemberService;
        this.errorService = errorService;
        this.eventListener = eventListener;
        this.bundle = bundle;
        this.messageService = messageService;
        this.lobbyController = lobbyController;
        this.inGameController = inGameController;
        gameMemberService.updateID();

    }

    public void init() {
        disposables = new CompositeDisposable();
        errorService.setErrorCodesGameMembersPost();
        // Get game via REST
        disposables.add(gameService.getCurrentGame()
                .subscribe(game -> allMembers = game.members()));
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
                }, errorService::handleError));
        // Listen to game updates
        disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".*", Game.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(eventDto -> {
                    String event = eventDto.event();
                    Game game = eventDto.data();
                    gameNameLabel.setText(game.name());

                    if (event.endsWith("created") || event.endsWith("updated")) {
                        allMembers = game.members();
                        if (game.started()) {
                            InGameController controller = inGameController.get();
                            controller.setSpectator(spectator);
                            app.show(controller);
                        }
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
                        ScrollPane content = (ScrollPane) this.allChatTab.getContent();
                        content.setVvalue(1.0);
                    } else if (event.event().endsWith(".deleted")) {
                        //search for the Label of the which will be deleted
                        for (Node l : chatBox.getChildren()) {
                            if (event.data()._id().equals(l.getId())) {
                                this.deleteLabel = (Label) l;
                            }
                        }
                        chatBox.getChildren()
                                .remove(this.deleteLabel);

                    }
                }));
        disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".deleted", Game.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    if (!userService.getCurrentUserID().equals(gameService.getOwnerID())) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(bundle.getString("warning"));
                        alert.setHeaderText(bundle.getString("game.deleted"));
                        alert.showAndWait();
                        gameService.setCurrentGameID(null);
                        app.show(lobbyController.get());
                    }
                }));

        errorCodes.put("400_member", bundle.getString("validation.failed"));
        errorCodes.put("400_game", bundle.getString("validation.failed"));
        errorCodes.put("401_member", bundle.getString("invalid.token"));
        errorCodes.put("401_game", bundle.getString("invalid.token"));
        errorCodes.put("403_member", bundle.getString("change.membership.error"));
        errorCodes.put("403_game", bundle.getString("change.game.error"));
        errorCodes.put("404_member", bundle.getString("membership.not.found"));
        errorCodes.put("404_game", bundle.getString("membership.not.found"));
        errorCodes.put("429_member", bundle.getString("limit.reached"));
        errorCodes.put("429_game", bundle.getString("limit.reached"));

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

        disposables.add(gameMemberService.listCurrentGameMembers()
                .observeOn(FX_SCHEDULER)
                .subscribe(members -> {
                    for (Member member : members) {
                        if (member.color() != null) {
                            if (colourIsTaken.get(member.color()) != null) {
                                colourIsTaken.replace(member.color(), member.userId());
                            }
                        }
                    }
                }));
    }

    protected void onMemberEvent(EventDto<Member> eventDto) {
        String event = eventDto.event();
        Member member = eventDto.data();
        String memberID = member.userId();
        Color memberColor = member.color();

        if (memberID.equals(userService.getCurrentUserID())) {
            spectator = member.spectator();
        }
        // if the colour is valid
        if (colourIsTaken.get(memberColor) != null) {
            // check all colours
            for (Color colour : colourIsTaken.keySet()) {
                // if they are assigned to this user
                if (Objects.equals(colourIsTaken.get(colour), memberID)) {
                    // reset the colour
                    colourIsTaken.replace(colour, "");
                }
            }
            // assign the new colour to this user
            colourIsTaken.replace(memberColor, memberID);
            // and update the combobox
            updateComboBox();
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
                removeMemberFromList(member, controller);
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
    protected void removeMemberFromList(Member member, PlayerListItemController controller) {
        super.removeMemberFromList(member, controller);
        if (member.ready()) {
            readyMembers--;
        }
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

        offButton.setStyle("-fx-base: red ");


        disposables.add(gameService.getCurrentGame()
                .observeOn(FX_SCHEDULER)
                .subscribe(game -> {
                    gameNameLabel.setText(game.name());
                    gameService.setOwnerID(game.owner());
                    if (!(gameService.getOwnerID().equals(userService.getCurrentUserID()))) {
                        gameOptionButton.setDisable(true);
                    }
                }));

        //press esc to leave
        leaveGameButton.setCancelButton(true);
        playerList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                leaveGame(new ActionEvent());
            }
        });

        playerList.setItems(playerItems);
        updatePlayerLabel();

        messageTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage(null);
            }
        });

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

        gameService.setUpdateOption(2, 10);

        if (comingFromIngame) {
            updateVisualsOnRejoin();
        }

        if (rejoinFromLobby) {
            disposables.add(gameService.getCurrentGame()
                    .observeOn(FX_SCHEDULER)
                    .subscribe(game -> {
                        if (game.started()) {
                            updateVisualsOnRejoin();
                        }
                    })
            );
        }

        return parent;
    }

    private void rejoinIngame(ActionEvent actionEvent) {
        InGameController controller = inGameController.get();
        controller.setRejoin(true);
        app.show(controller);
    }

    void updateComboBox() {
        // create the colour-icons
        pickColourMenu.setCellFactory(param -> new ListCell<>() {
            private final Circle circle;

            {
                circle = new Circle(10f);
            }

            @Override
            protected void updateItem(Color colour, boolean empty) {
                super.updateItem(colour, empty);
                if (colour == null || empty) {
                    setGraphic(null);
                } else {
                    circle.setFill(colour);
                    HBox hBox = new HBox();
                    hBox.setId("item_" + colour);
                    hBox.getChildren().add(circle);
                    if (!Objects.equals(colourIsTaken.get(colour), "")) {
                        Label labelX = new Label("X");
                        labelX.setStyle("-fx-text-fill: #000000");
                        hBox.getChildren().add(labelX);
                    }
                    setGraphic(hBox);
                }
            }
        });
        // and display the current colour-icon
        pickColourMenu.setButtonCell(pickColourMenu.getCellFactory().call(null));
    }

    public void startGame(ActionEvent actionEvent) {
        onlyspectator = getOnlySpectator();
        if (readyMembers != allMembers) {
            app.showErrorDialog(bundle.getString("cannot.start.game"), bundle.getString("not.all.members.ready"));
            return;
        }
        int colouredMembers = (int) colourIsTaken.values().stream().filter(s -> !s.isEmpty()).count();
        if (colouredMembers != allMembers) {
            app.showErrorDialog(bundle.getString("cannot.start.game"), bundle.getString("not.all.members.coloured"));
            return;
        }

        if (onlyspectator) {
            app.showErrorDialog(bundle.getString("cannot.start.game"), bundle.getString("not.enough.players"));
            return;
        }

        disposables.add(gameService.startGame()
                .observeOn(FX_SCHEDULER)
                .subscribe(game -> {
                    // do nothing, the switch to in-game-screen happens with the websocket event
                }, throwable -> {
                    if (throwable instanceof HttpException ex) {
                        ErrorResponse response = errorService.readErrorMessage(ex);
                        String message = errorCodes.get(response.statusCode() + "_game");
                        Platform.runLater(() -> app.showHttpErrorDialog(response.statusCode(), response.error(), message));
                    } else {
                        app.showErrorDialog(bundle.getString("smth.went.wrong"), bundle.getString("try.again"));
                    }
                }));
    }

    public void gameReady(ActionEvent actionEvent) {
        disposables.add(gameMemberService.updateMember(userService.getCurrentUserID()) //maybe null is not correct
                .observeOn(FX_SCHEDULER)
                .subscribe(member -> {
                            String buttonText = member.ready() ? bundle.getString("ready") : bundle.getString("not.ready");
                            gameReadyButton.setText(buttonText);
                        }
                        , throwable -> {
                            if (throwable instanceof HttpException ex) {
                                ErrorResponse response = errorService.readErrorMessage(ex);
                                String message = errorCodes.get(response.statusCode() + "_member");
                                Platform.runLater(() -> app.showHttpErrorDialog(response.statusCode(), response.error(), message));
                            } else {
                                app.showErrorDialog(bundle.getString("smth.went.wrong"), bundle.getString("limit.reached"));
                            }
                        }));

    }

    public void leaveGame(ActionEvent actionEvent) {
        if (userService.getCurrentUserID().equals(gameService.getOwnerID())) {
            ButtonType proceedButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType(bundle.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert ownerAlert = new Alert(Alert.AlertType.CONFIRMATION, bundle.getString("delete.game.owner.alert"), proceedButton, cancelButton);
            ownerAlert.setTitle(bundle.getString("warning"));
            ownerAlert.setHeaderText(null);
            Optional<ButtonType> result = ownerAlert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == proceedButton) {
                    gameService.deleteGame()
                            .observeOn(FX_SCHEDULER)
                            .subscribe();
                } else {
                    ownerAlert.close();
                    return;
                }
            }
        } else {
            ButtonType proceedButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType(bundle.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert memberAlert = new Alert(Alert.AlertType.CONFIRMATION, bundle.getString("delete.game.alert"), proceedButton, cancelButton);
            memberAlert.setTitle(bundle.getString("warning"));
            memberAlert.setHeaderText(null);
            Optional<ButtonType> result = memberAlert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == proceedButton) {
                    gameMemberService.deleteMember(userService.getCurrentUserID())
                            .observeOn(FX_SCHEDULER)
                            .subscribe();
                } else {
                    memberAlert.close();
                    return;
                }
            }
        }
        gameService.setCurrentGameID(null);
        gameMemberService.setGameID(null);
        final LobbyController controller = lobbyController.get();
        app.show(controller);
    }


    public void sendMessage(ActionEvent actionEvent) {
        String message = messageTextField.getText();
        if (message.isBlank()) {
            return;
        }
        messageTextField.clear();
        disposables.add(messageService.sendGameMessage(message, gameService.getCurrentGameID())
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    ownMessageIds.add(result._id());
                    Label msgLabel = createMessageLabel(result);
                    VBox chatBox = (VBox) ((ScrollPane) this.allChatTab.getContent()).getContent();
                    chatBox.getChildren().add(msgLabel);
                    ((ScrollPane) this.allChatTab.getContent()).setVvalue(1.0);
                }));
    }

    public void changeColour(ActionEvent actionEvent) {
        // if the chosen colour is valid...
        if (colourIsTaken.get(pickColourMenu.getValue()) != null) {
            // ...but already taken
            if (!Objects.equals(colourIsTaken.get(pickColourMenu.getValue()), "") || Objects.equals(colourIsTaken.get(pickColourMenu.getValue()), userService.getCurrentUserID())) {
                // the new colour will not be selected
                pickColourMenu.getSelectionModel().clearSelection();
                pickColourMenu.setValue(chosenColour);
                // ...and free
            } else {
                // the hex-code is created
                String colour = "#" + pickColourMenu.getValue().toString().substring(2, 8);
                // send to the server
                disposables.add(gameMemberService.updateColour(userService.getCurrentUserID(), colour)
                        .observeOn(FX_SCHEDULER)
                        .subscribe(member -> {
                                    // and stored locally
                                    chosenColour = pickColourMenu.getValue();
                                }
                                , throwable -> {
                                    if (throwable instanceof HttpException ex) {
                                        ErrorResponse response = errorService.readErrorMessage(ex);
                                        String message = errorCodes.get(response.statusCode() + "_member");
                                        Platform.runLater(() -> app.showHttpErrorDialog(response.statusCode(), response.error(), message));
                                    } else {
                                        app.showErrorDialog(bundle.getString("smth.went.wrong"), bundle.getString("limit.reached"));
                                    }
                                }));
            }
        }
    }

    public Label createMessageLabel(Message message) {
        Label msgLabel = new Label();
        disposables.add(userService.getUserName(message.sender())
                .observeOn(FX_SCHEDULER)
                .subscribe(
                        result -> msgLabel.setText(result + ": " + message.body())
                ));
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
            if (res.isPresent()) {
                if (res.get() == proceedButton) {
                    this.deleteLabel = label;
                    delete(this.deleteLabel.getId());
                }
            }
            alert.close();
        }
    }

    public void delete(String messageId) {
        messageService.deleteGameMessage(messageId, gameService.getCurrentGameID())
                .observeOn(FX_SCHEDULER)
                .subscribe();
    }

    public void gameOption(ActionEvent actionEvent) {
        gameOptionController = new GameOptionController(bundle, this);
        gameOption = gameOptionController.render();
        paneOption.setVisible(true);
        paneOption.getChildren().add(gameOption);
    }

    public void changeOnOff(ActionEvent actionEvent) {
        if (actionEvent.getSource() == onButton) {
            if (offButton.isSelected()) {
                offButton.setSelected(false);
                offButton.setStyle("-fx-base: lightgray ");
                onButton.setStyle("-fx-base: lightgreen");
                disposables.add(gameMemberService.setSpectator(userService.getCurrentUserID(), true)
                        .subscribe(member -> gameReadyButton.setDisable(true)));

            } else {
                onButton.setSelected(true);
            }
        }
        if (actionEvent.getSource() == offButton) {
            if (onButton.isSelected()) {
                onButton.setSelected(false);
                onButton.setStyle("-fx-base: lightgray");
                offButton.setStyle("-fx-base: red");
                disposables.add(gameMemberService.setSpectator(userService.getCurrentUserID(), false)
                        .subscribe(member -> gameReadyButton.setDisable(false)));

            } else {
                offButton.setSelected(true);

            }
        }

    }

    public void closeGameOptionMenu(boolean menuClosed) {
        if (gameOptionController != null) {
            gameOptionController.destroy(menuClosed);
            gameOptionController = null;
        }
        if (gameOption != null) {
            paneOption.getChildren().remove(gameOption);
            gameOption = null;
        }
    }

    public void setMapsizeAndVictorypoints(int victoryPoints, int mapSize) {
        if (mapSize >= 0 && mapSize <= 10 && victoryPoints >= 3 && victoryPoints <= 15) {
            gameService.setUpdateOption(mapSize, victoryPoints).subscribe();
        } else {
            ButtonType proceedButton1 = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, bundle.getString("failedEntering"), proceedButton1);
            alert.setTitle(bundle.getString("Entering failed"));
            alert.setHeaderText(null);
        }
    }

    public void setComingFromIngame(boolean comingFromIngame) {
        this.comingFromIngame = comingFromIngame;
    }

    public void setRejoinFromLobby(boolean rejoinFromLobby) {
        this.rejoinFromLobby = rejoinFromLobby;
    }

    private void updateVisualsOnRejoin() {
        startGameButton.setText(bundle.getString("join"));
        startGameButton.setOnAction(this::rejoinIngame);
        gameReadyButton.setDisable(true);
        pickColourMenu.setDisable(true);
        gameOptionButton.setDisable(true);
        offButton.setDisable(true);
        onButton.setDisable(true);
    }

    public boolean getOnlySpectator() {
        onlyspectator = true;
        for (Member m : gameMemberService.listCurrentGameMembers().blockingFirst()) {
            if (!m.spectator()) {
                onlyspectator = false;
                break;
            }
        }
        return onlyspectator;
    }
}