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
import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasItems;

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

    @Mock
    Provider<InGameController> inGameController;

    @Mock
    Provider<LobbyController> lobbyController;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @InjectMocks
    GameReadyController gameReadyController;

    private Observable<List<Member>> existingMembers;
    private PublishSubject<EventDto<Member>> memberUpdates;

    @Override
    public void start(Stage stage) throws Exception {
        Member existingMember = new Member("", "", "12", "1", true, null);

        when(userService.getUserByID("1")).thenReturn(
                Observable.just(new User("1", "Player1", "online", null, null)));
        when(userService.getUserName(anyString())).thenReturn(Observable.just("Player1"));
        when(userService.getCurrentUserID()).thenReturn("1");
        when(gameService.getCurrentGame()).thenReturn(Observable.just(new Game("1", "2", "12", "name", "42", false, 1)));
        when(gameService.getCurrentGameID()).thenReturn("12");
        existingMembers = Observable.just(List.of(existingMember));
        when(gameMemberService.listCurrentGameMembers()).thenReturn(existingMembers);
        memberUpdates = PublishSubject.create();
        when(eventListener.listen(anyString(), any())).thenReturn(Observable.empty());
        when(eventListener.listen("games.12.members.*.*", Member.class)).thenReturn(memberUpdates);
        new App(gameReadyController).start(stage);
    }

    @Override
    public void stop() {
        this.app = null;
        this.bundle = null;
        this.eventListener = null;
        this.existingMembers = null;
        this.gameMemberService = null;
        this.gameReadyController = null;
        this.gameService = null;
        this.inGameController = null;
        this.lobbyController = null;
        this.memberUpdates = null;
        this.userService = null;
    }

    @Test
    void gameMemberListUpdates() {
        when(userService.getUserByID("42")).thenReturn(
                Observable.just(new User("42", "Player42", "online", null, null)));

        final ListView<Parent> playerList = lookup("#playerList").queryListView();
        // Get existing members
        List<Member> exMembersList = existingMembers.blockingFirst();
        // create a member
        Member createdMember = new Member("", "", "12", "42", true, null);
        memberUpdates.onNext(new EventDto<>("created", createdMember));
        // The list should now have 2 items: one existing member from REST and one new member
        verifyThat(playerList, hasItems(2));
    }

    @Test
    void gameReady() {
        when(gameMemberService.updateMember(anyString())).thenReturn(Observable.just(new Member("", "", "12", "1", false, null)));
        when(userService.getCurrentUserID()).thenReturn("1");
        clickOn("#gameReadyButton");

        verify(gameMemberService).updateMember("1");
    }

    @Test
    void leaveGame() {
        when(userService.getCurrentUserID()).thenReturn("42");
        when(gameMemberService.deleteMember(anyString())).thenReturn(Observable.just(new Member("", "", null, "1", false, null)));
        clickOn("#leaveGameButton");
        clickOn("OK");
        verify(gameMemberService).updateID();
        verify(gameMemberService).deleteMember("42");
        verify(gameService).setCurrentGameID(null);
    }

    @Test
    void deleteGame() {
        when(userService.getCurrentUserID()).thenReturn("1");
        when(gameService.getOwnerID()).thenReturn("1");
        when(gameService.deleteGame()).thenReturn(Observable.just(new Game("", "", null, "name", "1", false, 0)));
        clickOn("#leaveGameButton");
        clickOn("OK");
        verify(gameService).deleteGame();
        verify(gameService).setCurrentGameID(null);
    }

    @Test
    void startGameFailedColors() {
        clickOn("#startGameButton");

        // Both players shouldn't have a set color by default, so this error message is expected
        verify(app).showErrorDialog(
                bundle.getString("cannot.start.game"), bundle.getString("not.all.members.coloured")
        );
    }

    @Test
    void startGameFailedMembersNotReady() {
        memberUpdates.onNext(new EventDto<>("updated",
                new Member("", "", "12", "1", false, null)));

        clickOn("#startGameButton");

        verify(app).showErrorDialog(
                bundle.getString("cannot.start.game"), bundle.getString("not.all.members.ready")
        );
    }

    @Test
    void testColour() {
        when(userService.getCurrentUserID()).thenReturn("1");
        String colourHexBlue = "#" + Color.BLUE.toString().substring(2, 8);
        when(gameMemberService.updateColour("1", colourHexBlue)).thenReturn(Observable.just(new Member("", "", "12", "1", false, Color.BLUE)));
        clickOn("#pickColourMenu");
        clickOn("#item_" + Color.BLUE);
        verify(gameMemberService).updateColour("1", colourHexBlue);
    }
}