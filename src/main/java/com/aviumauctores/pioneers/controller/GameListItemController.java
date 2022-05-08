package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.model.Game;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class GameListItemController implements Controller {
    private BorderPane root;
    private Label gameName;
    private Label numMembersLabel;
    private Button joinButton;

    private Game game;
    private final ObservableList<Parent> gameItems;

    public GameListItemController(LobbyController parentController, Game game, ObservableList<Parent> gameItems) {
        this.game = game;
        this.gameItems = gameItems;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {
        gameItems.remove(root);
    }

    @Override
    public Parent render() {
        gameName = new Label(game.name());
        // Set width so numMembersTextLabel is correct aligned in the ListView
        gameName.setPrefWidth(100.0);
        Label numMembersTextLabel = new Label("Anzahl Spieler:");
        int numMembers = game.members();
        numMembersLabel = new Label(numMembers + "/4");
        joinButton = new Button("Join");
        // Don't let more than four players join
        joinButton.setDisable(numMembers >= 4);
        HBox rightHBox = new HBox(10.0, numMembersLabel, joinButton);
        rightHBox.setAlignment(Pos.CENTER);
        root = new BorderPane(numMembersTextLabel, null, rightHBox, null, gameName);
        BorderPane.setAlignment(gameName, Pos.CENTER);
        BorderPane.setAlignment(numMembersTextLabel, Pos.CENTER);
        BorderPane.setAlignment(rightHBox, Pos.CENTER);
        root.setPadding(new Insets(0.0, 10.0, 0.0, 10.0));
        return root;
    }

    public void onGameUpdated(Game newGame) {
        game = newGame;
        gameName.setText(game.name());
        int numMembers = game.members();
        numMembersLabel.setText(numMembers + "/4");
        // Don't let more than four players join
        joinButton.setDisable(numMembers >= 4);
    }
}
