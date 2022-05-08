package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.service.LoginService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest extends ApplicationTest {

    @Mock
    LoginService loginService;

    @Mock
    App app;

    @InjectMocks
    LoginController loginController;

    @Override
    public void start(Stage stage){
        new App(loginController).start(stage);
    }

    @Test
    void login() {
        when(loginService.login(any(), any())).thenReturn(Observable.just(new LoginResult("1", "Struppi", "online", null, "a", "r")));

        write("Struppi\t");
        write("12345678\t");
        type(KeyCode.SPACE);

        verify(loginService).login("Struppi", "12345678");
    }
}