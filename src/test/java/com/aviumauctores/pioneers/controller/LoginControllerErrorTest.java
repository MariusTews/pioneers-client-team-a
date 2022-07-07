package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.dto.users.UpdateUserDto;
import com.aviumauctores.pioneers.service.ErrorService;
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
import org.testfx.matcher.base.NodeMatchers;

import javax.inject.Provider;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
class LoginControllerErrorTest extends ApplicationTest {

    @Mock
    LoginService loginService;

    @Mock
    Provider<LobbyController> lobbyController;

    @Mock
    PreferenceService preferenceService;

    @Mock
    UserService userService;

    @Mock
    ErrorService errorService;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @Spy
    App app;

    @InjectMocks
    LoginController loginController;

    @Override
    public void start(Stage stage) {
        app = new App(loginController);
        app.start(stage);
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
        when(loginService.login(any(), any())).thenReturn(Observable.error(new Throwable()));

        write("Struppi\t");
        write("1\t");
        type(KeyCode.SPACE);

        verifyThat(bundle.getString("smth.went.wrong"), NodeMatchers.isVisible());
        verifyThat(bundle.getString("try.again"), NodeMatchers.isVisible());
        verify(loginService).login("Struppi", "1");
    }

    @Test
    void toLobby() {
        when(userService.updateUser("1", new UpdateUserDto("Mark", "online", "brr", null, null)))
                .thenReturn(Observable.error(new Throwable()));
        loginController.toLobby(new LoginResult("1", "Mark", "offline", "brr", "acc", "ref", null));

        //to show the error popup
        write("Struppi\t");
        write("1\t");

        //verify popup is visible
        verifyThat(bundle.getString("smth.went.wrong"), NodeMatchers.isVisible());
        verifyThat(bundle.getString("try.again"), NodeMatchers.isVisible());

        verify(userService).updateUser("1", new UpdateUserDto("Mark", "online", "brr", null, null));
    }
}