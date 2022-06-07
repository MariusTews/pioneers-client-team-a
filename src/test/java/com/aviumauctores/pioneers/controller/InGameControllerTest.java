package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.model.ExpectedMove;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.model.State;
import com.aviumauctores.pioneers.service.GameMemberService;
import com.aviumauctores.pioneers.service.GameService;
import com.aviumauctores.pioneers.service.PioneerService;
import com.aviumauctores.pioneers.service.UserService;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.Node;
import javafx.scene.Parent;
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
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InGameControllerTest extends ApplicationTest {
    @Mock
    UserService userService;

    @Mock
    GameMemberService gameMemberService;

    @Mock
    GameService gameService;

    @Mock
    PioneerService pioneerService;

    @Mock
    EventListener eventListener;

    @Spy
    Provider<InGameChatController> inGameChatController = () -> new InGameChatController(
            null, null, null, null,
            null, null, null, null) {
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

    @Spy
    PlayerResourceListController playerResourceListController = new PlayerResourceListController(null, null, null) {
        @Override
        public void init(VBox node, String startingPlayer) {
            // Do nothing
        }

        @Override
        public void createPlayerBox(Player player) {
            // Do nothing
        }

        @Override
        public void updateResourceList() {
            // Do nothing
        }

        @Override
        public void hideArrow(Player player) {
            // Do nothing
        }

        @Override
        public void showArrow(Player player) {
            // Do nothing
        }
    };

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @InjectMocks
    InGameController inGameController;

    @Override
    public void start(Stage stage) throws Exception {
        when(userService.getCurrentUserID()).thenReturn("1");
        when(gameMemberService.getMember("1")).thenReturn(Observable.just(new Member("", "", "12", "1", true, Color.GREEN)));
        when(gameService.getCurrentGameID()).thenReturn("12");
        Player player = new Player("12", "1", "#008000",
                2, 0, 0, 0, 0, 0,0, 0, 0, 0);
        when(pioneerService.getPlayer("1")).thenReturn(Observable.just(player));
        when(pioneerService.getState()).thenReturn(Observable.just(new State("", "12",
                List.of(new ExpectedMove("roll", List.of("12"))))));
        when(eventListener.listen(anyString(), any())).thenReturn(Observable.empty());
        new App(inGameController).start(stage);
    }

    @Test
    void onFieldClicked() {
        // Open the build menu
        clickOn("#building01_10");
        Optional<Node> settlementLabel = lookup("Settlement").tryQuery();
        assertThat(settlementLabel).isPresent();
    }

    @Test
    void onMainPaneClicked() {
        clickOn("#building01_10");
        // Click on main pane to close the build menu
        clickOn("#mainPane");
        Optional<Node> settlementLabel = lookup("Settlement").tryQuery();
        assertThat(settlementLabel).isNotPresent();
    }
}
