package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Achievement;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.AchievementsService;
import com.aviumauctores.pioneers.model.Achievement;
import com.aviumauctores.pioneers.service.AchievementsService;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Provider;

import java.io.IOException;
import java.util.Objects;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;
import static com.aviumauctores.pioneers.Constants.RANKING;
import static com.aviumauctores.pioneers.Constants.*;

public class AchievementsController extends PlayerListController {

    private final App app;
    private final ResourceBundle bundle;
    private final AchievementsService achievementsService;

    @FXML
    public Button friendsButton;
    @FXML
    public Label playerLabel2;
    @FXML
    public VBox aM1;
    @FXML
    public VBox aM2;
    @FXML
    public VBox aM3;
    @FXML
    public VBox aM4;
    @FXML
    public VBox aM5;
    @FXML
    public VBox aM6;
    @FXML
    public VBox aM7;
    @FXML
    public VBox aM11;
    @FXML
    public Label settlementsALabel;
    @FXML
    public Label roadMasterLabel;
    @FXML
    public Label cityMasterLabel;
    @FXML
    public Label workaholicLabel;
    @FXML
    public Label winnerLabel;
    @FXML
    public Label longestRoadLabel;
    @FXML
    public Label marcoPolo;
    @FXML
    public Label capitalistLabel;
    @FXML
    public Button leaveButton;
    @FXML
    public Label rankingPointsLabel;
    @FXML
    public Label achievementsLabel;
    @FXML
    public VBox aM8;


    private final Provider<LobbyController> lobbyController;
    public VBox aM9;
    public VBox aM10;
    public VBox aM12;
    public VBox aM13;
    public VBox aM14;
    public VBox aM15;
    public Pane achievementsPane;


    private Color hoverColor;

    private final Color defaultColor;
    private ListView<HBox> friendsList;

    @Inject
    public AchievementsController(App app,
                                  Provider<LobbyController> lobbyController,
                                  ResourceBundle bundle,
                                  LoginService loginService,
                                  UserService userService, AchievementsService achievementsService) {
        super(loginService, userService);
        this.lobbyController = lobbyController;
        this.app = app;
        this.bundle = bundle;
        this.achievementsService = achievementsService;

        defaultColor = Color.rgb(255, 255, 255);


    }

    @Override
    public void init() {
        disposables = new CompositeDisposable();

        if (app.getLightTheme()) {
            hoverColor = Color.rgb(63, 189, 63);
        } else {
            hoverColor = Color.rgb(174, 229, 143);
        }
        achievementsService.init();
        disposables.add(achievementsService.listUserAchievements().observeOn(FX_SCHEDULER).subscribe(achievements -> {
            for (Achievement achievement : achievements) {
                if (achievement.unlockedAt() != null) {

                    switch (achievement.id()) {
                        case (ACHIEVEMENT_SETTLEMENTS) -> setBackground(aM1, Color.FORESTGREEN);
                        case (ACHIEVEMENT_ROADS) -> setBackground(aM2, Color.FORESTGREEN);
                        case (ACHIEVEMENT_CITIES) -> setBackground(aM3, Color.FORESTGREEN);
                        case (ACHIEVEMENT_ALL) -> setBackground(aM4, Color.FORESTGREEN);
                        case (ACHIEVEMENT_WIN) -> setBackground(aM5, Color.FORESTGREEN);
                        case (ACHIEVEMENTS_WIN_LONGEST) -> setBackground(aM6, Color.FORESTGREEN);
                        case (ACHIEVEMENT_RESOURCES) -> setBackground(aM7, Color.FORESTGREEN);
                        case (ACHIEVEMENT_TRADE) -> setBackground(aM7, Color.FORESTGREEN);
                        default -> {
                        }
                    }
                }
            }
        }));

        disposables.add(achievementsService.getUserAchievement(userService.getCurrentUserID(), RANKING)
                .observeOn(FX_SCHEDULER)
                .subscribe(success -> {
                    for (Achievement achievement : success) {

                        if (Objects.equals(achievement.id(), RANKING)) {
                            rankingPointsLabel.setText(achievement.progress()+" "+ bundle.getString("ranking.points"));
                        }
                    }
                }, error -> rankingPointsLabel.setText("0"+ " " + bundle.getString("ranking.points"))));


    }

    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/achievementScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        friendsButton.setOnAction(this::onFriends);
        disposables.add(userService.getUserName(userService.getCurrentUserID())
                .observeOn(FX_SCHEDULER)
                .subscribe(name -> playerLabel2.setText(name + "")));


