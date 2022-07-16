package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.*;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.*;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class LobbyController extends PlayerListController {

    private final App app;
    public VBox mainVbox;

    private User user = null;
    private final GameService gameService;
    private final ErrorService errorService;
    private final PreferenceService preferenceService;
    private final EventListener eventListener;
    private ResourceBundle bundle;
    private final Provider<LoginController> loginController;
    private final Provider<ChatController> chatController;
    private final Provider<CreateGameController> createGameController;
    private final Provider<JoinGameController> joinGameController;
    private final Provider<SettingsController> settingsController;

    private final Provider<AchievementsController> achievementsController;

    @FXML
    public Label gameLabel;

    @FXML
    public Button friendsButton;

    @FXML
    public ListView<Parent> gameListView;

    @FXML
    public Label playerLabel;

    @FXML
    public ListView<Parent> playerListView;

    @FXML
    public Button createGameButton;

    @FXML
    public Button chatButton;

    @FXML
    public Button quitButton;

    @FXML
    public Button settingsButton;

    @FXML

    private final ObservableList<Parent> gameItems = FXCollections.observableArrayList();
    private final HashMap<String, GameListItemController> gameListItemControllers = new HashMap<>();

    @Inject
    public LobbyController(App app,
                           LoginService loginService, UserService userService,
                           GameService gameService, ErrorService errorService,
                           PreferenceService preferenceService,
                           EventListener eventListener,
                           ResourceBundle bundle,
                           Provider<LoginController> loginController,
                           Provider<ChatController> chatController,
                           Provider<CreateGameController> createGameController,
                           Provider<JoinGameController> joinGameController,
                           Provider<SettingsController> settingsController,
                           Provider<AchievementsController> achievementsController) {
        super(loginService, userService);
        this.app = app;
        this.gameService = gameService;
        this.errorService = errorService;
        this.preferenceService = preferenceService;
        this.eventListener = eventListener;
        this.bundle = bundle;
        this.loginController = loginController;
        this.chatController = chatController;
        this.createGameController = createGameController;
        this.joinGameController = joinGameController;
        this.settingsController = settingsController;
        this.achievementsController = achievementsController;
    }


    public void init() {
        disposables = new CompositeDisposable();

        // Get games via REST
        disposables.add(gameService.listGames()
                .observeOn(FX_SCHEDULER)
                .subscribe(games -> {
                    games.forEach(this::addGameToList);
                    // This might be called before render when gameLabel is not initialized yet
                    if (gameLabel != null) {
                        updateGameLabel();
                    }
                }));

        // Get users via REST
        disposables.add(userService.listOnlineUsers()
                .observeOn(FX_SCHEDULER)
                .subscribe(users -> {
                    users.forEach(this::addPlayerToList);
                    if (playerLabel != null) {
                        updatePlayerLabel();
                    }
                }));

        // Listen to game updates
        disposables.add(eventListener.listen("games.*.*", Game.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(eventDto -> {
                    String event = eventDto.event();
                    Game game = eventDto.data();
                    if (event.endsWith("created")) {
                        addGameToList(game);
                    } else if (event.endsWith("updated")) {
                        GameListItemController controller = gameListItemControllers.get(game._id());
                        if (controller != null) {
                            controller.onGameUpdated(game);
                        }
                    } else if (event.endsWith("deleted")) {
                        GameListItemController controller = gameListItemControllers.get(game._id());
                        if (controller != null) {
                            removeGameFromList(game._id(), controller);
                        }
                    }
                    updateGameLabel();
                }));
        // Listen to user updates
        disposables.add(eventListener.listen("users.*.*", User.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(this::onUserEvent));
    }

    private void addGameToList(Game game) {
        GameListItemController controller = new GameListItemController(this, game, gameItems, bundle);
        gameListItemControllers.put(game._id(), controller);
        addGameItem(controller);
    }

    private void addGameItem(GameListItemController controller) {
        gameItems.add(controller.render());
    }

    private void updateGameLabel() {
        gameLabel.setText(String.format(bundle.getString("games") + " (%d)", gameItems.size()));
    }

    private void removeGameFromList(String gameID, GameListItemController controller) {
        controller.destroy(false);
        gameListItemControllers.remove(gameID);
    }

    @Override
    protected void updatePlayerLabel() {
        playerLabel.setText(String.format(bundle.getString("online.players") + " (%d)", playerItems.size()));
    }

    public void destroy(boolean closed) {
        super.destroy(closed);
        // Destroy and delete each sub controller
        // We cannot call remove<Name>FromList since it would remove elements from the map while iterating over it
        gameListItemControllers.forEach((id, controller) -> controller.destroy(false));
        gameListItemControllers.clear();
        playerListItemControllers.forEach((id, controller) -> controller.destroy(false));
        playerListItemControllers.clear();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/lobbyScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


        gameListView.setItems(gameItems);
        updateGameLabel();
        playerListView.setItems(playerItems);
        updatePlayerLabel();
        return parent;
    }

    public void toCreateGame(ActionEvent event) {
        final CreateGameController controller = createGameController.get();
        app.show(controller);
    }

    public void toJoinGame(Game game) {
        gameService.setCurrentGameID(game._id());
        app.show(joinGameController.get());
    }

    public void toChat(ActionEvent event) {
        final ChatController controller = chatController.get();
        app.show(controller);
    }

    public void quit(ActionEvent event) {
        errorService.setErrorCodesLogout();
        disposables.add(loginService.logout()
                .observeOn(FX_SCHEDULER)
                .subscribe(() -> {
                    preferenceService.setRememberMe(false);
                    preferenceService.setRefreshToken("");
                    app.show(loginController.get());
                }, errorService::handleError));
    }

    public void toSettings(ActionEvent event) {
        final SettingsController controller = settingsController.get();
        app.show(controller);
    }

    public void setGerman(MouseEvent event) {
        preferenceService.setLocale(Locale.GERMAN);
        changeLanguage();
    }

    public void setEnglish(MouseEvent event) {
        preferenceService.setLocale(Locale.ENGLISH);
        changeLanguage();
    }

    private void changeLanguage() {
        this.bundle = getNewResourceBundle();
        updateGameLabel();
        updatePlayerLabel();
        quitButton.setText(bundle.getString("sign.out"));
        createGameButton.setText(bundle.getString("create.game"));
        gameListItemControllers.values().forEach(this::changeGameListItemLanguage);
    }

    private void changeGameListItemLanguage(GameListItemController controller) {
        controller.getJoinButton().setText(bundle.getString("join"));
        controller.getNumMembersTextLabel().setText(bundle.getString("amount.players") + ":");
    }

    private ResourceBundle getNewResourceBundle() {
        return ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", preferenceService.getLocale());
    }

    public void inToAchievement(MouseEvent mouseEvent) {
        final AchievementsController controller = achievementsController.get();
        app.show(controller);
    }
}
