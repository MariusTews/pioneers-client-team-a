package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.service.LoginService;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;
import retrofit2.HttpException;
import retrofit2.Response;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.WaitForAsyncUtils.waitFor;

@ExtendWith(MockitoExtension.class)
class LoginControllerErrorTest extends ApplicationTest {

    @Mock
    LoginService loginService;

    @Mock
    App app;

    @InjectMocks
    LoginController loginController;

    @Override
    public void start(Stage stage) throws Exception {
        new App(loginController).start(stage);
    }

    @Test
    void login(){
        //when(loginService.login(any(), any())).thenReturn(Observable.just(new LoginResult("1", "Struppi", "offline", null, "a", "r"))).;
        when(loginService.login(any(), any())).thenReturn(Observable.error(new Throwable("HTTP 400 ")));

        write("marius\t");
        write("1\t");
        type(KeyCode.SPACE);
//        FxRobot robot = new FxRobot();
//        Boolean condition = robot.lookup("#dialogLabel").tryQuery().isPresent();
//        waitFor(5, TimeUnit.SECONDS, condition);
//        verifyThat("Falscher Benutzername oder falsches Passwort.", NodeMatchers.isVisible());
        verify(loginService).login("marius", "1");

    }
}