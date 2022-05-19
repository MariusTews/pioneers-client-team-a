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
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.ALLCHAT_ID;
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

    @InjectMocks
    ChatController chatController;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @Override
    public void start(Stage stage) throws Exception {
        User user1 = new User("1", "user1", "online", null);
        Group group1 = new Group("1", "2", "3", null);
        Message message1 = new Message("1", "2", "3", "1", "hello");
        chatController.setUser(user1);
        when(userService.findAll()).thenReturn(Observable.just(List.of(user1)));
        when(userService.listOnlineUsers()).thenReturn(Observable.just(List.of(user1)));
        Observable<EventDto<User>> userUpdates = Observable.just(new EventDto<>("created", user1));
        Observable<EventDto<Message>> messageUpdates = Observable.just(new EventDto<>("created", message1));
        when(eventListener.listen("users.*.updated", User.class)).thenReturn(userUpdates);
        //when(eventListener.listen("groups.*.messages.*.*", Message.class)).thenReturn(messageUpdates);
        when(eventListener.listen("groups." + ALLCHAT_ID + ".messages.*.*", Message.class)).thenReturn(messageUpdates);
        new App(chatController).start(stage);
    }

    @Test
    void sendMessage() {
        when(messageService.sendGroupMessage(any(), any())).thenReturn(Observable.just("hello"));
        clickOn("#chatTextField");
        write("hello");
        type(KeyCode.ENTER);

        verify(messageService).sendGroupMessage("hello", ALLCHAT_ID);

    }

}