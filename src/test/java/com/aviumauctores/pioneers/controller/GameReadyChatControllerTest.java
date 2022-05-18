package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.*;
import com.aviumauctores.pioneers.service.*;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameReadyChatControllerTest extends ApplicationTest {
    @Mock
    App app;

    @Mock
    UserService userService;

    @Mock
    GameMemberService gameMemberService;

    @Mock
    MessageService messageService;

    @Mock
    GameService gameService;

    @Mock
    EventListener eventListener;

    @InjectMocks
    GameReadyController gameReadyController;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @Override
    public void start(Stage stage) throws Exception {
        Member member = new Member("", "", "", "", false);
        Message message = new Message("", "", "", "1", "hello");
        User user = new User("", "Struppi", "online", null);
        when(messageService.sendGameMessage(any(), any())).thenReturn(Observable.just(message));
        when(gameMemberService.listCurrentGameMembers()).thenReturn(Observable.just(List.of(member)));
        when(gameService.getCurrentGameID()).thenReturn("");
        when(eventListener.listen("games." + gameService.getCurrentGameID() + ".members.*.*", Member.class)).thenReturn(Observable.just(new EventDto<>("", member)));
        when(eventListener.listen("games." + gameService.getCurrentGameID() + ".messages.*.*", Message.class)).thenReturn(Observable.just(new EventDto<>("", message)));
        when(eventListener.listen("users." + "" + ".*", User.class)).thenReturn(Observable.just(new EventDto<>("", user)));
        when(userService.getUserByID(anyString())).thenReturn(Observable.just(user));
        new App(gameReadyController).start(stage);
    }


    @Test
    void sendMessage() {
        Game game = new Game("", "", "", "game1", "1", 1);
        clickOn("#messageTextField");
        write("hello");
        type(KeyCode.ENTER);

        verify(messageService).sendGameMessage("hello", "");
    }
}