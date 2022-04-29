package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.games.CreateGameDto;
import com.aviumauctores.pioneers.dto.games.UpdateGameDto;
import com.aviumauctores.pioneers.model.Game;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

import static com.aviumauctores.pioneers.Constants.*;

public interface GamesApiService {
    @GET(LIST_GAMES_URL)
    Observable<List<Game>> listGames();

    @POST(CREATE_GAME_URL)
    Observable<Game> createGame(@Body CreateGameDto createGameDto);

    @GET(GET_GAME_URL)
    Observable<Game> getGame(@Path(PATH_ID) String id);

    @PATCH(UPDATE_GAME_URL)
    Observable<Game> updateGame(@Path(PATH_ID) String id, @Body UpdateGameDto updateGameDto);

    @DELETE(DELETE_GAME_URL)
    Observable<Game> deleteGame(@Path(PATH_ID) String id);
}
