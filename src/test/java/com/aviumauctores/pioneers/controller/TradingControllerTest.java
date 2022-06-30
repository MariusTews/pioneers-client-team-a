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
import org.testfx.service.query.NodeQuery;

import java.util.*;

import static com.aviumauctores.pioneers.Constants.RESOURCE_LUMBER;
import static com.aviumauctores.pioneers.Constants.RESOURCE_WOOL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TradingControllerTest extends ApplicationTest {
    @Mock
    InGameController inGameController;


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
        clickOn("#bankWood");
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
        Spinner<Integer> spinnerTradeWood = lookup("#tradeWood").query();
        spinnerTradeWood.increment();
        spinnerRequestWool.increment();
        spinnerRequestWool.increment();
        clickOn("#tradeButton");

        verify(pioneerService).createMove("build", null, resources, null, null);
    }



    @Test
    void testBankTradeWood() {
        clickOn("#bankWood");
        verifyThat("#tradeWool", NodeMatchers.isDisabled());
        verifyThat("#tradeClay", NodeMatchers.isDisabled());
        verifyThat("#tradeStone", NodeMatchers.isDisabled());
        verifyThat("#tradeBread", NodeMatchers.isDisabled());
        verifyThat("#requestWood", NodeMatchers.isDisabled());
        Spinner<Integer> spinnerTradeWood = lookup("#tradeWood").query();
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
        verifyThat("#tradeWood", NodeMatchers.isDisabled());
        verifyThat("#tradeClay", NodeMatchers.isDisabled());
        verifyThat("#tradeStone", NodeMatchers.isDisabled());
        verifyThat("#tradeBread", NodeMatchers.isDisabled());
        verifyThat("#requestWool", NodeMatchers.isDisabled());
        Spinner<Integer> spinnerTradeWool = lookup("#tradeWool").query();
        Spinner<Integer> spinnerRequestClay = lookup("#requestClay").query();

        spinnerRequestClay.increment();
        assertEquals(spinnerTradeWool.getValue(), 4);

        spinnerRequestClay.increment();
        assertEquals(spinnerTradeWool.getValue(), 8);

        spinnerRequestClay.decrement();
        assertEquals(spinnerTradeWool.getValue(), 4);
    }

    @Test
    void testBankTradeClay() {
        clickOn("#bankClay");
        verifyThat("#tradeWood", NodeMatchers.isDisabled());
        verifyThat("#tradeWool", NodeMatchers.isDisabled());
        verifyThat("#tradeStone", NodeMatchers.isDisabled());
        verifyThat("#tradeBread", NodeMatchers.isDisabled());
        verifyThat("#requestClay", NodeMatchers.isDisabled());
        Spinner<Integer> spinnerTradeClay = lookup("#tradeClay").query();
        Spinner<Integer> spinnerRequestWood = lookup("#requestWood").query();

        spinnerRequestWood.increment();
        assertEquals(spinnerTradeClay.getValue(), 4);

        spinnerRequestWood.increment();
        assertEquals(spinnerTradeClay.getValue(), 8);

        spinnerRequestWood.decrement();
        assertEquals(spinnerTradeClay.getValue(), 4);
    }

    @Test
    void testBankTradeStone() {
        clickOn("#bankStone");
        verifyThat("#tradeWood", NodeMatchers.isDisabled());
        verifyThat("#tradeClay", NodeMatchers.isDisabled());
        verifyThat("#tradeWool", NodeMatchers.isDisabled());
        verifyThat("#tradeBread", NodeMatchers.isDisabled());
        verifyThat("#requestStone", NodeMatchers.isDisabled());
        Spinner<Integer> spinnerTradeStone = lookup("#tradeStone").query();
        Spinner<Integer> spinnerRequestBread= lookup("#requestBread").query();

        spinnerRequestBread.increment();
        assertEquals(spinnerTradeStone.getValue(), 4);

        spinnerRequestBread.increment();
        assertEquals(spinnerTradeStone.getValue(), 8);

        spinnerRequestBread.decrement();
        assertEquals(spinnerTradeStone.getValue(), 4);
    }

    @Test
    void testBankTradeBread() {
        clickOn("#bankBread");
        verifyThat("#tradeWood", NodeMatchers.isDisabled());
        verifyThat("#tradeClay", NodeMatchers.isDisabled());
        verifyThat("#tradeStone", NodeMatchers.isDisabled());
        verifyThat("#tradeWool", NodeMatchers.isDisabled());
        verifyThat("#requestBread", NodeMatchers.isDisabled());
        Spinner<Integer> spinnerTradeBread = lookup("#tradeBread").query();
        Spinner<Integer> spinnerRequestStone = lookup("#requestStone").query();

        spinnerRequestStone.increment();
        assertEquals(spinnerTradeBread.getValue(), 4);

        spinnerRequestStone.increment();
        assertEquals(spinnerTradeBread.getValue(), 8);

        spinnerRequestStone.decrement();
        assertEquals(spinnerTradeBread.getValue(), 4);
    }

}