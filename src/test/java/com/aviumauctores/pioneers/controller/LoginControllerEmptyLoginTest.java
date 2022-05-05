package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.service.LoginService;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
class LoginControllerEmptyLoginTest extends ApplicationTest {

    @InjectMocks
    LoginController loginController;

    @Override
    public void start(Stage stage){
        new App(loginController).start(stage);
    }

    @Test
    void emptyLogin(){
        write("\t");
        write("\t");
        type(KeyCode.SPACE);

        verifyThat("#usernameErrorLabel", LabeledMatchers.hasText("Keine valide Eingabe."));
        verifyThat("#passwordErrorLabel", LabeledMatchers.hasText("Keine valide Eingabe."));
    }
}