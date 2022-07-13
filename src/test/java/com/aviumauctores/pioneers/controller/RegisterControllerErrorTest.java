package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.service.ErrorService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
public class RegisterControllerErrorTest extends ApplicationTest {
    @Mock
    UserService userService;

    @Mock
    Provider<LoginController> loginController;

    @Mock
    PreferenceService preferenceService;

    @Mock
    ErrorService errorService;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @Spy
    App app;

    @InjectMocks
    RegisterController registerController;

    @Override
    public void start(Stage stage) {
        new App(registerController).start(stage);
    }

    @Override
    public void stop() {
        this.loginController = null;
        this.bundle = null;
        this.registerController = null;
        this.userService = null;
        this.preferenceService = null;
    }

    @Test
    void testUsernameTaken() {
        when(userService.register(anyString(), anyString())).thenReturn(Observable.error(new Throwable()));
        write("Jannis\t");
        write("1234\t");
        write("\t");
        type(KeyCode.SPACE);
        verifyThat(bundle.getString("smth.went.wrong"), NodeMatchers.isVisible());
        verifyThat(bundle.getString("try.again"), NodeMatchers.isVisible());
        verify(userService).register("Jannis", "1234");
    }

    @Test
    void RateLimitReached() {
        when(userService.register(anyString(), anyString())).thenReturn(Observable.error(new Throwable()));
        write("Jannis\t");
        write("1234\t");
        write("\t");
        type(KeyCode.SPACE);
        verifyThat(bundle.getString("smth.went.wrong"), NodeMatchers.isVisible());
        verifyThat(bundle.getString("try.again"), NodeMatchers.isVisible());
        verify(userService).register("Jannis", "1234");

    }

    @Test
    void testValidationFailed() {
        when(userService.register(anyString(), anyString())).thenReturn(Observable.error(new Throwable()));
        write("Jannis\t");
        write("1234\t");
        write("\t");
        type(KeyCode.SPACE);
        verifyThat(bundle.getString("smth.went.wrong"), NodeMatchers.isVisible());
        verifyThat(bundle.getString("try.again"), NodeMatchers.isVisible());
        verify(userService).register("Jannis", "1234");

    }

    @Test
    void testNoServerConnection() {
        when(userService.register(anyString(), anyString())).thenReturn(Observable.error(new Throwable("HTTP 402 ")));
        write("Jannis\t");
        write("1234\t");
        write("\t");
        type(KeyCode.SPACE);
        verifyThat(bundle.getString("smth.went.wrong"), NodeMatchers.isVisible());
        verifyThat(bundle.getString("try.again"), NodeMatchers.isVisible());
        verify(userService).register("Jannis", "1234");
    }
}
