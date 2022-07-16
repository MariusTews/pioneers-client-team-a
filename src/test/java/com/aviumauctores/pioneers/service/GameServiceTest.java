package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.gamemembers.CreateMemberDto;
import com.aviumauctores.pioneers.dto.games.CreateGameDto;
import com.aviumauctores.pioneers.dto.games.UpdateGameDto;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.GameSettings;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.rest.GameMembersApiService;
import com.aviumauctores.pioneers.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    GamesApiService gamesApiService;

    @Mock
    GameMembersApiService gameMembersApiService;

    @InjectMocks
    GameService gameService;


    @Test
    void create() {
        when(gamesApiService.createGame(new CreateGameDto("name", false, new GameSettings(2, 10), "password")))
                .thenReturn(Observable.just(new Game("1", "2", "newGame", "game", "me", false, 1, new GameSettings(2, 10))));

        String check = gameService.create("name", "password").blockingFirst();
        assertEquals(check, "newGame");

        verify(gamesApiService).createGame(new CreateGameDto("name", false, new GameSettings(2, 10), "password"));
    }

    @Test
    void joinGame() {
        when(gameMembersApiService.createMember(null, new CreateMemberDto(false, null, "password", false)))
                .thenReturn(Observable.just(new Member("1", "2", null, "user1", false, null, false)));
        Member check = gameService.joinGame("password").blockingFirst();
        assertEquals(check, new Member("1", "2", null, "user1", false, null, false));

        verify(gameMembersApiService).createMember(null, new CreateMemberDto(false, null, "password", false));
    }

    @Test
    void listGames() {
        when(gamesApiService.listGames()).thenReturn(Observable.just(List.of(new Game("1", "2", "123", "game2", "me", false, 1, null))));
        List<Game> check = new ArrayList<>();
        check.add(new Game("1", "2", "123", "game2", "me", false, 1, null));

        List<Game> result = gameService.listGames().blockingFirst();
        assertEquals(result, check);

        verify(gamesApiService).listGames();
    }

    @Test
    void getCurrentGame() {
        when(gamesApiService.getGame(null)).thenReturn(Observable.just(new Game("1", "2", "123", "game2", "me", false, 1, null)));

        Game check = gameService.getCurrentGame().blockingFirst();
        assertEquals(check, new Game("1", "2", "123", "game2", "me", false, 1, null));

        verify(gamesApiService).getGame(null);
    }

    @Test
    void deleteGame() {
        when(gamesApiService.deleteGame(null)).thenReturn(Observable.just(new Game("1", "2", "123", "game2", "me", false, 1, null)));

        Game check = gameService.deleteGame().blockingFirst();
        assertEquals(check, new Game("1", "2", "123", "game2", "me", false, 1, null));

        verify(gamesApiService).deleteGame(null);

    }

    @Test
    void updateGame() {
        when(gamesApiService.updateGame(null, new UpdateGameDto(null, null, false, null, null)))
                .thenReturn(Observable.just(new Game("1", "2", "123", null, null, false, 1, null)));

        Game check = gameService.updateGame(false).blockingFirst();
        assertEquals(check, new Game("1", "2", "123", null, null, false, 1, null));

        verify(gamesApiService).updateGame(null, new UpdateGameDto(null, null, false, null, null));

    }

    @Test
    void startGame() {
        when(gamesApiService.updateGame(null, new UpdateGameDto(null, null, true, null, null)))
                .thenReturn(Observable.just(new Game("1", "2", "123", null, null, true, 1, null)));

        Game check = gameService.startGame().blockingFirst();
        assertEquals(check, new Game("1", "2", "123", null, null, true, 1, null));

        verify(gamesApiService).updateGame(null, new UpdateGameDto(null, null, true, null, null));

    }

    @Test
    void setUpdateOption() {
        when(gamesApiService.updateGame(null, new UpdateGameDto(null, null, false, new GameSettings(5, 12), null)))
                .thenReturn(Observable.just(new Game("1", "2", "123", null, null, true, 1, new GameSettings(5, 12))));

        Game check = gameService.setUpdateOption(5, 12).blockingFirst();
        assertEquals(check, new Game("1", "2", "123", null, null, true, 1, new GameSettings(5, 12)));

        verify(gamesApiService).updateGame(null, new UpdateGameDto(null, null, false, new GameSettings(5, 12), null));
    }
}