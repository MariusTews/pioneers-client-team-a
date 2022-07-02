package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.rob.RobDto;
import com.aviumauctores.pioneers.model.Move;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.*;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import java.util.*;

import static com.aviumauctores.pioneers.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TradingControllerTest extends ApplicationTest {

    HashMap<String, Integer> myRessources = new HashMap<>();

    {
        myRessources.put(RESOURCE_LUMBER, 100);
        myRessources.put(RESOURCE_GRAIN, 100);
        myRessources.put(RESOURCE_WOOL, 100);
        myRessources.put(RESOURCE_BRICK, 100);
        myRessources.put(RESOURCE_ORE, 100);
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

    @Mock
    InGameController inGameController;

    @Spy
    Player player = new Player("1", "2", "#111111", true, 2, myRessources, null, 2, 0);



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
        when(pioneerService.listPlayers()).thenReturn(Observable.just(players));
        when(userService.getUserName(any())).thenReturn(Observable.just("User"));
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
    void testBankTrade() {
        HashMap<String, Integer> resources = new HashMap<>();
        resources.put(RESOURCE_LUMBER, -8);
        resources.put(RESOURCE_WOOL, 2);
        Move move = new Move("1", "2", "3", "4", "5", 0, "6", new RobDto(1,2,3, "4"), resources, "7");
        when(pioneerService.createMove("build", null, resources, "684072366f72202b72406465", null)).thenReturn(Observable.just(move));
        clickOn("#bankLumber");
        Spinner<Integer> spinnerRequestWool = lookup("#requestWool").query();
        spinnerRequestWool.increment();
        spinnerRequestWool.increment();
        clickOn("#tradeButton");
        verify(pioneerService).createMove("build", null, resources, "684072366f72202b72406465", null);

    }

    @Test
    void testPrivateTrade() {
        clickOn("#requestList");
        HashMap<String, Integer> resources = new HashMap<>();
        resources.put(RESOURCE_LUMBER, -1);
        resources.put(RESOURCE_WOOL, 2);
        Move move = new Move("1", "2", "3", "4", "5", 0, "6", new RobDto(1,2,3, "4"), resources, "7");
        when(pioneerService.createMove("build", null, resources, null, null)).thenReturn(Observable.just(move));

        Spinner<Integer> spinnerRequestWool = lookup("#requestWool").query();
        Spinner<Integer> spinnerTradeLumber = lookup("#tradeLumber").query();
        spinnerTradeLumber.increment();
        spinnerRequestWool.increment();
        spinnerRequestWool.increment();
        clickOn("#tradeButton");

        verify(pioneerService).createMove("build", null, resources, null, null);
    }



    @Test
    void testBankTradeLumber() {
        clickOn("#bankLumber");
        verifyThat("#tradeWool", NodeMatchers.isDisabled());
        verifyThat("#tradeBrick", NodeMatchers.isDisabled());
        verifyThat("#tradeOre", NodeMatchers.isDisabled());
        verifyThat("#tradeGrain", NodeMatchers.isDisabled());
        verifyThat("#requestLumber", NodeMatchers.isDisabled());
        Spinner<Integer> spinnerTradeWood = lookup("#tradeLumber").query();
        Spinner<Integer> spinnerRequestWool = lookup("#requestWool").query();

        spinnerRequestWool.increment();
        assertEquals(spinnerTradeWood.getValue(), 4);

        spinnerRequestWool.increment();
        assertEquals(spinnerTradeWood.getValue(), 8);

        spinnerRequestWool.decrement();
        assertEquals(spinnerTradeWood.getValue(), 4);
    }

    @Test
    void testBankTradeWool() {
        clickOn("#bankWool");
        verifyThat("#tradeLumber", NodeMatchers.isDisabled());
        verifyThat("#tradeBrick", NodeMatchers.isDisabled());
        verifyThat("#tradeOre", NodeMatchers.isDisabled());
        verifyThat("#tradeGrain", NodeMatchers.isDisabled());
        verifyThat("#requestWool", NodeMatchers.isDisabled());
        Spinner<Integer> spinnerTradeWool = lookup("#tradeWool").query();
        Spinner<Integer> spinnerRequestClay = lookup("#requestBrick").query();

        spinnerRequestClay.increment();
        assertEquals(spinnerTradeWool.getValue(), 2);

        spinnerRequestClay.increment();
        assertEquals(spinnerTradeWool.getValue(), 4);

        spinnerRequestClay.decrement();
        assertEquals(spinnerTradeWool.getValue(), 2);
    }

    @Test
    void testBankTradeBrick() {
        clickOn("#bankBrick");
        verifyThat("#tradeLumber", NodeMatchers.isDisabled());
        verifyThat("#tradeWool", NodeMatchers.isDisabled());
        verifyThat("#tradeOre", NodeMatchers.isDisabled());
        verifyThat("#tradeGrain", NodeMatchers.isDisabled());
        verifyThat("#requestBrick", NodeMatchers.isDisabled());
        Spinner<Integer> spinnerTradeClay = lookup("#tradeBrick").query();
        Spinner<Integer> spinnerRequestWood = lookup("#requestLumber").query();

        spinnerRequestWood.increment();
        assertEquals(spinnerTradeClay.getValue(), 4);

        spinnerRequestWood.increment();
        assertEquals(spinnerTradeClay.getValue(), 8);

        spinnerRequestWood.decrement();
        assertEquals(spinnerTradeClay.getValue(), 4);
    }

    @Test
    void testBankTradeOre() {
        clickOn("#bankOre");
        verifyThat("#tradeLumber", NodeMatchers.isDisabled());
        verifyThat("#tradeBrick", NodeMatchers.isDisabled());
        verifyThat("#tradeWool", NodeMatchers.isDisabled());
        verifyThat("#tradeGrain", NodeMatchers.isDisabled());
        verifyThat("#requestOre", NodeMatchers.isDisabled());
        Spinner<Integer> spinnerTradeStone = lookup("#tradeOre").query();
        Spinner<Integer> spinnerRequestBread= lookup("#requestGrain").query();

        spinnerRequestBread.increment();
        assertEquals(spinnerTradeStone.getValue(), 4);

        spinnerRequestBread.increment();
        assertEquals(spinnerTradeStone.getValue(), 8);

        spinnerRequestBread.decrement();
        assertEquals(spinnerTradeStone.getValue(), 4);
    }

    @Test
    void testBankTradeGrain() {
        clickOn("#bankGrain");
        verifyThat("#tradeLumber", NodeMatchers.isDisabled());
        verifyThat("#tradeBrick", NodeMatchers.isDisabled());
        verifyThat("#tradeOre", NodeMatchers.isDisabled());
        verifyThat("#tradeWool", NodeMatchers.isDisabled());
        verifyThat("#requestGrain", NodeMatchers.isDisabled());
        Spinner<Integer> spinnerTradeBread = lookup("#tradeGrain").query();
        Spinner<Integer> spinnerRequestStone = lookup("#requestOre").query();

        spinnerRequestStone.increment();
        assertEquals(spinnerTradeBread.getValue(), 3);

        spinnerRequestStone.increment();
        assertEquals(spinnerTradeBread.getValue(), 6);

        spinnerRequestStone.decrement();
        assertEquals(spinnerTradeBread.getValue(), 3);
    }

}