package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.achievements.CreateAchievementDto;
import com.aviumauctores.pioneers.model.Achievement;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.List;

import static com.aviumauctores.pioneers.Constants.*;

public interface AchievementsApiService {

    @GET(USER_ACHIEVEMENTS_URL)
    Observable<List<Achievement>> listUserAchievements(@Path(PATH_USER_ID) String id);

    @GET(USER_ACHIEVEMENTS_BY_ID_URL)
    Observable<List<Achievement>> getUserAchievement(
            @Path(PATH_USER_ID) String userId,
            @Path(PATH_ID) String id
    );

    @PUT(USER_ACHIEVEMENTS_BY_ID_URL)
    Observable<Achievement> putAchievement(
            @Path(PATH_USER_ID) String userId,
            @Path(PATH_ID) String id,
            @Body CreateAchievementDto createAchievementDto
    );

}
