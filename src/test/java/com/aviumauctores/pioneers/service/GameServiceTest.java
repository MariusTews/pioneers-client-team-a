package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.games.CreateGameDto;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.GameSettings;
import com.aviumauctores.pioneers.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    GamesApiService gamesApiService;

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
    }

    @Test
    void listGames() {
    }

    @Test
    void getCurrentGame() {
    }

    @Test
    void deleteGame() {
    }

    @Test
    void updateGame() {
    }

    @Test
    void startGame() {
    }

    @Test
    void setUpdateOption() {
    }
}