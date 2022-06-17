package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.GameMemberService;
import com.aviumauctores.pioneers.service.GameService;
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
import org.testfx.matcher.base.NodeMatchers;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
class GameReadyChatControllerTest extends ApplicationTest {

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

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @InjectMocks
    GameReadyController gameReadyController;

    private Observable<EventDto<Message>> messageCreateUpdates;

    private final Message message = new Message("", "", "1", "1", "hello");

    @Override
    public void start(Stage stage) throws Exception {
        Member member = new Member("", "", "1", "1", false, null);
        User user = new User("1", "Struppi", "online", null, null);
        messageCreateUpdates = Observable.just(new EventDto<>(".created", message));
        when(gameMemberService.listCurrentGameMembers()).thenReturn(Observable.just(List.of(member)));
        when(gameService.getCurrentGameID()).thenReturn("1");
        when(userService.getUserName(anyString())).thenReturn(Observable.just("Player1"));
        when(userService.getCurrentUserID()).thenReturn("1");
        when(gameService.getCurrentGame()).thenReturn(Observable.just(new Game("1", "2", "12", "name", "42", false, 1)));
        when(messageService.sendGameMessage(anyString(), anyString())).thenReturn(Observable.just(message));
        when(eventListener.listen(anyString(), any())).thenReturn(Observable.empty());
        when(eventListener.listen("games.1.members.*.*", Member.class)).thenReturn(Observable.just(new EventDto<>("", member)));
        when(eventListener.listen("games.1.messages.*.*", Message.class)).thenReturn(messageCreateUpdates);
        when(eventListener.listen("users.1.*", User.class)).thenReturn(Observable.just(new EventDto<>("", user)));
        when(userService.getUserByID(anyString())).thenReturn(Observable.just(user));
        when(userService.getUserName(anyString())).thenReturn(Observable.just("Struppi"));
        new App(gameReadyController).start(stage);
    }

    @Override
    public void stop() {
        this.bundle = null;
        this.eventListener = null;
        this.gameMemberService = null;
        this.gameReadyController = null;
        this.gameService = null;
        this.messageCreateUpdates = null;
        this.userService = null;
        this.messageService = null;
    }


    @Test
    void sendMessage() {
        Game game = new Game("", "", "1", "game1", "1", false, 1);
        clickOn("#messageTextField");
        write("hello");
        type(KeyCode.ENTER);
        EventDto<Message> createdMessageEventDto = messageCreateUpdates.blockingFirst();

        verifyThat("#1", NodeMatchers.isVisible());
        verify(messageService).sendGameMessage("hello", "1");
    }

    @Test
    void delete() {
        when(messageService.deleteGameMessage(any(), any())).thenReturn(Observable.just(message));

        clickOn("#messageTextField");
        write("hello");
        type(KeyCode.ENTER);

        //create a Message
        EventDto<Message> createdMessageEventDto = messageCreateUpdates.blockingFirst();

        //click on it and choose Ok to delete it
        rightClickOn("#1");
        clickOn("OK");

        verify(messageService).deleteGameMessage("1", "1");

    }
}