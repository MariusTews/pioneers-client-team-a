package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.model.Game;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.ResourceBundle;

public class GameListItemController implements Controller {
    private BorderPane root;
    private Label gameName;
    private Label numMembersLabel;
    private Button joinButton;

    private final LobbyController parentController;
    private Game game;
    private final ObservableList<Parent> gameItems;
    private final ResourceBundle bundle;

    public GameListItemController(LobbyController parentController, Game game, ObservableList<Parent> gameItems,
                                  ResourceBundle bundle) {
        this.parentController = parentController;
        this.game = game;
        this.gameItems = gameItems;
        this.bundle = bundle;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy(boolean closed) {
        gameItems.remove(root);
    }

    @Override
    public Parent render() {
        gameName = new Label(game.name());
        gameName.getStyleClass().add("game-list");
        // Set width so numMembersTextLabel is correct aligned in the ListView
        gameName.setPrefWidth(90.0);
        Label numMembersTextLabel = new Label(bundle.getString("amount.players") + ":");
        numMembersTextLabel.getStyleClass().add("game-list");
        int numMembers = game.members();
        numMembersLabel = new Label(numMembers + "");
        joinButton = new Button(bundle.getString("join"));
        joinButton.getStyleClass().add("game-list");
        joinButton.setOnAction(this::onJoinButtonPressed);
        // Don't let more than four players join
        joinButton.setDisable(numMembers >= 4);
        HBox rightHBox = new HBox(8.0, numMembersLabel, joinButton);
        rightHBox.setAlignment(Pos.CENTER);
        root = new BorderPane(numMembersTextLabel, null, rightHBox, null, gameName);
        BorderPane.setAlignment(gameName, Pos.CENTER);
        BorderPane.setAlignment(numMembersTextLabel, Pos.CENTER);
        BorderPane.setAlignment(rightHBox, Pos.CENTER);
        root.setPadding(new Insets(0.0, 10.0, 0.0, 10.0));
        return root;
    }

    private void onJoinButtonPressed(ActionEvent event) {
        parentController.toJoinGame(game);
    }

    public void onGameUpdated(Game newGame) {
        game = newGame;
        gameName.setText(game.name());
        int numMembers = game.members();
        numMembersLabel.setText(numMembers + "");
    }
}
