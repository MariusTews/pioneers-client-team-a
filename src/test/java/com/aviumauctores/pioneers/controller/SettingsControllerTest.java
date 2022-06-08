package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.dto.users.UpdateUserDto;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.Mockito.*;

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
        User user = new User("123", "Hans", "online", "xyz", null);
        when(userService.getUserByID(any())).thenReturn(Observable.just(user));
        new App(settingsController).start(stage);
    }

    @Test
    void changeUserName() {
        User user = new User("123", "Peter", "online", "xyz", null);
        when(loginService.checkPasswordLogin(any(), any())).thenReturn(Observable.just(new LoginResult("123", "Hans", "online", null, null, null, null)));
        when(userService.updateUser(any(), new UpdateUserDto(any(), null, null, null, null)))
                .thenReturn(Observable.just(user));

        //click through the screen and change Name from Hans to Peter
        clickOn("#changeNameButton");
        clickOn("#newParameterField");
        write("Peter");
        clickOn("#acceptChangesButton");

        WaitForAsyncUtils.waitForFxEvents();

        Label currentNameLabel = lookup("#currentNameLabel").query();
        assertEquals(currentNameLabel.getText(), "Peter");
        verify(userService).updateUser("123", new UpdateUserDto("Peter", null, null, null, null));
    }

    @Test
    void changeAvatar() {
        when(loginService.checkPasswordLogin(any(), any())).thenReturn(Observable.just(new LoginResult("123", "Hans", "online", "xyz", null, null, null)));
        when(userService.updateUser(any(), new UpdateUserDto(null, null, any(), null, null)))
                .thenReturn(Observable.empty());

        //click through the screen and change Avatar from xyz to abc
        clickOn("#changeAvatarButton");
        clickOn("#newParameterField");
        write("abc");

        clickOn("#acceptChangesButton");

        verify(userService).updateUser("123", new UpdateUserDto(null, null, "abc", null, null));
    }

    @Test
    void changePassword() {
        when(loginService.checkPasswordLogin(any(), any())).thenReturn(Observable.just(new LoginResult("123", "Hans", "online", null, null, null, null)));
        when(userService.updateUser(any(), new UpdateUserDto(null, null, null, any(), null)))
                .thenReturn(Observable.empty());

        //check that it does not work, if you don`t confirm the password
        clickOn("#changePasswordButton");
        clickOn("#newPasswordField");
        write("abc");
        clickOn("#acceptChangesButton");

        verify(userService, never()).updateUser(any(), new UpdateUserDto(null, null, null, any(), null));

        clickOn("#confirmField");
        write("abc");
        clickOn("#acceptChangesButton");

        verify(userService).updateUser(any(), new UpdateUserDto(null, null, null, any(), null));

    }

}