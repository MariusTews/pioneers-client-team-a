package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.achievements.CreateAchievementDto;
import com.aviumauctores.pioneers.dto.achievements.UpdateAchievementDto;
import com.aviumauctores.pioneers.model.Achievement;
import com.aviumauctores.pioneers.rest.AchievementsApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class AchievementsService {

    private final AchievementsApiService achievementsApiService;

    private final UserService userService;

    @Inject
    public AchievementsService(AchievementsApiService achievementsApiService, UserService userService) {
        this.achievementsApiService = achievementsApiService;
        this.userService = userService;
    }

    public Observable<List<Achievement>> getUserAchievements() {
        return achievementsApiService.listUserAchievements(userService.getCurrentUserID());
    }

    public Observable<Achievement> putAchievement(String id, String unlocked, int progress) {
        return achievementsApiService.putAchievement(
                userService.getCurrentUserID(),
                id,
                new CreateAchievementDto(unlocked, progress));
    }

    public Observable<Achievement> updateAchievement(String id, String unlocked, int progress) {
        return achievementsApiService.updateAchievement(
                userService.getCurrentUserID(),
                id,
                new UpdateAchievementDto(unlocked, progress)
        );
    }
}
