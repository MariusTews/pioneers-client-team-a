package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.dto.users.UpdateUserDto;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import static org.mockito.ArgumentMatchers.any;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingsControllerTest extends ApplicationTest {


    @Mock
    UserService userService;

    @Mock
    LoginService loginService;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @InjectMocks
    SettingsController settingsController;

    @Override
    public void start(Stage stage) throws Exception {
        //create User for the settings
        User user = new User("123", "Hans", "online", null, null);
        when(userService.getUserByID(any())).thenReturn(Observable.just(user));
        new App(settingsController).start(stage);
    }

    @Test
    void changeUserName() {
        when(loginService.checkPasswordLogin(any(), any())).thenReturn(Observable.just(new LoginResult("123", "Hans", "online", null, null, null, null)));
        when(userService.updateUser(any(), new UpdateUserDto(any(), null, null, null, null)))
                .thenReturn(Observable.empty());

        //click through the screen and change Name from Hans to Peter
        clickOn("#changeNameButton");
        clickOn("#newParameterField");
        write("Peter");
        clickOn("#acceptChangesButton");

        verify(userService).updateUser("123", new UpdateUserDto("Peter", null, null, null, null));
    }

}