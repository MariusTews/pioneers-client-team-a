package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.messages.CreateMessageDto;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.rest.GroupsApiService;
import com.aviumauctores.pioneers.rest.MessagesApiService;
import com.aviumauctores.pioneers.rest.UsersApiService;
import com.aviumauctores.pioneers.service.GroupService;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.MessageService;
import com.aviumauctores.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest extends ApplicationTest {

    @Mock
    MessagesApiService messagesApiService;

    @Mock
    UsersApiService usersApiService;

    @Mock
    GroupsApiService groupsApiService;

    @Mock
    UserService userService;

    @Mock
    App app;

    @InjectMocks
    ChatController chatController;

    @Override
    public void start(Stage stage){
        new App(chatController).start(stage);
    }

    @Test
    void sendMessage() {
        ObservableList<User> users = FXCollections.observableArrayList();
        when(userService.findAll()).thenReturn(Observable.just(users));
        doNothing().when(groupsApiService.updateGroup(any(), any()));
        when(messagesApiService.sendMessage(any(), any(), any()))
                .thenReturn(Observable.just(new Message("3", "3", "3", "123", "hello")));

        write("Hello, my name is ...");
        type(KeyCode.SPACE);

        verify(messagesApiService).sendMessage("groups","62756e8567968900144280a9",
                new CreateMessageDto("Hello, my name is ..."));

        FxAssert.verifyThat("#3", LabeledMatchers.hasText("Hello, my name is ..."));
        FxAssert.verifyThat("#chatTextField", TextMatchers.hasText(""));
    }
}