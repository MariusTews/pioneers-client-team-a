package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.model.Move;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.*;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.util.*;

import static com.aviumauctores.pioneers.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
public class TradingControllerCounterproposalTest extends ApplicationTest {
    HashMap<String, Integer> myResources = new HashMap<>();

    {
        myResources.put(RESOURCE_LUMBER, 100);
        myResources.put(RESOURCE_GRAIN, 100);
        myResources.put(RESOURCE_WOOL, 100);
        myResources.put(RESOURCE_BRICK, 100);
        myResources.put(RESOURCE_ORE, 100);
    }

    @Spy
    HashMap<String, Integer> resourceRatio = new HashMap<>();

    {
        resourceRatio.put(RESOURCE_LUMBER, 4);
        resourceRatio.put(RESOURCE_GRAIN, 3);
        resourceRatio.put(RESOURCE_WOOL, 2);
        resourceRatio.put(RESOURCE_BRICK, 4);
        resourceRatio.put(RESOURCE_ORE, 4);
    }

    @Spy
    HashMap<String, Integer> sendResources = new HashMap<>();

    {
        sendResources.put(RESOURCE_LUMBER, -1);
        sendResources.put(RESOURCE_GRAIN, 2);
        sendResources.put(RESOURCE_WOOL, 1);
        sendResources.put(RESOURCE_BRICK, -1);
        sendResources.put(RESOURCE_ORE, 0);
    }

    @Mock
    InGameController inGameController;

    @Spy
    Player player = new Player("1", "2", "#111111", true, 2, myResources, null, 2, 0);


    @Spy
    TradeService tradeService;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @Mock
    UserService userService;

    @Mock
    GameService gameService;

    @Mock
    PioneerService pioneerService;

    @Mock
    ColorService colorService;

    @Mock
    ErrorService errorService;

    @Mock
    App app;

    @InjectMocks
    TradingController tradingController;

    @Override
    public void start(Stage stage) {
        Player player1 = new Player(null, null, "#008000", true, 1, null, null, 2, 2);
        Player player2 = new Player(null, null, "#008000", true, 1, null, null, 2, 2);
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        when(userService.getCurrentUserID()).thenReturn("1");
        when(pioneerService.listPlayers()).thenReturn(io.reactivex.rxjava3.core.Observable.just(players));
        when(userService.getUserName(any())).thenReturn(io.reactivex.rxjava3.core.Observable.just("User"));
        when(userService.getUserByID(any())).thenReturn(Observable.just(new User("1", "name", null, null, null)));
        new App(tradingController).start(stage);
    }

    @Override
    public void stop() {
        this.inGameController = null;
        this.bundle = null;
        this.pioneerService = null;
        this.userService = null;
        this.colorService = null;
        this.tradingController = null;
    }


    @Test
    void testHandleRequestNoCounterproposal() {
        HashMap<String, Integer> resources = new HashMap<>();
        {
            resources.put(RESOURCE_LUMBER, 1);
            resources.put(RESOURCE_GRAIN, -2);
            resources.put(RESOURCE_WOOL, -1);
            resources.put(RESOURCE_BRICK, 1);
            resources.put(RESOURCE_ORE, 0);
        }
        when(pioneerService.createMove("accept", null, null, "partner", null))
                .thenReturn(Observable.just(new Move("1", "2", "3", "me", "accept", 1, null, null, null, "partner")));
        Platform.runLater(() -> tradingController.handleRequest(resources, "partner"));
        WaitForAsyncUtils.waitForFxEvents();

        verifyThat("#cancelTradeButton", NodeMatchers.isEnabled());

        verify(pioneerService).createMove("accept", null, null, "partner", null);


    }

    @Test
    void testHandleRequestCounterproposalAccepted() {
        HashMap<String, Integer> resources = new HashMap<>();
        {
            resources.put(RESOURCE_LUMBER, -1);
            resources.put(RESOURCE_GRAIN, 2);
            resources.put(RESOURCE_WOOL, 1);
            resources.put(RESOURCE_BRICK, -1);
            resources.put(RESOURCE_ORE, -1);
        }
        when(pioneerService.createMove("accept", null, null, "partner", null))
                .thenReturn(Observable.just(new Move("1", "2", "3", "me", "accept", 1, null, null, null, "partner")));

        Platform.runLater(() -> tradingController.handleRequest(resources, "partner"));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(((Button) lookup("#tradeButton").query()).getText(), bundle.getString("accept"));
        verifyThat("#tradeButton", NodeMatchers.isEnabled());
        verifyThat("#cancelTradeButton", NodeMatchers.isEnabled());

        clickOn("#tradeButton");
        assertEquals(((Button) lookup("#tradeButton").query()).getText(), bundle.getString("trading"));

    }

    @Test
    void testHandleRequestCounterproposalDeclined() {
        HashMap<String, Integer> resources = new HashMap<>();
        {
            resources.put(RESOURCE_LUMBER, -1);
            resources.put(RESOURCE_GRAIN, 2);
            resources.put(RESOURCE_WOOL, 1);
            resources.put(RESOURCE_BRICK, -1);
            resources.put(RESOURCE_ORE, -1);
        }
        when(pioneerService.createMove("accept", null, null, null, null))
                .thenReturn(Observable.just(new Move("1", "2", "3", "me", "accept", 1, null, null, null, "partner")));

        Platform.runLater(() -> tradingController.handleRequest(resources, "partner"));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(((Button) lookup("#tradeButton").query()).getText(), bundle.getString("accept"));
        verifyThat("#tradeButton", NodeMatchers.isEnabled());
        verifyThat("#cancelTradeButton", NodeMatchers.isEnabled());

        clickOn("#cancelTradeButton");
        assertEquals(((Button) lookup("#tradeButton").query()).getText(), bundle.getString("trading"));
    }
}
