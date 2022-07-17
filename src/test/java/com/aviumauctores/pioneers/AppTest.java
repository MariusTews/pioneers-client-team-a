package com.aviumauctores.pioneers;

import javafx.scene.control.PasswordField;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.assertions.api.Assertions.assertThat;

class AppTest extends ApplicationTest {
    private ScreenAsserts screenAsserts = new ScreenAsserts(this);

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        final App app = new App(null);
        MainComponent testComponent = DaggerTestComponent.builder().mainApp(app).build();
        app.start(stage);
        app.show(testComponent.loginController());

    }

    @Override
    public void stop() {
        this.stage = null;
        this.screenAsserts = null;
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

        //Go to achievement screen
        screenAsserts.assertLobbyScreen();
        clickOn("#achievementsIcon");

        screenAsserts.asserAchievementScreen();
        clickOn("#leaveButton");

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
        clickOn("OK");

        screenAsserts.assertLobbyScreen();
        // Go to chat screen
        clickOn("#chatButton");

        screenAsserts.assertChatScreen();
        // Go back to lobby
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#leaveButton");

        screenAsserts.assertLobbyScreen();
        // Go to settings
        clickOn("#settingsButton");

        screenAsserts.assertSettingsScreen();
        // Go back to lobby
        clickOn("#leaveButton");

        screenAsserts.assertLobbyScreen();
        // Go to achievements
        clickOn("#achievementsButton");

        screenAsserts.assertAchievementsScreen();
        // Go back to lobby
        clickOn("#leaveButton");

        screenAsserts.assertLobbyScreen();
        // Join a game
        clickOn("Join");
        // Wait for CI
        WaitForAsyncUtils.waitForFxEvents();

        //you join the game directly because you are already a member of the game

        screenAsserts.assertGameReadyScreen();
        clickOn("#gameReadyButton");
        clickOn("#pickColourMenu");
        type(KeyCode.KP_DOWN);
        sleep(200);
        type(KeyCode.ESCAPE);
        clickOn("#onButton");
        clickOn("#offButton");
        clickOn("#gameOptionButton");
        Spinner<Integer> vpSpinner = lookup("#victoryPoints").query();
        vpSpinner.getValueFactory().setValue(3);
        clickOn("#takeOverButton");
        clickOn("#startGameButton");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(500);


        screenAsserts.assertIngameScreen();
        HBox vpBox = lookup("#vpHbox").query();
        assertEquals(vpBox.getChildren().size(), 3);
        // founding 1
        clickOn("#buildingX0Y0Z0R0");
        clickOn("#buildingX0Y0Z0R11");
        // founding 2
        clickOn("#buildingX0Y2Z_2R6");
        clickOn("#buildingX0Y1Z_1R11");
        clickOn("#rollButton");
        // build street
        clickOn("#buildingX_1Y1Z0R3");
        clickOn("#buildButton");
        // build settlement
        clickOn("#buildingX_1Y0Z1R0");
        clickOn("#buildButton");

        WaitForAsyncUtils.waitForFxEvents();
        screenAsserts.assertPostGameScreen();
        clickOn("#showStatsButton");

        WaitForAsyncUtils.waitForFxEvents();
        screenAsserts.assertStatsScreen();
        clickOn("#leaveButton");

        screenAsserts.assertPostGameScreen();
        clickOn("#returnButton");

        screenAsserts.assertLobbyScreen();
        // Logout
        clickOn("#quitButton");

        // Now we should be back in login screen
        screenAsserts.assertLoginScreen();
    }
}