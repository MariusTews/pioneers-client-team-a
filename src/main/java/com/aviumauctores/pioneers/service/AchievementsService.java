package com.aviumauctores.pioneers.service;

import static com.aviumauctores.pioneers.Constants.*;

import com.aviumauctores.pioneers.dto.achievements.CreateAchievementDto;
import com.aviumauctores.pioneers.dto.achievements.UpdateAchievementDto;
import com.aviumauctores.pioneers.model.Achievement;
import com.aviumauctores.pioneers.rest.AchievementsApiService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AchievementsService {

    protected CompositeDisposable disposables;

    private final AchievementsApiService achievementsApiService;

    private final UserService userService;

    private HashMap<String, Integer> achievementsProgress = new HashMap<>();

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private
    Calendar calender = Calendar.getInstance();

    @Inject
    public AchievementsService(AchievementsApiService achievementsApiService, UserService userService) {
        this.achievementsApiService = achievementsApiService;
        this.userService = userService;
    }

    public Observable<List<Achievement>> getUserAchievements() {
        Observable<List<Achievement>> achievementList = achievementsApiService.listUserAchievements(userService.getCurrentUserID());
        achievementList.observeOn(FX_SCHEDULER)
                .subscribe(achievements -> {
                    for (Achievement achievement : achievements) {
                        int achievementProgress = 0;
                        if (achievementProgress < achievement.progress()) {
                            achievementProgress = achievement.progress();
                        }
                        achievementsProgress.put(
                                achievement.id(),
                                achievementProgress
                        );
                    }
                });
        return achievementList;
    }

    public Observable<Achievement> putAchievement(String id, int progress) {
        String unlocked = null;
        if (achievementsProgress.containsKey(id)) {
            if (!id.equals(ACHIEVEMENT_RESOURCES)) {
                progress += achievementsProgress.get(id);
            }
            if (achievementsProgress.get(id) < ACHIEVEMENT_UNLOCK_VALUES.get(id) && progress >= ACHIEVEMENT_UNLOCK_VALUES.get(id)) {
                unlocked = dateFormat.format(calender.getTime());
                disposables.add(putAchievement(ACHIEVEMENT_ALL, 1).observeOn(FX_SCHEDULER).subscribe());
            }
            achievementsProgress.replace(id, progress);
        }
        return achievementsApiService.putAchievement(
                userService.getCurrentUserID(),
                id,
                new CreateAchievementDto(unlocked, progress));
    }

    public Observable<Achievement> updateAchievement(String id, int progress) {
        String unlocked = null;
        if (achievementsProgress.get(id) != null) {
            progress += achievementsProgress.get(id);
        }
        if (progress >= ACHIEVEMENT_UNLOCK_VALUES.get(id)) {
            unlocked = dateFormat.format(calender.getTime());
        }
        return achievementsApiService.updateAchievement(
                userService.getCurrentUserID(),
                id,
                new UpdateAchievementDto(unlocked, progress)
        );
    }
}
