package com.aviumauctores.pioneers.controller;


import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.rob.RobDto;
import com.aviumauctores.pioneers.model.Move;
import com.aviumauctores.pioneers.service.ErrorService;
import com.aviumauctores.pioneers.service.PioneerService;
import io.reactivex.rxjava3.core.Observable;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TradeRequestControllerTest extends ApplicationTest {
    @Mock
    InGameController inGameController;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @Mock
    PioneerService pioneerService;

    @Mock
    ErrorService errorService;

    @Mock
    HashMap<String, Integer> tradeRessources;


    @InjectMocks
    TradeRequestController tradeRequestController;

    @Override
    public void start(Stage stage) {
        new App(tradeRequestController).start(stage);
    }

    @Test
    void testAcceptButton() {
        HashMap<String, Integer> resources = new HashMap<>();
        Move move = new Move("1", "2", "3", "4", "5", 0, "6", new RobDto(1,2,3, "4"), null, "7");
        when(pioneerService.createMove("offer", null, resources, null, null)).thenReturn(Observable.just(move));
        clickOn("#acceptButton");
        verify(pioneerService).createMove("offer", null, resources, null, null);
    }

    @Test
    void testDeclineButton() {
        Move move = new Move("1", "2", "3", "4", "5", 0, "6", new RobDto(1,2,3, "4"), null, "7");
        when(pioneerService.createMove("offer", null, null, null, null)).thenReturn(Observable.just(move));
        clickOn("#declineButton");
        verify(pioneerService).createMove("offer", null, null, null, null);
    }
}
