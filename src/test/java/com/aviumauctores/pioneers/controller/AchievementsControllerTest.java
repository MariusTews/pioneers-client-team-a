package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.model.Achievement;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.*;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AchievementsControllerTest extends ApplicationTest {

    @Mock
    App app;

    @Mock
    UserService userService;

    @Mock
    LoginService loginService;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle ("com/aviumauctores/pioneers/lang", Locale.ROOT);

    @Mock
    AchievementsService achievementsService;


    @Mock
    EventListener eventListener;

    @InjectMocks
    AchievementsController achievementsController;

    @Mock
    Provider<LobbyController> lobbyControllerProvider;

    @Override
    public void start(Stage stage) throws Exception {
        when(userService.getCurrentUserID()).thenReturn("12");
        when(userService.getUserName("12")).thenReturn(Observable.just("Player"));
        when(achievementsService.listUserAchievements()).thenReturn(Observable.just(List.of(new Achievement("120","122","12",RANKING,null,120))));
        when(achievementsService.getUserAchievement("12",RANKING)).thenReturn(Observable.just(List.of(new Achievement("120","122","12",RANKING,null,120))));
        new App (achievementsController).start (stage);
    }

    @Test
    public void readyAchievement(){
        final Label rankingLabel = lookup("#rankingPointsLabel").query();
        assertThat(rankingLabel).hasText("120"+" "+bundle.getString("ranking.points"));
    }

    @Override
    public void stop() {
        this.bundle = null;
        this.userService = null;
        this.achievementsService=null;
    }


}
