package com.aviumauctores.pioneers;

import org.testfx.framework.junit5.ApplicationTest;

public class ScreenAsserts {
    private final ApplicationTest test;

    public ScreenAsserts(ApplicationTest test) {
        this.test = test;
    }

    void assertLoginScreen() {
        // The existence of the remember-me checkbox proves that we are in login screen
        test.lookup("#rememberMeCheckBox").query();
    }

    void assertLobbyScreen() {
        // Only the lobby screen has the game list so if it exists we must be in lobby screen
        test.lookup("#gameListView").queryListView();
    }

    void assertCreateGameScreen() {
        // Check that we are in create game screen
        test.lookup("#gameNameInput").query();
    }

    void assertJoinGameScreen() {
        // Check that we are in join game screen
        test.lookup("#joinGameButton").queryButton();
    }

    void assertGameReadyScreen() {
        // Check that we are in game ready screen
        test.lookup("#startGameButton").queryButton();
    }

    void assertChatScreen() {
        // The chat tab pane is an indicator that we are in chat screen
        test.lookup("#chatTabPane").query();
    }
}
