package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
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

import static com.aviumauctores.pioneers.Constants.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

@ExtendWith(MockitoExtension.class)
public class RegisterControllerErrorTest extends ApplicationTest {
    @Mock
    UserService userService;

    @Mock
    Provider<LoginController> loginController;

    @Spy
    App app;

    @Mock
    PreferenceService preferenceService;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @InjectMocks
    RegisterController registerController;

    @Override
    public void start(Stage stage) {
        new App(registerController).start(stage);
    }

    @Test
    void testUsernameTaken() {
        when(userService.register(anyString(), anyString())).thenReturn(Observable.error(new Throwable(HTTP_409)));
        write("Jannis\t");
        write("1234\t");
        write("\t");
        type(KeyCode.SPACE);
        verifyThat("#errorLabel", hasText("Username schon vergeben"));
        verify(userService).register("Jannis", "1234");
    }

    @Test
    void RateLimitReached() {
        when(userService.register(anyString(), anyString())).thenReturn(Observable.error(new Throwable(HTTP_429)));
        write("Jannis\t");
        write("1234\t");
        write("\t");
        type(KeyCode.SPACE);
        verifyThat("#errorLabel", hasText("Bitte warten Sie einen Moment und versuchen es dann erneut."));
        verify(userService).register("Jannis", "1234");

    }

    @Test
    void testValidationFailed() {
        when(userService.register(anyString(), anyString())).thenReturn(Observable.error(new Throwable(HTTP_400)));
        write("Jannis\t");
        write("1234\t");
        write("\t");
        type(KeyCode.SPACE);
        verifyThat("#errorLabel", hasText("Validierung fehlgeschlagen. (Passwort zu kurz)"));
        verify(userService).register("Jannis", "1234");

    }

    @Test
    void testNoServerConnection() {
        when(userService.register(anyString(), anyString())).thenReturn(Observable.error(new Throwable("HTTP 402 ")));
        write("Jannis\t");
        write("1234\t");
        write("\t");
        type(KeyCode.SPACE);
        verifyThat("#errorLabel", hasText("Keine Verbindung zum Server."));
        verify(userService).register("Jannis", "1234");
    }
}
