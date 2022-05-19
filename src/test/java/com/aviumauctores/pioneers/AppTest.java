package com.aviumauctores.pioneers;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.assertions.api.Assertions.assertThat;

class AppTest extends ApplicationTest {
    private Stage stage;

    @Override
    public void start(Stage stage){
        this.stage = stage;
        final App app = new App(null);
        MainComponent testComponent = DaggerTestComponent.builder().mainApp(app).build();
        app.start(stage);
        app.show(testComponent.loginController());
    }

    @Test
    void criticalPath() {
        assertThat(stage.getTitle()).isEqualTo("Pioneers");

        assertLoginScreen();

        write("User1\t");
        write("12345678\t\t\t");
        // With 3 tabs we are on the create account button
        type(KeyCode.SPACE);

        final TextField regUsername = lookup("#textfieldUsername").query();
        final PasswordField regPassword = lookup("#textfieldPassword").query();
        // Check the credentials has been passed from login to register screen
        assertThat(regUsername).hasText("User1");
        assertThat(regPassword).hasText("12345678");
        // Create an account
        type(KeyCode.ENTER);

        assertLoginScreen();

        write("User1\t");
        write("12345678\t");
        // Login
        type(KeyCode.SPACE);

        assertLobbyScreen();
        // Go to create game screen
        clickOn("#createGameButton");

        assertCreateGameScreen();
        // Go back to lobby
        clickOn("#cancelButton");

        assertLobbyScreen();
        // Go to create game screen again
        clickOn("#createGameButton");

        write("Game101");
        clickOn("#gamePasswordInput");
        write("1");
        clickOn("#createGameButton");

        assertGameReadyScreen();
        // Go back to lobby
        clickOn("#leaveGameButton");

        assertLobbyScreen();
        // Go to chat screen
        clickOn("#chatButton");

        assertChatScreen();
        // Go back to lobby
        clickOn("#leaveButton");

        assertLobbyScreen();
        // Join a game
        clickOn("Join");

        // Back to lobby
        type(KeyCode.ESCAPE);

        assertLobbyScreen();
        // Join again
        clickOn("Join");

        // Longer password so CI has more time for screen change
        write("12345678");
        // Assert only now the join game screen because CI seems to dislike the first place
        assertJoinGameScreen();
        // Join game
        type(KeyCode.ENTER);

        assertGameReadyScreen();
        // Go back to lobby
        clickOn("#leaveGameButton");

        // Logout
        clickOn("#quitButton");

        // Now we should be back in login screen
        assertLoginScreen();
    }

    private void assertLoginScreen() {
        // The existence of the remember-me checkbox proves that we are in login screen
        lookup("#rememberMeCheckBox").query();
    }

    private void assertLobbyScreen() {
        // Only the lobby screen has the game list so if it exists we must be in lobby screen
        lookup("#gameListView").queryListView();
    }

    private void assertCreateGameScreen() {
        // Check that we are in create game screen
        lookup("#gameNameInput").query();
    }

    private void assertJoinGameScreen() {
        // Check that we are in join game screen
        lookup("#joinGameButton").queryButton();
    }

    private void assertGameReadyScreen() {
        // Check that we are in game ready screen
        lookup("#startGameButton").queryButton();
    }

    private void assertChatScreen() {
        // The chat tab pane is an indicator that we are in chat screen
        lookup("#chatTabPane").query();
    }
}