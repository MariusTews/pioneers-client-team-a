package com.aviumauctores.pioneers.service;

import static com.aviumauctores.pioneers.Constants.*;

import com.aviumauctores.pioneers.dto.achievements.CreateAchievementDto;
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

    private final HashMap<String, Integer> achievementsProgress = new HashMap<>();

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private final Calendar calender = Calendar.getInstance();

    @Inject
    public AchievementsService(AchievementsApiService achievementsApiService, UserService userService) {
        this.achievementsApiService = achievementsApiService;
        this.userService = userService;
    }

    public void init(){
        disposables = new CompositeDisposable();
    }

    public Observable<List<Achievement>> getUserAchievements() {
        Observable<List<Achievement>> achievementList = achievementsApiService.listUserAchievements(userService.getCurrentUserID());
        disposables.add(achievementList
                .observeOn(FX_SCHEDULER)
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
                }));
        return achievementList;
    }

    public void dispose(){
        if (disposables != null) {
            disposables.dispose();
        }
    }

    public Observable<Achievement> putAchievement(String id, int progress) {
        String unlocked = null;
        if (achievementsProgress.containsKey(id)) {
            if (!id.equals(ACHIEVEMENT_RESOURCES)) {
                progress += achievementsProgress.get(id);
            } else if (progress < achievementsProgress.get(id)) {
                progress = achievementsProgress.get(id);
            }
            if (achievementsProgress.get(id) < ACHIEVEMENT_UNLOCK_VALUES.get(id) && progress >= ACHIEVEMENT_UNLOCK_VALUES.get(id)) {
                unlocked = dateFormat.format(calender.getTime());
                achievementsProgress.replace(id, progress);
                putAchievement(ACHIEVEMENT_ALL, 1).observeOn(FX_SCHEDULER).subscribe();
            }
            achievementsProgress.replace(id, progress);
        }
        return achievementsApiService.putAchievement(
                userService.getCurrentUserID(),
                id,
                new CreateAchievementDto(unlocked, progress));
    }
}
