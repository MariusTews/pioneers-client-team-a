package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.service.GameService;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.PreferenceService;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LobbyControllerTest extends ApplicationTest {
    @Mock
    App app;

    @Mock
    LoginService loginService;

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

    @Override
    public void start(Stage stage) {
        when(gameService.listGames()).thenReturn(Observable.empty());
        when(eventListener.listen("games.*.*", Game.class)).thenReturn(Observable.empty());
        new App(lobbyController).start(stage);
    }

    @Test
    void quitFailed() {
        when(loginService.logout()).thenReturn(Completable.error(new Throwable()));

        clickOn("#quitButton");
        // Ensure a dialog is shown
        verify(app).showErrorDialog(anyString(),anyString());
    }
}