package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.GameSettings;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.*;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Completable;
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

import javax.inject.Provider;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasItems;


@ExtendWith(MockitoExtension.class)
class LobbyControllerTest extends ApplicationTest {
    @Mock
    App app;

    @Mock
    LoginService loginService;

    @Mock
    UserService userService;

    @Mock
    GameService gameService;

    @Mock
    EventListener eventListener;

    @Mock
    PreferenceService preferenceService;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @InjectMocks
    LobbyController lobbyController;

    @Mock
    ErrorService errorService;

    @Mock
    Provider<LoginController> loginController;

    @Mock
    Provider<ChatController> chatController;

    @Mock
    Provider<CreateGameController> createGameController;

    @Mock
    Provider<JoinGameController> joinGameController;


    private Observable<EventDto<User>> userUpdates;
    private Observable<EventDto<Game>> gameUpdates;

    @Override
    public void start(Stage stage) {
        when(gameService.listGames()).thenReturn(Observable.empty());
        when(userService.listOnlineUsers()).thenReturn(Observable.empty());
        when(eventListener.listen(anyString(), any())).thenReturn(Observable.empty());
        User createdUser = new User("1", "Player1", "online", null, null);
        userUpdates = Observable.just(new EventDto<>("created", createdUser));
        Game createdGame = new Game("", "", "1", "Game1", "2", false, 1, new GameSettings(2, 10));
        gameUpdates = Observable.just(new EventDto<>("created", createdGame));
        when(eventListener.listen("users.*.*", User.class)).thenReturn(userUpdates);
        when(eventListener.listen("games.*.*", Game.class)).thenReturn(gameUpdates);
        new App(lobbyController).start(stage);
    }

    @Override
    public void stop() {
        this.userUpdates = null;
        this.bundle = null;
        this.app = null;
        this.userService = null;
        this.preferenceService = null;
        this.loginService = null;
        this.lobbyController = null;
        this.eventListener = null;
        this.gameService = null;
        this.gameUpdates = null;
    }

    @Test
    void gameListUpdates() {
        final ListView<Parent> gameListView = lookup("#gameListView").queryListView();
        // Create a game
        EventDto<Game> createdGameEventDto = gameUpdates.blockingFirst();
        // Ensure the game has been added to the list view
        verifyThat(gameListView, hasItems(1));
    }

    @Test
    void playerListUpdates() {
        final ListView<Parent> playerListView = lookup("#playerListView").queryListView();
        // Create a user
        EventDto<User> createdUserEventDto = userUpdates.blockingFirst();
        // Ensure the user has been added to the list view
        verifyThat(playerListView, hasItems(1));
    }

    @Test
    void quitFailed() {
        when(loginService.logout()).thenReturn(Completable.error(new Throwable()));

        clickOn("#quitButton");
        // Ensure a dialog is shown
        verify(errorService).handleError(any());
    }

    @Test
    void changeLanguage() {
        when(preferenceService.getLocale()).thenReturn(Locale.ENGLISH);
        clickOn("#britishFlag");
        verify(preferenceService).setLocale(Locale.ENGLISH);

        when(preferenceService.getLocale()).thenReturn(Locale.GERMAN);
        clickOn("#germanFlag");
        verify(preferenceService).setLocale(Locale.GERMAN);
    }
}