package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.PreferenceService;
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

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
class LoginControllerErrorTest extends ApplicationTest {

    @Mock
    LoginService loginService;

    @Mock
    PreferenceService preferenceService;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @Spy
    App app;

    @InjectMocks
    LoginController loginController;

    @Override
    public void start(Stage stage){
        app = new App(loginController);
        app.start(stage);
    }

    @Test
    void login(){
        when(loginService.login(any(), any())).thenReturn(Observable.error(new Throwable("HTTP 400 ")));

        write("Struppi\t");
        write("1\t");
        type(KeyCode.SPACE);

        verifyThat("Validation failed.", NodeMatchers.isVisible());
        verify(loginService).login("Struppi", "1");
    }
}