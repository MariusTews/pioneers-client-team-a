package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.service.PreferenceService;
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

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameListItemControllerTest extends ApplicationTest {
    @Mock
    LobbyController parentController;

    @Spy
    Game game = new Game("", "", "1", "name", "42", false, 1);

    @Mock
    ObservableList<Parent> items;

    @Mock
    PreferenceService preferenceService;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @InjectMocks
    GameListItemController gameListItemController;

    @Override
    public void start(Stage stage) {
        new App(gameListItemController).start(stage);
    }

    @Override
    public void stop() {
        this.bundle = null;
        this.game = null;
        this.gameListItemController = null;
        this.items = null;
        this.parentController = null;
        this.preferenceService = null;
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

        Game newGame = new Game("", "", "2", "game2", "42", false, 2);
        Platform.runLater(() -> {
            gameListItemController.onGameUpdated(newGame);

            // Check that the Labels has been updated correctly
            assertEquals(gameNameLabel.getText(), newGame.name());
            assertEquals(numPlayersLabel.getText(), "2/4");
        });

    }
}