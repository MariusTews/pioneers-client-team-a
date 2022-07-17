package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.pioneers.CreateMoveDto;
import com.aviumauctores.pioneers.dto.players.UpdatePlayerDto;
import com.aviumauctores.pioneers.model.*;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

import static com.aviumauctores.pioneers.Constants.*;

public interface PioneersApiService {

    @GET(LIST_BUILDINGS_URL)
    Observable<List<Building>> listBuildings(@Path (PATH_GAME_ID) String gameId);

    @GET(GET_BUILDINGID_URL)
    Observable<Building> getBuilding(
            @Path(PATH_GAME_ID) String gameId, @Path(PATH_BUILDING_ID) String buildingId
    );

    @GET(GET_MAP_URL)
    Observable<Map> getMap(@Path(PATH_GAME_ID) String id);

    @GET(LIST_PLAYERS_URL)
    Observable<List<Player>> listMembers(@Path(PATH_GAME_ID) String gameId);

    @GET(GET_USERID_URL)
    Observable<Player> getPlayer(
            @Path(PATH_GAME_ID) String gameId, @Path(PATH_USER_ID) String userId
    );

    @GET(GET_STATE_URL)
    Observable<State> getState (@Path(PATH_GAME_ID) String gameId);

    @POST(CREATE_MOVE_URL)
    Observable<Move> createMove(@Path(PATH_GAME_ID) String gameId, @Body CreateMoveDto createMoveDto);

    @GET(GET_MOVES_URL)
    Observable<List<Move>> getMoves(@Path(PATH_GAME_ID) String gameId, @Query(QUERY_USERID) String userId);

    @GET(GET_MOVEID_URL)
    Observable<Move> getMoveId(@Path(PATH_GAME_ID) String gameId, @Path(PATH_MOVEID) String moveId);

    @PATCH(GET_USERID_URL)
    Observable<Player> updatePlayer(
            @Path(PATH_GAME_ID) String gameId, @Path(PATH_USER_ID) String userId,
            @Body UpdatePlayerDto updatePlayerDto
    );
























}