        return parent;
    }

    @Override
    public void destroy(boolean closed) {
        if (closed) {
            disposables.add(userService.changeCurrentUserStatus("offline")
                    .subscribe(() -> {
                        if (disposables != null) {
                            disposables.dispose();
                            disposables = null;
                        }
                    }));
        } else {
            if (disposables != null) {
                disposables.dispose();
                disposables = null;
            }
        }
    }

    public void onFriends(ActionEvent actionEvent) {
        // init friendsList
        friendsList = new ListView<>();
        friendsList.setMaxSize(200, 300);

        // setup header
        HBox headerHbox = new HBox();
        Label headerLabel = new Label(bundle.getString("friends"));
        headerLabel.setStyle("-fx-font-size: 20px");
        headerHbox.getChildren().add(headerLabel);
        HBox empty = new HBox(new Label());
        friendsList.getItems().addAll(headerHbox, empty);

        // add friends
        User myUser = userService.getUserByID(userService.getCurrentUserID()).blockingFirst();
        List<String> friends = myUser.friends();
        for (String friend : friends) {
            StringBuilder name = new StringBuilder(userService.getUserName(friend).blockingFirst());
            int length = name.length();
            name.append(" ".repeat(Math.max(0, 24 - length)));
            HBox friendsHbox = new HBox(new Label(name.toString()));
            disposables.add(achievementsService.getUserAchievement(friend, RANKING)
                    .observeOn(FX_SCHEDULER).
                    subscribe(success -> {
                        for (Achievement achievement : success) {
                            if (Objects.equals(achievement.id(), RANKING)) {
                                friendsHbox.getChildren().add(new Label(" RP: " + achievement.progress()));
                            }
                        }
                    }, error -> friendsHbox.getChildren().add(new Label(" RP: 0"))));
            friendsList.getItems().add(friendsHbox);
        }
        friendsList.setLayoutX(friendsButton.getLayoutX()+270);
        friendsList.setLayoutY(friendsButton.getLayoutY());

        achievementsPane.getChildren().add(friendsList);

    }

    public void leaveAchievments(MouseEvent mouseEvent) {
        final LobbyController controller = lobbyController.get();
        app.show(controller);

    }

    @Override
    protected void updatePlayerLabel() {

    }

    public void helloaM1(MouseEvent mouseEvent) {
        if (aM1.getBackground().equals(Background.fill(Color.FORESTGREEN))) {
            setTooltip(settlementsALabel, bundle.getString("build-settlements"));
        } else
            setBackground(aM1, hoverColor);
        setTooltip(settlementsALabel, bundle.getString("build-settlements"));
    }

    public void byeaM1(MouseEvent mouseEvent) {
        if (aM1.getBackground().equals(Background.fill(Color.FORESTGREEN))) {
            deleteTooltip(settlementsALabel);
        } else
            setBackground(aM1, defaultColor);
        deleteTooltip(settlementsALabel);
    }

    public void helloaM2(MouseEvent mouseEvent) {

        if (aM2.getBackground().equals(Background.fill(Color.FORESTGREEN))) {
            setTooltip(roadMasterLabel, bundle.getString("build-roads"));
        } else {
            setBackground(aM2, hoverColor);
            setTooltip(roadMasterLabel, bundle.getString("build-roads"));
        }

    }

    public void byeaM2(MouseEvent mouseEvent) {
        if (aM2.getBackground().equals(Background.fill(Color.FORESTGREEN))) {
            deleteTooltip(roadMasterLabel);
        } else {
            setBackground(aM2, defaultColor);
            deleteTooltip(roadMasterLabel);
        }
    }

    public void helloaM3(MouseEvent mouseEvent) {
        if (aM3.getBackground().equals(Background.fill(Color.FORESTGREEN))) {
            setTooltip(cityMasterLabel, bundle.getString("build-cities"));
        } else {
            setBackground(aM3, hoverColor);
            setTooltip(cityMasterLabel, bundle.getString("build-cities"));
        }
    }

    public void byeaM3(MouseEvent mouseEvent) {
        if (aM3.getBackground().equals(Background.fill(Color.FORESTGREEN))) {
            deleteTooltip(cityMasterLabel);
        } else {
            setBackground(aM3, defaultColor);
            deleteTooltip(cityMasterLabel);
        }
    }

    public void helloaM4(MouseEvent mouseEvent) {
        if (aM4.getBackground().equals(Background.fill(Color.FORESTGREEN))) {
            setTooltip(workaholicLabel, bundle.getString("workaholic"));
        } else {
            setBackground(aM4, hoverColor);
            setTooltip(workaholicLabel, bundle.getString("workaholic"));
        }
    }

    public void byeaM4(MouseEvent mouseEvent) {
        if (aM4.getBackground().equals(Background.fill(Color.FORESTGREEN))) {
            deleteTooltip(workaholicLabel);
        } else {
            setBackground(aM4, defaultColor);
            deleteTooltip(workaholicLabel);
        }
    }

    public void helloaM5(MouseEvent mouseEvent) {
        if (aM5.getBackground().equals(Background.fill(Color.FORESTGREEN))) {
            setTooltip(winnerLabel, bundle.getString("winner"));
        } else {
            setBackground(aM5, hoverColor);
            setTooltip(winnerLabel, bundle.getString("winner"));
        }
    }

    public void byeaM5(MouseEvent mouseEvent) {
        if (aM5.getBackground().equals(Background.fill(Color.FORESTGREEN))) {
            deleteTooltip(winnerLabel);
        } else {
            setBackground(aM5, defaultColor);
            deleteTooltip(winnerLabel);
        }
    }

    public void helloaM6(MouseEvent mouseEvent) {
        if (aM6.getBackground().equals(Background.fill(Color.FORESTGREEN))) {
            setTooltip(longestRoadLabel, bundle.getString("longestRoad"));
        } else {
            setBackground(aM6, hoverColor);
            setTooltip(longestRoadLabel, bundle.getString("longestRoad"));
        }
    }

    public void byeaM6(MouseEvent mouseEvent) {
        if (aM6.getBackground().equals(Background.fill(Color.FORESTGREEN))) {
            deleteTooltip(longestRoadLabel);
        } else {
            setBackground(aM6, defaultColor);
            deleteTooltip(longestRoadLabel);
        }
    }

    public void helloaM7(MouseEvent mouseEvent) {
        if (aM7.getBackground().equals(Background.fill(Color.FORESTGREEN))) {
            setTooltip(marcoPolo, bundle.getString("marcoPolo"));
        } else {
            setBackground(aM7, hoverColor);
            setTooltip(marcoPolo, bundle.getString("marcoPolo"));
        }
    }

    public void byeaM7(MouseEvent mouseEvent) {
        if (aM7.getBackground().equals(Background.fill(Color.FORESTGREEN))) {
            deleteTooltip(marcoPolo);
        } else {
            setBackground(aM7, defaultColor);
            deleteTooltip(marcoPolo);
        }
    }

    public void helloaM11(MouseEvent mouseEvent) {
        if (aM4.getBackground().equals(Background.fill(Color.FORESTGREEN))) {
            setTooltip(capitalistLabel, bundle.getString("capitalist"));

        } else {
            setBackground(aM11, hoverColor);
            setTooltip(capitalistLabel, bundle.getString("capitalist"));
        }
    }

    public void byeaM11(MouseEvent mouseEvent) {
        if (aM11.getBackground().equals(Background.fill(Color.FORESTGREEN))) {
            deleteTooltip(capitalistLabel);
        } else {
            setBackground(aM11, defaultColor);
            deleteTooltip(capitalistLabel);
        }
    }

    public void setBackground(VBox vbox, Color color) {
        BackgroundFill bf = new BackgroundFill(color, new CornerRadii(1), null);
        vbox.setBackground(new Background(bf));
    }

    public void setTooltip(Label achievmentsLabel, String conditions) {
        Tooltip tooltip = new Tooltip(conditions);
        tooltip.setHideDelay(Duration.seconds(10));
        tooltip.setShowDelay(Duration.seconds(0));
        achievmentsLabel.setTooltip(tooltip);
    }

    public void deleteTooltip(Label achievmentsLabel) {
        achievmentsLabel.setTooltip(null);
    }

    public void onAchievementsPaneClicked(MouseEvent mouseEvent) {
        if (friendsList != null) {
            achievementsPane.getChildren().remove(friendsList);
            friendsList = null;
        }
    }
}
