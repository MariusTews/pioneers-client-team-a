package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.model.Game;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameListItemControllerTest extends ApplicationTest {
    @Mock
    LobbyController parentController;

    @Spy
    Game game = new Game("", "", "1", "name", "42", 1);

    @Mock
    ObservableList<Parent> items;

    @InjectMocks
    GameListItemController gameListItemController;

    @Override
    public void start(Stage stage) {
        new App(gameListItemController).start(stage);
    }

    @Test
    void onJoinButtonPressed() {
        clickOn("Join");
        verify(parentController).toJoinGame(game);
    }

    @Test
    void onGameUpdated() {
        Label gameNameLabel = lookup(game.name()).query();
        Label numPlayersLabel = lookup(game.members() + "/4").query();
        // Check that the Labels have the right text
        assertEquals(gameNameLabel.getText(), game.name());
        assertEquals(numPlayersLabel.getText(), "1/4");

        Game newGame = new Game("", "", "2", "game2", "42", 2);
        Platform.runLater(() -> {
            gameListItemController.onGameUpdated(newGame);

            // Check that the Labels has been updated correctly
            assertEquals(gameNameLabel.getText(), newGame.name());
            assertEquals(numPlayersLabel.getText(), "2/4");
        });

    }
}