package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.DaggerTestComponent;
import com.aviumauctores.pioneers.MainComponent;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.PreferenceService;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
class LoginControllerEmptyLoginTest extends ApplicationTest {

    @Mock
    PreferenceService preferenceService;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @InjectMocks
    LoginController loginController;

    @Override
    public void start(Stage stage){
        new App(loginController).start(stage);
    }

    @Test
    void emptyLogin(){
        type(KeyCode.TAB);
        type(KeyCode.TAB);
        type(KeyCode.SPACE);

        verifyThat("#usernameErrorLabel", LabeledMatchers.hasText("Invalid input."));
        verifyThat("#passwordErrorLabel", LabeledMatchers.hasText("Invalid input."));
    }
}