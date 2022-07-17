package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.service.*;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;
import static com.aviumauctores.pioneers.Constants.*;

public class PostGameController extends  LoggedInController {

    private final App app;
    private final ResourceBundle bundle;
    private final Provider<LobbyController> lobbyController;
    private final RankingService rankingService;
    private final GameService gameService;
    private final GameMemberService gameMemberService;
    private final Provider<StatController> statController;
    private final AchievementsService achievementsService;
    public Label winnerHeader;
    public Label winnerName;
    public HBox secondPlayerContainer;
    public Label secondPlayerName;
    public Label thirdPlayerName;
    public Button returnButton;
    public Button showStatsButton;
    public ButtonBar buttonBar;
    public HBox thirdPlayerContainer;
    public Label fourthPlayerName;
    public HBox fourthPlayerContainer;
    public VBox winnerContainer;

    @Inject
    public PostGameController(App app, LoginService loginService, UserService userService, ResourceBundle bundle,
                              Provider<LobbyController> lobbyController, RankingService rankingService,
                              GameService gameService, GameMemberService gameMemberService, Provider<StatController> statController,
                              AchievementsService achievementsService) {
        super(loginService, userService);
        this.app = app;
        this.bundle = bundle;
        this.lobbyController = lobbyController;
        this.rankingService = rankingService;
        this.gameService = gameService;
        this.gameMemberService = gameMemberService;
        this.statController = statController;
        this.achievementsService = achievementsService;
    }

    @Override
    public void init() {
        disposables = new CompositeDisposable();
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/postGameScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        }catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        Node[] container = new Node[]{winnerContainer, secondPlayerContainer, thirdPlayerContainer, fourthPlayerContainer};
        Label[] labels = new Label[]{winnerName, secondPlayerName, thirdPlayerName, fourthPlayerName};
        HashMap<Integer, Player> ranking = rankingService.createRanking();
        int numPlayers = ranking.size();
        for(int i = 0; i < 4; i++){
            if(i >= numPlayers){
                container[i].setVisible(false);
            }else {
                String playerID = ranking.get(i).userId();
                if (Objects.equals(playerID, userService.getCurrentUserID())) {
                    disposables.add(achievementsService.putAchievement(RANKING, 100 - i * 25)
                            .observeOn(FX_SCHEDULER)
                            .subscribe());
                    disposables.add(achievementsService.putAchievement(WINSTREAK, 1)
                            .observeOn(FX_SCHEDULER)
                            .subscribe());
                    disposables.add(achievementsService.putAchievement(ACHIEVEMENT_WIN, 1)
                            .observeOn(FX_SCHEDULER)
                            .subscribe());
                }
                labels[i].setText(userService.getUserName(playerID).blockingFirst());
            }
        }
        return parent;
    }

    public void toLobby(ActionEvent event) {
        rankingService.clear();
        gameMemberService.deleteMember(userService.getCurrentUserID());
        gameService.setCurrentGameID(null);
        app.show(lobbyController.get());
    }

    public void toStatScreen(ActionEvent event) {
        app.show(statController.get());
    }
}
