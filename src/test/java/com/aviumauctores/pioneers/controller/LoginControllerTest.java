package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.dto.users.UpdateUserDto;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.PreferenceService;
import com.aviumauctores.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest extends ApplicationTest {

    @Mock
    LoginService loginService;

    @Mock
    UserService userService;

    @Mock
    PreferenceService preferenceService;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @Mock
    App app;

    @Mock
    Provider<LobbyController> lobbyController;

    @InjectMocks
    LoginController loginController;

    @Override
    public void start(Stage stage) {
        new App(loginController).start(stage);
    }

    @Override
    public void stop() {
        this.loginController = null;
        this.bundle = null;
        this.app = null;
        this.userService = null;
        this.preferenceService = null;
        this.loginService = null;
        this.lobbyController = null;
    }

    @Test
    void login() {
        when(loginService.login(any(), any())).thenReturn(Observable.just(new LoginResult("1", "Struppi", "online", null, "a", "r", null)));

        write("Struppi\t");
        write("12345678\t");
        type(KeyCode.SPACE);

        verify(loginService).login("Struppi", "12345678");
    }

    @Test
    void loginScreen() {
        clickOn("#sunIcon");
        verify(app).setTheme("light");

        clickOn("#moonIcon");
        verify(app).setTheme("dark");

        clickOn("#britishFlag");
        verify(preferenceService).setLocale(Locale.ENGLISH);

        clickOn("#germanFlag");
        verify(preferenceService).setLocale(Locale.GERMAN);
    }

    @Test
    void toLobby() {
        User user1 = new User("1", "Mark", "online", "brr", null);
        when(userService.updateUser("1", new UpdateUserDto("Mark", "online", "brr", null, null))).thenReturn(Observable.just(user1));
        loginController.toLobby(new LoginResult("1", "Mark", "offline", "brr", "acc", "ref", null));

        verify(userService).updateUser("1", new UpdateUserDto("Mark", "online", "brr", null, null));
    }
}