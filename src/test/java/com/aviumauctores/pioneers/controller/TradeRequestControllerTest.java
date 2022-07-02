package com.aviumauctores.pioneers.controller;


import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.rob.RobDto;
import com.aviumauctores.pioneers.model.Move;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.service.ErrorService;
import com.aviumauctores.pioneers.service.PioneerService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import static com.aviumauctores.pioneers.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TradeRequestControllerTest extends ApplicationTest {
    HashMap<String, Integer> myRessources = new HashMap<>();

    {
        myRessources.put(RESOURCE_LUMBER, 100);
        myRessources.put(RESOURCE_GRAIN, 100);
        myRessources.put(RESOURCE_WOOL, 100);
        myRessources.put(RESOURCE_BRICK, 100);
        myRessources.put(RESOURCE_ORE, 100);
    }

    HashMap<String, Integer> tradeRessourcesReverse = new HashMap<>();

    {
        tradeRessourcesReverse.put(RESOURCE_WOOL, -1);
        tradeRessourcesReverse.put(RESOURCE_LUMBER, 1);
        tradeRessourcesReverse.put(RESOURCE_ORE, -2);
        tradeRessourcesReverse.put(RESOURCE_BRICK, 2);
        tradeRessourcesReverse.put(RESOURCE_GRAIN, -3);
    }

    @Mock
    InGameController inGameController;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @Mock
    PioneerService pioneerService;

    @Mock
    ErrorService errorService;

    @Spy
    HashMap<String, Integer> tradeRessources = new HashMap<>();

    {
        tradeRessources.put(RESOURCE_WOOL, 1);
        tradeRessources.put(RESOURCE_LUMBER, -1);
        tradeRessources.put(RESOURCE_ORE, 2);
        tradeRessources.put(RESOURCE_BRICK, -2);
        tradeRessources.put(RESOURCE_GRAIN, 3);
    }

    @Spy
    Player player = new Player("1", "2", "#111111", true, 2, myRessources, null, 2, 0);


    @InjectMocks
    TradeRequestController tradeRequestController;

    @Override
    public void start(Stage stage) {
        new App(tradeRequestController).start(stage);
    }

    @Test
    void testAcceptButton() {
        Move move = new Move("1", "2", "3", "4", "5", 0, "6", new RobDto(1, 2, 3, "4"), null, "7");
        when(pioneerService.createMove("offer", null, tradeRessourcesReverse, null, null)).thenReturn(Observable.just(move));
        clickOn("#acceptButton");
        verify(pioneerService).createMove("offer", null, tradeRessourcesReverse, null, null);
    }

    @Test
    void testDeclineButton() {
        Move move = new Move("1", "2", "3", "4", "5", 0, "6", new RobDto(1, 2, 3, "4"), null, "7");
        when(pioneerService.createMove("offer", null, null, null, null)).thenReturn(Observable.just(move));
        clickOn("#declineButton");
        verify(pioneerService).createMove("offer", null, null, null, null);
    }

    @Test
    void testFillLabels() {
        assertEquals(((Label) lookup("#tradeWoolLabel").query()).getText(), "1");
        assertEquals(((Label) lookup("#getLumberLabel").query()).getText(), "1");
        assertEquals(((Label) lookup("#tradeOreLabel").query()).getText(), "2");
        assertEquals(((Label) lookup("#getBrickLabel").query()).getText(), "2");
        assertEquals(((Label) lookup("#tradeGrainLabel").query()).getText(), "3");
    }
}
