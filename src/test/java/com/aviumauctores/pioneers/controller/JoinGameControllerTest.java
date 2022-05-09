package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.service.CreateGameService;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@ExtendWith(MockitoExtension.class)
class JoinGameControllerTest extends ApplicationTest {
    @Mock
    App app;

    @Mock
    CreateGameService createGameService;

    @Mock
    EventListener eventListener;

    @InjectMocks
    JoinGameController joinGameController;

    @Override
    public void start(Stage stage) {
        when(createGameService.getCurrentGameID()).thenReturn("1");
        when(createGameService.getCurrentGame()).thenReturn(Observable.just(new Game("", "", "1", "testgame", "42", 1)));
        when(eventListener.listen("games.1.*", Game.class)).thenReturn(Observable.empty());
        new App(joinGameController).start(stage);
    }

    @Test
    void gameNameLabel() {
        // Ensure the game name is shown
        verifyThat("#gameNameLabel", hasText("testgame"));
    }

    @Test
    void showPassword() {
        final PasswordField passwordTextField = lookup("#passwordTextField").query();
        final TextField showPasswordTextField = lookup("#showPasswordTextField").query();

        // Ensure that only the masked text field is visible
        verifyThat(passwordTextField, isVisible());
        verifyThat(showPasswordTextField, isInvisible());

        write("1234567");

        clickOn("#showPasswordButton");

        // Now ensure that only the unmasked text field is visible
        verifyThat(passwordTextField, isInvisible());
        verifyThat(showPasswordTextField, isVisible());

        // Ensure the content of the text fields are equal
        assertEquals(passwordTextField.getText(), "1234567");
        assertEquals(showPasswordTextField.getText(), "1234567");

        clickOn("#showPasswordButton");

        // A second click on the button should make the masked text field visible again
        verifyThat(passwordTextField, isVisible());
        verifyThat(showPasswordTextField, isInvisible());
    }

    @Test
    void joinGameFailed() {
        when(createGameService.joinGame(anyString())).thenReturn(Observable.error(new Throwable()));

        write("abcdef");
        type(KeyCode.ENTER);
        // Ensure the dialog is shown
        verify(app).showConnectionFailedDialog();
    }
}