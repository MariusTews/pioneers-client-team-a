package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.games.CreateGameDto;
import com.aviumauctores.pioneers.dto.games.UpdateGameDto;
import com.aviumauctores.pioneers.model.Game;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface GamesApiService {
    @GET("games")
    Call<List<Game>> listGames(@Header("Authorization") String authToken);

    @POST("games")
    Call<Game> createGame(@Header("Authorization") String authToken, @Body CreateGameDto createGameDto);

    @GET("games/{id}")
    Call<Game> getGame(@Header("Authorization") String authToken, @Path("id") String id);

    @PATCH("games/{id}")
    Call<Game> updateGame(@Header("Authorization") String authToken, @Path("id") String id, @Body UpdateGameDto updateGameDto);

    @DELETE("games/{id}")
    Call<Game> deleteGame(@Header("Authorization") String authToken, @Path("id") String id);
}
