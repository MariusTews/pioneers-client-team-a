package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.model.Achievement;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.*;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AchievementsControllerTest extends ApplicationTest {

    @Mock
    App app;

    @Mock
    UserService userService;

    @Spy
    ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);

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
        when(achievementsService.listUserAchievements()).thenReturn(Observable.just(List.of(new Achievement("120", "122", "12", RANKING, null, 120))));
        when(achievementsService.getUserAchievement("12", RANKING)).thenReturn(Observable.just(List.of(new Achievement("120", "122", "12", RANKING, null, 120))));
        new App(achievementsController).start(stage);
    }

    @Override
    public void stop() {
        this.bundle = null;
        this.userService = null;
        this.achievementsService = null;
    }

    @Test
    public void readyAchievement() {
        final Label rankingLabel = lookup("#rankingPointsLabel").query();
        assertThat(rankingLabel).hasText("120" + " " + bundle.getString("ranking.points"));
    }

    @Test
    public void showFriends() {
        List<String> friendsList = new ArrayList<>();
        friendsList.add("13");
        friendsList.add("14");
        when(userService.getUserByID("12")).thenReturn(Observable.just(new User("12", "me", "false", null, friendsList)));
        when(userService.getUserName("13")).thenReturn(Observable.just("friend1"));
        when(userService.getUserName("14")).thenReturn(Observable.just("friend2"));
        when(achievementsService.getUserAchievement("13", RANKING)).thenReturn(Observable.just(List.of(
                new Achievement("1", "2", "13", RANKING, "3", 75)
        )));
        when(achievementsService.getUserAchievement("14", RANKING)).thenReturn(Observable.just(List.of(
                new Achievement("4", "5", "14", RANKING, "6", 100)
        )));

        clickOn("#friendsButton");
        verifyThat("#friendsList", NodeMatchers.isVisible());
        ListView<HBox> list = lookup("#friendsList").query();
        ObservableList<HBox> items = list.getItems();
        assertThat(list).hasExactlyNumItems(4);

        HBox hBox1 = items.get(2);
        HBox hbox2 = items.get(3);
        Label label1 = (Label) hBox1.getChildren().get(1);
        Label label2 = (Label) hbox2.getChildren().get(1);
        assertEquals(label1.getText(), " RP: 75");
        assertEquals(label2.getText(), " RP: 100");
        clickOn("#friendsButton");
    }


}
