package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.games.CreateGameDto;
import com.aviumauctores.pioneers.dto.games.UpdateGameDto;
import com.aviumauctores.pioneers.model.Game;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

import static com.aviumauctores.pioneers.Constants.*;

public interface GamesApiService {
    @GET(LIST_GAMES_URL)
    Call<List<Game>> listGames();

    @POST(CREATE_GAME_URL)
    Call<Game> createGame(@Body CreateGameDto createGameDto);

    @GET(GET_GAME_URL)
    Call<Game> getGame(@Path(PATH_ID) String id);

    @PATCH(UPDATE_GAME_URL)
    Call<Game> updateGame(@Path(PATH_ID) String id, @Body UpdateGameDto updateGameDto);

    @DELETE(DELETE_GAME_URL)
    Call<Game> deleteGame(@Path(PATH_ID) String id);
}
