package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.GameMemberService;
import com.aviumauctores.pioneers.service.GameService;
import com.aviumauctores.pioneers.service.UserService;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasItems;
import static org.testfx.matcher.control.ListViewMatchers.isEmpty;

@ExtendWith(MockitoExtension.class)
class GameReadyControllerTest extends ApplicationTest {
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

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @InjectMocks
    GameReadyController gameReadyController;

    private Observable<List<Member>> existingMembers;
    private Observable<EventDto<Member>> memberUpdates;

    @Override
    public void start(Stage stage) throws Exception {
        Member existingMember = new Member("", "", "12", "1", true);
        Member createdMember = new Member("", "", "12", "42", true);
        when(userService.getUserByID("1")).thenReturn(
                Observable.just(new User("1", "Player1", "online", null)));
        when(userService.getUserByID("42")).thenReturn(
                Observable.just(new User("42", "Player42", "online", null)));

        //when(gameService.getCurrentGame()).thenReturn(Observable.just(new Game("1", "2", "12", "name", "42", 2 )));
        when(gameService.getCurrentGameID()).thenReturn("12");
        existingMembers = Observable.just(List.of(existingMember));
        when(gameMemberService.listCurrentGameMembers()).thenReturn(existingMembers);
        memberUpdates = Observable.just(new EventDto<>("created", createdMember));
        when(eventListener.listen(anyString(), any())).thenReturn(Observable.empty());
        when(eventListener.listen("games.12.members.*.*", Member.class)).thenReturn(memberUpdates);
        new App(gameReadyController).start(stage);
    }

    @Test
    void gameMemberListUpdates() {

        final ListView<Parent> playerList = lookup("#playerList").queryListView();
        // Get existing members
        List<Member> exMembersList = existingMembers.blockingFirst();
        // create a member
        EventDto<Member> createdMemberEventDto = memberUpdates.blockingFirst();
        // The list should now have 2 items: one existing member from REST and one new member
        verifyThat(playerList, hasItems(2));
    }


}