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
    private final ScreenAsserts screenAsserts = new ScreenAsserts(this);

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

        screenAsserts.assertLoginScreen();

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

        screenAsserts.assertLoginScreen();

        write("User1\t");
        write("12345678\t");
        // Login
        type(KeyCode.SPACE);

        screenAsserts.assertLobbyScreen();
        // Go to create game screen
        clickOn("#createGameButton");

        screenAsserts.assertCreateGameScreen();
        // Go back to lobby
        clickOn("#cancelButton");

        screenAsserts.assertLobbyScreen();
        // Go to create game screen again
        clickOn("#createGameButton");

        write("Game101");
        clickOn("#gamePasswordInput");
        write("1");
        clickOn("#createGameButton");

        screenAsserts.assertGameReadyScreen();
        // Go back to lobby
        clickOn("#leaveGameButton");

        screenAsserts.assertLobbyScreen();
        // Go to chat screen
        clickOn("#chatButton");

        screenAsserts.assertChatScreen();
        // Go back to lobby
        clickOn("#leaveButton");

        screenAsserts.assertLobbyScreen();
        // Join a game
        clickOn("Join");
        // Time for CI
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        screenAsserts.assertJoinGameScreen();
        // Back to lobby
        type(KeyCode.ESCAPE);

        screenAsserts.assertLobbyScreen();
        // Join again
        clickOn("Join");
        // Time for CI
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Longer password so CI has more time for screen change
        write("12345678");
        screenAsserts.assertJoinGameScreen();
        // Join game
        type(KeyCode.ENTER);

        screenAsserts.assertGameReadyScreen();
        // Go back to lobby
        clickOn("#leaveGameButton");

        // Logout
        clickOn("#quitButton");

        // Now we should be back in login screen
        screenAsserts.assertLoginScreen();
    }
}