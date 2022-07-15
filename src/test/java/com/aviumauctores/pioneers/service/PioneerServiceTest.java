package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.pioneers.CreateMoveDto;
import com.aviumauctores.pioneers.model.*;
import com.aviumauctores.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PioneerServiceTest {

    @Mock
    PioneersApiService pioneersApiService;

    @Mock
    GameService gameService;

    @InjectMocks
    PioneerService pioneerService;

    @Test
    void getState() {
        when(gameService.getCurrentGameID()).thenReturn("game1");
        when(pioneersApiService.getState("game1")).thenReturn(Observable.just(new State("1", "game1", null, null)));

        // check if states are equal
        State check = new State("1", "game1", null, null);
        State result = pioneerService.getState().blockingFirst();

        assertEquals(check, result);

        verify(pioneersApiService).getState("game1");
    }

    @Test
    void createMove() {
        when(gameService.getCurrentGameID()).thenReturn("game1");
        when(pioneersApiService.createMove("game1", new CreateMoveDto("action", null, null, "partner", null))).thenReturn(
                Observable.just(new Move("1", "2", "game1", "3", "action", 1, null, null, null, "partner")));

        // check if moves are equal
        Move check = new Move("1", "2", "game1", "3", "action", 1, null, null, null, "partner");
        Move result = pioneerService.createMove("action", null, null, "partner", null).blockingFirst();

        assertEquals(check, result);
        verify(pioneersApiService).createMove("game1", new CreateMoveDto("action", null, null, "partner", null));
    }

    @Test
    void getPlayer() {
        when(gameService.getCurrentGameID()).thenReturn("game1");
        when(pioneersApiService.getPlayer("game1", "user1")).thenReturn(Observable.just(new Player("game1", "user1", null, true, 0, null, null, 0, 0)));

        // check if players are equal
        Player check = new Player("game1", "user1", null, true, 0, null, null, 0, 0);
        Player result = pioneerService.getPlayer("user1").blockingFirst();

        assertEquals(check, result);
        verify(pioneersApiService).getPlayer("game1", "user1");
    }

    @Test
    void listPlayers() {
        when(gameService.getCurrentGameID()).thenReturn("game1");
        Player player1 = new Player("game1", "user1", null, true, 0, null, null, 0, 0);
        Player player2 = new Player("game1", "user2", null, true, 0, null, null, 0, 0);
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        when(pioneersApiService.listMembers("game1")).thenReturn(Observable.just(players));

        // check if lists are equal
        List<Player> check = pioneerService.listPlayers().blockingFirst();
        assertEquals(check, players);

        verify(pioneersApiService).listMembers("game1");

    }

    @Test
    void listBuildings() {
        when(gameService.getCurrentGameID()).thenReturn("game1");
        Building building1 = new Building(0, 0, 0, 7, "id", "city", "game1", "me");
        Building building2 = new Building(0, 0, 1, 7, "id1", "road", "game1", "me");
        List<Building> buildings = new ArrayList<>();
        buildings.add(building1);
        buildings.add(building2);

        when(pioneersApiService.listBuildings("game1")).thenReturn(Observable.just(buildings));

        // check if lists are equal
        List<Building> check = pioneerService.listBuildings().blockingFirst();
        assertEquals(check, buildings);

        verify(pioneersApiService).listBuildings("game1");

    }

    @Test
    void getMap() {
        when(gameService.getCurrentGameID()).thenReturn("game1");
        when(pioneersApiService.getMap("game1")).thenReturn(Observable.just(new Map("game1", null, null)));

        // check if maps are equal
        Map check = new Map("game1", null, null);
        Map result = pioneerService.getMap().blockingFirst();
        assertEquals(check, result);

        verify(pioneersApiService).getMap("game1");
    }
}
