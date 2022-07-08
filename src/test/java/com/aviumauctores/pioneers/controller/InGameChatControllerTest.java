package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.*;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InGameChatControllerTest extends ApplicationTest {
    @Mock
    App app;
    @Mock
    UserService userService;
    @Mock
    GameService gameService;
    @Mock
    GameMemberService gameMemberService;
    @Mock
    EventListener eventListener;
    @Mock
    ErrorService errorService;
    @Mock
    MessageService messageService;
    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);
    @InjectMocks
    InGameChatController inGameChatController;

    @Override
    public void start(Stage stage) throws Exception {
        User user1 = new User("01", "user1", "online", null, null);
        when(userService.getUserByID("01")).thenReturn(Observable.just(user1));
        when(userService.getCurrentUserID()).thenReturn("01");
        Member member = new Member("", "", "1", "01", true, Color.GREEN, true);
        when(gameMemberService.getMember("01")).thenReturn(Observable.just(member));
        Message message1 = new Message("1", "2", "3", "01", "hello");
        Observable<EventDto<Message>> messageUpdates = Observable.just(new EventDto<>(".created", message1));
        when(gameService.getCurrentGameID()).thenReturn("1");
        when(eventListener.listen("games." + "1" + ".messages.*.*", Message.class)).thenReturn(messageUpdates);
        new App(inGameChatController).start(stage);
    }

    @Test
    void chatSpectatorTest() {
//  disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".messages.*.*", Message.class)
        Message message1 = new Message("1", "2", "3", "01", "hello");
        when(messageService.sendGameMessage(any(), any())).thenReturn(Observable.just(message1));
        clickOn("#messageTextField");
        write("hello");
        clickOn("#sendMessageButton");

        verify(messageService).sendGameMessage("hello", "1");
        Label spectatorLabel = lookup("#msgSpectatorLabel").query();
        assertThat(spectatorLabel.getTextFill()).isEqualTo(Color.GREY);
    }

    @Override
    public void stop() {
        this.inGameChatController = null;
        this.app = null;
        this.messageService = null;
        this.eventListener = null;
        this.bundle = null;
        this.userService = null;
        this.gameService = null;
        this.gameMemberService = null;

    }
}
