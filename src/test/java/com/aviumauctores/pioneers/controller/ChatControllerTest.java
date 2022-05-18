package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.MainComponent;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.dto.messages.CreateMessageDto;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.Group;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.rest.GroupsApiService;
import com.aviumauctores.pioneers.rest.MessagesApiService;
import com.aviumauctores.pioneers.rest.UsersApiService;
import com.aviumauctores.pioneers.service.GroupService;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.MessageService;
import com.aviumauctores.pioneers.service.UserService;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
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
import java.util.Locale;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.ALLCHAT_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasItems;

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

    private Observable<EventDto<User>> userUpdates;
    private Observable<EventDto<Message>> messageUpdates;
    @Override
    public void start(Stage stage) throws Exception {
        User user1 = new User("1", "user1", "online", null);
        Group group1 = new Group("1", "2", "3", null);
        Message message1 = new Message("1", "2", "3", "1", "hello");
        chatController.setUser(user1);
        when(userService.findAll()).thenReturn(Observable.just(List.of(user1)));
        when(userService.listOnlineUsers()).thenReturn(Observable.just(List.of(user1)));
        when(groupService.updateGroup(any(), any())).thenReturn(Observable.empty());
        when(messageService.listMessages(any(), any(), any(), eq(100) )).thenReturn(Observable.just(List.of(message1)));
        userUpdates = Observable.just(new EventDto<>("created", user1));
        messageUpdates = Observable.just(new EventDto<>("created", message1));
        when(eventListener.listen("users.*.updated", User.class)).thenReturn(userUpdates);
        //when(eventListener.listen("groups.*.messages.*.*", Message.class)).thenReturn(messageUpdates);
        when(eventListener.listen("groups." + ALLCHAT_ID + ".messages.*.*", Message.class)).thenReturn(messageUpdates);
        new App(chatController).start(stage);
    }

    @Test
    void sendMessage() {
        when(messageService.sendMessage(any(), any())).thenReturn(Observable.just("hello"));
        clickOn("#chatTextField");
        write("hello");
        type(KeyCode.ENTER);

        verify(messageService).sendMessage("hello", ALLCHAT_ID);

    }

    @Test
    void delete() {
/*        when(messageService.deleteMessage(any(), any())).thenReturn(Observable.empty());

        Message message1 = new Message("1", "2", "3", "1", "hello");
        VBox vBox = lookup("#allChatVBox").query();
        Label msgLabel = new Label("hello");
        msgLabel.setId("3");
        vBox.getChildren().add(msgLabel);
        rightClickOn(msgLabel);
        clickOn("#Ok");

        verify(messageService).deleteMessage("3", "627cf3c93496bc00158f3859");
*/


    }

}