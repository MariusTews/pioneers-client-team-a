package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.service.GameMemberService;
import com.aviumauctores.pioneers.service.GameService;
import com.aviumauctores.pioneers.service.PioneerService;
import com.aviumauctores.pioneers.service.UserService;
import javafx.scene.control.SpinnerValueFactory;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameOptionControllerTest extends ApplicationTest {

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle ("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @Mock
    GameReadyController gameReadyController;

    @InjectMocks
    GameOptionController gameOptionController;

    @Mock
    UserService userService;

    @Mock
    GameMemberService gameMemberService;

    @Mock
    GameService gameService;

    @Mock
    PioneerService pioneerService;

    @Spy
    private SpinnerValueFactory<Integer> victoryValue;
    @Spy
    private SpinnerValueFactory<Integer> mapValue;


    @Override
    public void start(Stage stage) throws Exception {
        new App (gameOptionController).start (stage);
    }

    @Override
    public void stop() {
        this.bundle = null;
        this.gameMemberService = null;
        this.gameService = null;
        this.userService = null;
        this.pioneerService = null;
        this.gameReadyController = null;
        this.gameOptionController = null;
    }

    @Test
    void changeRadiusAndVictoryPoints() {
        when (victoryValue.getValue ()).thenReturn (10);
        when (mapValue.getValue ()).thenReturn (2);

        assertThat (victoryValue.getValue ()).isEqualTo (10);
        assertThat (mapValue.getValue ()).isEqualTo (2);


    }
}
