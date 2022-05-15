package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.model.User;
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
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import javax.inject.Provider;

import java.util.Locale;
import java.util.ResourceBundle;

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
    Provider<LoginController> loginController;

    @Mock
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
    void testRegisterController() {
        when(userService.register(anyString(), anyString())).thenReturn(Observable.just(new User("1", "Jannis", "online", "Lion")));
        FxAssert.verifyThat("#createAccountButton", NodeMatchers.isDisabled());
        write("Jannis\t");
        FxAssert.verifyThat("#createAccountButton", NodeMatchers.isDisabled());
        write("12345678\t");

        //check show password
        type(KeyCode.SPACE);
        verifyThat("#textfieldPassword_show", NodeMatchers.isVisible());
        verifyThat("#textfieldPassword", NodeMatchers.isInvisible());
        verifyThat("#textfieldPassword_show", hasText("12345678"));
        type(KeyCode.SPACE);
        verifyThat("#textfieldPassword_show", NodeMatchers.isInvisible());
        verifyThat("#textfieldPassword", NodeMatchers.isVisible());
        write("\t");

        //click create Button
        verifyThat("#createAccountButton", NodeMatchers.isEnabled());
        type(KeyCode.SPACE);
        verify(userService).register("Jannis", "12345678");
    }

}