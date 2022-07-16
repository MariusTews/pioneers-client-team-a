package com.aviumauctores.pioneers;

import javafx.scene.Node;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ScreenAsserts {
    private final ApplicationTest test;

    public ScreenAsserts(ApplicationTest test) {
        this.test = test;
    }

    void assertLoginScreen() {
        // The existence of the remember-me checkbox proves that we are in login screen
        Optional<Node> rememberMeCheckBox = test.lookup("#rememberMeCheckBox").tryQuery();
        assertThat(rememberMeCheckBox).isPresent();
    }

    void assertLobbyScreen() {
        // Only the lobby screen has the game list so if it exists we must be in lobby screen
        Optional<Node> gameListView = test.lookup("#gameListView").tryQuery();
        assertThat(gameListView).isPresent();
    }

    void assertSettingsScreen() {
        Optional<Node> changeNameButton = test.lookup("#changeNameButton").tryQuery();
        assertThat(changeNameButton).isPresent();
    }

    void assertCreateGameScreen() {
        // Check that we are in create game screen
        Optional<Node> gameNameInput = test.lookup("#gameNameInput").tryQuery();
        assertThat(gameNameInput).isPresent();
    }

    void assertJoinGameScreen() {
        // Check that we are in join game screen
        Optional<Node> gameNameLabel = test.lookup("#gameNameLabel").tryQuery();
        assertThat(gameNameLabel).isPresent();
        Optional<Node> joinGameButton = test.lookup("Join").tryQuery();
        assertThat(joinGameButton).isPresent();
    }

    void assertGameReadyScreen() {
        // Check that we are in game ready screen
        Optional<Node> startGameButton = test.lookup("#startGameButton").tryQuery();
        assertThat(startGameButton).isPresent();
    }

    void assertChatScreen() {
        // The chat tab pane is an indicator that we are in chat screen
        Optional<Node> chatTabPane = test.lookup("#chatTabPane").tryQuery();
        assertThat(chatTabPane).isPresent();
    }

    void assertIngameScreen() {
        Optional<Node> mainPane = test.lookup("#ingamePane").tryQuery();
        assertThat(mainPane).isPresent();
    }

    public void assertAchievementsScreen() {
        Optional<Node> friendsButton = test.lookup("#friendsButton").tryQuery();
        assertThat(friendsButton).isPresent();
    }
}
