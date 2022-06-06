package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.service.GameMemberService;
import com.aviumauctores.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.Node;
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
import java.util.Optional;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InGameControllerTest extends ApplicationTest {
    @Mock
    UserService userService;

    @Mock
    GameMemberService gameMemberService;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @InjectMocks
    InGameController inGameController;

    @Override
    public void start(Stage stage) throws Exception {
        when(userService.getCurrentUserID()).thenReturn("1");
        when(gameMemberService.getMember("1")).thenReturn(Observable.just(new Member("", "", "12", "1", true, Color.GREEN)));
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
