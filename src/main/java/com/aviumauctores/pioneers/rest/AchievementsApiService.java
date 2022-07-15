package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.achievements.CreateAchievementDto;
import com.aviumauctores.pioneers.dto.achievements.UpdateAchievementDto;
import com.aviumauctores.pioneers.model.Achievement;
import com.aviumauctores.pioneers.model.AchievementSummary;
import dagger.Provides;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

import static com.aviumauctores.pioneers.Constants.*;

public interface AchievementsApiService {
    @GET(ACHIEVEMENTS_URL)
    Observable<List<AchievementSummary>> listAchievements();

    @GET(ACHIEVEMENTS_BY_ID_URL)
    Observable<AchievementSummary> getAchievement(@Path(PATH_ID) String id);

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

    @PATCH(USER_ACHIEVEMENTS_BY_ID_URL)
    Observable<Achievement> updateAchievement(
            @Path(PATH_USER_ID) String userId,
            @Path(PATH_ID) String id,
            @Body UpdateAchievementDto updateAchievementDto
    );

    @DELETE(USER_ACHIEVEMENTS_BY_ID_URL)
    Observable<Achievement> deleteAchievement(
            @Path(PATH_USER_ID) String userId,
            @Path(PATH_ID) String id
    );
}
