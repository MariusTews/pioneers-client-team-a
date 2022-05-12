package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.ErrorService;
import com.aviumauctores.pioneers.service.GameService;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.UserService;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class LobbyController extends PlayerListController {

    private final App app;
    private final LoginService loginService;
    private final UserService userService;
    private final GameService gameService;
    private final ErrorService errorService;
    private final EventListener eventListener;
    private final Provider<LoginController> loginController;
    private final Provider<ChatController> chatController;
    private final Provider<CreateGameController> createGameController;
    private final Provider<JoinGameController> joinGameController;

    @FXML public Label gameLabel;

    @FXML public ListView<Parent> gameListView;

    @FXML public Label playerLabel;

    @FXML public ListView<Parent> playerListView;

    @FXML public Button createGameButton;

    @FXML public Button chatButton;

    @FXML public Button quitButton;

    private final ObservableList<Parent> gameItems = FXCollections.observableArrayList();
    private final Map<String, GameListItemController> gameListItemControllers = new HashMap<>();

    private CompositeDisposable disposables;

    @Inject
    public LobbyController(App app, LoginService loginService, UserService userService,
                           GameService gameService, ErrorService errorService,
                           EventListener eventListener,
                           Provider<LoginController> loginController,
                           Provider<ChatController> chatController,
                           Provider<CreateGameController> createGameController,
                           Provider<JoinGameController> joinGameController){
        this.app = app;
        this.loginService = loginService;
        this.userService = userService;
        this.gameService = gameService;
        this.errorService = errorService;
        this.eventListener = eventListener;
        this.loginController = loginController;
        this.chatController = chatController;
        this.createGameController = createGameController;
        this.joinGameController = joinGameController;
    }




    public void init(){
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
        GameListItemController controller = new GameListItemController(this, game, gameItems);
        gameListItemControllers.put(game._id(), controller);
        gameItems.add(controller.render());
    }

    private void updateGameLabel() {
        gameLabel.setText(String.format("Spiele (%d)", gameItems.size()));
    }

    private void removeGameFromList(String gameID, GameListItemController controller) {
        controller.destroy();
        gameListItemControllers.remove(gameID);
    }

    @Override
    protected void updatePlayerLabel() {
        playerLabel.setText(String.format("Online Spieler (%d)", playerItems.size()));
    }

    public void destroy(){
        if (disposables != null) {
            disposables.dispose();
            disposables = null;
        }
        // Destroy and delete each sub controller
        // We cannot call remove<Name>FromList since it would remove elements from the map while iterating over it
        gameListItemControllers.forEach((id, controller) -> controller.destroy());
        gameListItemControllers.clear();
        playerListItemControllers.forEach((id, controller) -> controller.destroy());
        playerListItemControllers.clear();
    }

    public Parent render(){
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/lobbyScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        }catch(IOException e) {
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
        controller.gameName.set("");
        controller.password.set("");
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
        disposables.add(loginService.logout()
                .observeOn(FX_SCHEDULER)
                .subscribe(() -> app.show(loginController.get()),
                        throwable -> {
                            if (throwable instanceof HttpException ex) {
                                app.showHttpErrorDialog(errorService.readErrorMessage(ex));
                            } else {
                                app.showConnectionFailedDialog();
                            }
                        }));
    }
}
