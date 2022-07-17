package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.pioneers.CreateMoveDto;
import com.aviumauctores.pioneers.model.*;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

import static com.aviumauctores.pioneers.Constants.*;

public interface PioneersApiService {

    @GET(LIST_BUILDINGS_URL)
    Observable<List<Building>> listBuildings(@Path (PATH_GAME_ID) String gameId);

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


}
