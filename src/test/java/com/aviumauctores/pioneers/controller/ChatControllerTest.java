package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.Group;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.GroupService;
import com.aviumauctores.pioneers.service.MessageService;
import com.aviumauctores.pioneers.service.UserService;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.ALLCHAT_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest extends ApplicationTest {

    @Mock
    App app;

    @Mock
    UserService userService;

    @Mock
    GroupService groupService;

    @Mock
    MessageService messageService;

    @Mock
    EventListener eventListener;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @InjectMocks
    ChatController chatController;

    private Observable<EventDto<User>> userUpdates;
    private Observable<EventDto<Message>> messageCreateUpdates;

    @Override
    public void start(Stage stage) throws Exception {
        User user1 = new User("1", "user1", "online", null,null);
        Group group1 = new Group("1", "2", "3", null);
        Message message1 = new Message("1", "2", "3", "1", "hello");
        when(userService.findAll()).thenReturn(Observable.just(List.of(user1)));
        when(userService.listOnlineUsers()).thenReturn(Observable.just(List.of(user1)));
        //when(groupService.updateGroup(any(), any())).thenReturn(Observable.empty());
        when(messageService.listMessages(any(), any(), any(), eq(100) )).thenReturn(Observable.just(List.of(message1)));
        userUpdates = Observable.just(new EventDto<>(".created", user1));
        messageCreateUpdates = Observable.just(new EventDto<>(".created", message1));
        when(eventListener.listen("users.*.updated", User.class)).thenReturn(userUpdates);
        when(eventListener.listen("*." + ALLCHAT_ID + ".messages.*.*", Message.class)).thenReturn(messageCreateUpdates);
        new App(chatController).start(stage);
    }

    @Test
    void sendMessage() {
        when(messageService.sendGroupMessage(any(), any(), any())).thenReturn(Observable.just("hello"));
        clickOn("#chatTextField");
        write("hello");
        type(KeyCode.ENTER);

        verify(messageService).sendGroupMessage("global", "hello", ALLCHAT_ID);

    }

    @Test
    void delete() {
        Message message1 = new Message("1", "2", "3", "1", "hello");
        when(userService.getCurrentUserID()).thenReturn("1");
        when(messageService.getMessage(any(), any(), any())).thenReturn(Observable.just(message1));
        when(messageService.deleteMessage(any(), any(), any())).thenReturn(Observable.empty());

        //create a Message
        EventDto<Message> createdMessageEventDto = messageCreateUpdates.blockingFirst();

        //click on it and choose Ok to delete it
        rightClickOn("#3");
        clickOn("OK");


        verify(messageService).deleteMessage("global", "3", ALLCHAT_ID);

    }

    @Test
    void privateChats() {
        User user1 = new User("1", "user1", "online", null,null);
        when(messageService.sendGroupMessage(any(), any(), any())).thenReturn(Observable.just("hello"));

        // create ChatTab
        TabPane tabPane = lookup("#chatTabPane").query();
        Tab privateTab = chatController.createTab("17", user1);

        Platform.runLater(() -> tabPane.getTabs().add(privateTab));


        WaitForAsyncUtils.waitForFxEvents();


        // select Tab and send message
        clickOn("#17");
        clickOn("#chatTextField");
        write("hello");
        type(KeyCode.ENTER);


        verify(messageService).sendGroupMessage("groups", "hello", "17");
    }

}