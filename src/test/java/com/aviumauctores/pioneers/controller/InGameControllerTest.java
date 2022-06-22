package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.Map;
import com.aviumauctores.pioneers.model.*;
import com.aviumauctores.pioneers.service.*;
import com.aviumauctores.pioneers.sounds.GameMusic;
import com.aviumauctores.pioneers.sounds.GameSounds;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class InGameControllerTest extends ApplicationTest {
    @Mock
    UserService userService;

    @Mock
    ColorService colorService;

    @Mock
    GameMemberService gameMemberService;

    @Mock
    BuildService buildService;

    @Mock
    GameService gameService;

    @Mock
    PioneerService pioneerService;

    @Mock
    SoundService soundService;

    @Mock
    EventListener eventListener;

    @Mock
    GameMusic gameMusic;

    @Mock
    GameSounds gameSound;

    @Mock
    Provider<SettingsController> settingsController;

    @Mock
    StateService stateService;

    @Mock
    Provider<GameReadyController> gameReadyController;

    // For some reason Mockito doesn't want a lambda expression
    @SuppressWarnings("Convert2Lambda")
    @Spy
    Provider<InGameChatController> inGameChatController = new Provider<>() {
        @Override
        public InGameChatController get() {
            return new InGameChatController(null, null, null, null,
                    null, null, null, null, null, null) {
                @Override
                public void init() {
                    // Do nothing
                }

                @Override
                public Parent render() {
                    return new VBox();
                }

                @Override
                public void destroy(boolean closed) {
                    // Do nothing
                }
            };
        }
    };

    @Mock
    PlayerResourceListController playerResourceListController;

    @Mock
    ErrorService errorService;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @InjectMocks
    InGameController inGameController;

    private PublishSubject<EventDto<State>> stateUpdates;

    private PublishSubject<EventDto<Player>> playerUpdates;


    @Override
    public void start(Stage stage) throws Exception {
        when(userService.getCurrentUserID()).thenReturn("1");
        when(gameMemberService.getMember("1")).thenReturn(Observable.just(new Member("", "", "12", "1", true, Color.GREEN, false)));
        when(gameService.getCurrentGameID()).thenReturn("12");
        Player player = new Player("12", "1", "#008000", true,
                2, new HashMap<>(), new HashMap<>(), 0, 0);
        when(pioneerService.getPlayer("1")).thenReturn(Observable.just(player));
        when(pioneerService.getState()).thenReturn(Observable.just(new State("", "12",
                List.of(new ExpectedMove("roll", List.of("1"))), new Point3D(1, 3, 4))));

        when(soundService.createGameMusic(any())).thenReturn(new GameMusic());
        stateUpdates = PublishSubject.create();
        playerUpdates = PublishSubject.create();
        when(eventListener.listen(anyString(), any())).thenReturn(Observable.empty());
        when(pioneerService.createMove("founding-roll", null, null, null)).thenReturn(Observable.just(new Move("69",
                "420", "12", "1", "founding-roll", 2, null, null, null, null)));
        when(pioneerService.getMap()).thenReturn(Observable.just(new Map("101", List.of(new Tile(0, 0, 0, "desert", 10)), List.of(new Harbor(1, 1, 1, "dessert", 0)))));
        when(eventListener.listen("games." + gameService.getCurrentGameID() + ".state.*", State.class)).thenReturn(stateUpdates);
        when(eventListener.listen("games." + gameService.getCurrentGameID() + ".players." + userService.getCurrentUserID() + ".updated", Player.class)).thenReturn(playerUpdates);
        new App(inGameController).start(stage);
    }

    @Override
    public void stop() {
        this.stateUpdates = null;
        this.bundle = null;
        this.colorService = null;
        this.eventListener = null;
        this.gameMemberService = null;
        this.gameService = null;
        this.inGameChatController = null;
        this.inGameController = null;
        this.playerResourceListController = null;
        this.soundService = null;
        this.userService = null;
        this.pioneerService = null;
    }

    @Test
    void onFieldClicked() {
        Pane crossingPane = lookup("#crossingPane").query();
        crossingPane.setVisible(true);
        // Open the build menu
        clickOn("#building01_10");
        Optional<Node> settlementLabel = lookup("Settlement").tryQuery();
        assertThat(settlementLabel).isPresent();
    }

    @Test
    void onMainPaneClicked() {
        Pane crossingPane = lookup("#crossingPane").query();
        crossingPane.setVisible(true);
        clickOn("#building01_10");
        // Click on main pane to close the build menu
        clickOn("#mainPane");
        Optional<Node> settlementLabel = lookup("Settlement").tryQuery();
        assertThat(settlementLabel).isNotPresent();
    }

    @Test
    void onRollClicked() {
        when(pioneerService.createMove("roll", null, null, null)).thenReturn(Observable.just(new Move("42", "MountDoom", "12", "1", "roll", 5, null, null, null, null)));
        when(soundService.createGameSounds(any())).thenReturn(null);
        clickOn("#rollButton");
        verify(pioneerService).createMove("roll", null, null, null);
    }

    @Test
    void soundtest() {
        when(gameMusic.isRunning()).thenReturn(true);
        clickOn("#soundImage");
        assertThat(gameMusic.isRunning()).isEqualTo(true);

    }

    @Test
    void soundtest2() {
        when(gameSound.isRunning()).thenReturn(true);
        clickOn("#soundImage");
        assertThat(gameSound.isRunning()).isEqualTo(true);

    }

    @Test
    void showYourTurn() {
        ImageView arrow = lookup("#arrowOnDice").query();
        Label yourTurn = lookup("#yourTurnLabel").query();
        assertThat(arrow.isVisible()).isTrue();
        assertThat(yourTurn.isVisible()).isTrue();
    }

    @Test
    void openDropWindow() {
        HashMap<String, Integer> resources = new HashMap<>();
        when(stateService.getUpdatedPlayer()).thenReturn(new Player(gameService.getCurrentGameID(), userService.getCurrentUserID(), "#008000", 2, resources, null));

        stateUpdates.onNext(new EventDto<>("created",
                new State("", gameService.getCurrentGameID(), List.of(new ExpectedMove("drop", List.of(userService.getCurrentUserID()))))));

        Optional<Node> dropButton = lookup("#dropButton").tryQuery();
        assertThat(dropButton).isNotPresent();

        resources.put("ore", 8);

        stateUpdates.onNext(new EventDto<>("created",
                new State("", gameService.getCurrentGameID(), List.of(new ExpectedMove("drop", List.of(userService.getCurrentUserID()))))));

        assertThat(dropButton).isPresent();
    }
}