package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.*;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.w3c.dom.Text;

import javax.inject.Provider;
import java.awt.event.MouseEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.util.NodeQueryUtils.hasText;

@ExtendWith(MockitoExtension.class)
public class CreateGameControllerTest extends ApplicationTest{

    @Mock
    App app;

    @Mock
    LobbyController lobbyController;

    @Mock
    Provider<GameReadyController> gameReadyController;

    @Mock
    GameService gameService;

    @Mock
    UserService userService;

    @Mock
    GameMemberService gameMemberService;

    @Mock
    ActionEvent event;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @InjectMocks
    CreateGameController createGameController;



    @Override
    public void start(Stage stage) {
       new App(createGameController).start(stage);

    }

    @Test
    void createGame(){
        TextField gameName = lookup("#gameNameInput").query();
        PasswordField password = lookup("#gamePasswordInput").query();
        TextField showPassword = lookup("#gamePasswordText").query();
        FxAssert.verifyThat("#createGameButton", NodeMatchers.isDisabled());

        clickOn(gameName);
        write("game");

        FxAssert.verifyThat("#createGameButton", NodeMatchers.isDisabled());

        clickOn(password);
        write("password");
        FxAssert.verifyThat("#createGameButton", NodeMatchers.isEnabled());
        when(gameService.create(anyString(), anyString())).thenReturn(Observable.just(new Game("1", "1", "gameID", "game", "owner", 1).toString()));
        when(gameService.updateGame()).thenReturn(Observable.just(new Game("1", "2", "gameID", "game", "owner", 1)));
        clickOn("Create Game");
        verify(gameService).create("game", "password");
        verify(gameService).updateGame();
    }

    @Test
    void showPassword(){

        final PasswordField passwordTextField = lookup("#gamePasswordInput").query();
        final TextField showPasswordTextField = lookup("#gamePasswordText").query();

        // Ensure that only the masked text field is visible
        verifyThat(passwordTextField, isVisible());
        verifyThat(showPasswordTextField, isInvisible());

        clickOn(passwordTextField);
        write("1234567");

        clickOn("#showPasswordButton");

        // Now ensure that only the unmasked text field is visible
        verifyThat(passwordTextField, isInvisible());
        verifyThat(showPasswordTextField, isVisible());

        // Ensure the content of the text fields are equal
        assertEquals(passwordTextField.getText(), showPasswordTextField.getText());


        clickOn("#showPasswordButton");

        // A second click on the button should make the masked text field visible again
        verifyThat(passwordTextField, isVisible());
        verifyThat(showPasswordTextField, isInvisible());

    }
}
