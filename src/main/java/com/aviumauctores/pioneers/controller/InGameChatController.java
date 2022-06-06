package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.service.*;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class InGameChatController implements Controller {

    private final App app;
    private final UserService userService;
    private final GameService gameService;
    private final GameMemberService gameMemberService;
    private final EventListener eventListener;
    private final ErrorService errorService;
    private final ResourceBundle bundle;
    private final MessageService messageService;

    @FXML
    public TabPane tabPane;


    @FXML
    public TextField messageTextField;
    @FXML
    public Tab allChatTab;
    @FXML
    public ScrollPane chatPane;
    @FXML
    public Button sendMessageButton;

    private final ArrayList<String> ownMessageIds = new ArrayList<>();

    private Label deleteLabel;

    private CompositeDisposable disposables;

    @Inject
    public InGameChatController(App app, UserService userService, GameService gameService, GameMemberService gameMemberService,
                                EventListener eventListener, ErrorService errorService,
                                ResourceBundle bundle, MessageService messageService
    ) {

        this.app = app;
        this.userService = userService;
        this.gameService = gameService;
        this.gameMemberService = gameMemberService;
        this.eventListener = eventListener;
        this.errorService = errorService;
        this.bundle = bundle;
        this.messageService = messageService;
    }

    ;

    public void init() {
        disposables = new CompositeDisposable();

        disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".messages.*.*", Message.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    VBox chatBox = (VBox) ((ScrollPane) this.allChatTab.getContent()).getContent();
                    //if message is sent by myself then ignore it as it is already displayed in the sendMessage method
                    if (event.event().endsWith(".created") && !(event.data().sender().equals(userService.getCurrentUserID()))) {
                        HBox msgLabel = createMessageLabel(event.data());
                        chatBox.getChildren().add(msgLabel);
                    } else if (event.event().endsWith(".deleted")) {
                        //search for the Label of the which will be deleted
                        for (Node l : chatBox.getChildren()) {
                            if (event.data()._id().equals(l.getId())) {
                                this.deleteLabel = (Label) l;
                            }
                        }
                        chatBox.getChildren().remove(this.deleteLabel);
                    }
                }));


    }

    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/InGameChat.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


        return parent;
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
                    HBox msgLabel = createMessageLabel(result);
                    VBox chatBox = (VBox) ((ScrollPane) this.allChatTab.getContent()).getContent();
                    chatBox.getChildren().add(msgLabel);
                });
    }

    public HBox createMessageLabel(Message message) {
        Label msgLabel = new Label();
        ImageView avatarView = new ImageView();
        avatarView.setFitWidth(20.0);
        avatarView.setFitHeight(20.0);


        userService.getUserByID(message.sender())
                .observeOn(FX_SCHEDULER)
                .subscribe(
                        result -> {
                            msgLabel.setText(result.name() + ": " + message.body());
                            String avatarUrl = result.avatar();
                            Image avatar = avatarUrl == null ? null : new Image(avatarUrl);
                            avatarView.setImage(avatar);
                        }
                );
        msgLabel.setOnMouseClicked(this::onMessageClicked);
        msgLabel.setId(message._id());
        HBox playerBox = new HBox(5, avatarView, msgLabel);

        return playerBox;
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
            if (res.get() == proceedButton) {
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

    @Override
    public void destroy(boolean closed) {
        disposables.dispose();
    }
}
