package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.UserService;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import static com.aviumauctores.pioneers.Constants.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest extends ApplicationTest {

    @Mock
    UserService userService;
    @Mock
    App app;

    @InjectMocks
    RegisterController registerController;

    @Override
    public void start(Stage stage) throws Exception {
        new App(registerController).start(stage);
    }

    @Test
    void testRegisterController() {
        //TODO run complete Test
        when(userService.register(anyString(), anyString())).thenReturn(new Observable<>() {
            @Override
            protected void subscribeActual(@NonNull Observer<? super User> observer) {

            }
        });
        FxAssert.verifyThat("#accountErstellenButton", NodeMatchers.isDisabled());
        write("Jannis\t");
        FxAssert.verifyThat("#accountErstellenButton", NodeMatchers.isDisabled());
        write("12345678\t");
        //check show password
        type(KeyCode.SPACE);
        verifyThat("#showPassword", hasText("Ihr Passwort: 12345678"));
        type(KeyCode.SPACE);
        verifyThat("#showPassword", hasText(""));
        write("\t");
        //click erstellen Button
        verifyThat("#accountErstellenButton", NodeMatchers.isEnabled());
        type(KeyCode.SPACE);
        verify(userService).register("Jannis", "12345678");
    }

    @Test
    void testUsernameTaken() {
        when(userService.register(anyString(), anyString())).thenReturn(Observable.error(new Throwable(HTTP_409)));
        write("Jannis\t");
        write("1234\t");
        write("\t");
        type(KeyCode.SPACE);

        //verifyThat("Username vergeben", NodeMatchers.isVisible());
        verify(userService).register("Jannis", "1234");
    }

    @Test
    void RateLimitReached() {
        when(userService.register(anyString(), anyString())).thenReturn(Observable.error(new Throwable(HTTP_429)));
        write("Jannis\t");
        write("1234\t");
        write("\t");
        type(KeyCode.SPACE);

        //verifyThat("Username vergeben", NodeMatchers.isVisible());
        verify(userService).register("Jannis", "1234");

    }

    @Test
    void testValidationFailed() {
        when(userService.register(anyString(), anyString())).thenReturn(Observable.error(new Throwable(HTTP_400)));
        write("Jannis\t");
        write("1234\t");
        write("\t");
        type(KeyCode.SPACE);
        //verifyThat("Username vergeben", NodeMatchers.isVisible());
        verify(userService).register("Jannis", "1234");

    }
}