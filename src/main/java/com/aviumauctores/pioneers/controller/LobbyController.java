package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.error.ErrorResponse;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.service.ErrorService;
import com.aviumauctores.pioneers.service.GameService;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.PreferenceService;
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
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class LobbyController implements Controller {

    private final App app;

    private User user = null;
    private final LoginService loginService;
    private final GameService gameService;
    private final ErrorService errorService;
    private final PreferenceService preferenceService;
    private final EventListener eventListener;
    private final ResourceBundle bundle;
    private final Provider<LoginController> loginController;
    private final Provider<ChatController> chatController;
    private final Provider<CreateGameController> createGameController;
    private final Provider<JoinGameController> joinGameController;

    private HashMap<String, String> errorCodes = new HashMap<>();

    @FXML public Label gameLabel;

    @FXML public ListView<Parent> gameListView;

    @FXML public Label playerLabel;

    @FXML public ListView playerListView;

    @FXML public Button createGameButton;

    @FXML public Button chatButton;

    @FXML public Button quitButton;

    private final ObservableList<Parent> gameItems = FXCollections.observableArrayList();

    private final Map<String, GameListItemController> gameListItemControllers = new HashMap<>();

    private CompositeDisposable disposables;

    @Inject
    public LobbyController(App app,
                           LoginService loginService, GameService gameService, ErrorService errorService,
                           PreferenceService preferenceService,
                           EventListener eventListener,
                           ResourceBundle bundle,
                           Provider<LoginController> loginController,
                           Provider<ChatController> chatController,
                           Provider<CreateGameController> createGameController,
                           Provider<JoinGameController> joinGameController){
        this.app = app;
        this.loginService = loginService;
        this.gameService = gameService;
        this.errorService = errorService;
        this.preferenceService = preferenceService;
        this.eventListener = eventListener;
        this.bundle = bundle;
        this.loginController = loginController;
        this.chatController = chatController;
        this.createGameController = createGameController;
        this.joinGameController = joinGameController;
    }


    public void init(){
        disposables = new CompositeDisposable();
        disposables.add(gameService.listGames()
                .observeOn(FX_SCHEDULER)
                .subscribe(games -> {
                    games.forEach(this::addGameToList);
                    // This might be called before render when gameLabel is not initialized yet
                    if (gameLabel != null) {
                        gameLabel.setText(String.format(bundle.getString("games") + " (%d)", gameItems.size()));
                    }
                }));
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
                            controller.destroy();
                            gameListItemControllers.remove(game._id());
                        }
                    }
                    gameLabel.setText(String.format(bundle.getString("games") + " (%d)", gameItems.size()));
                }));

        errorCodes.put("400", bundle.getString("validation.failed"));
        errorCodes.put("401", bundle.getString("invalid.token"));
        errorCodes.put("429", bundle.getString("limit.reached"));

    }

    private void addGameToList(Game game) {
        GameListItemController controller = new GameListItemController(this, game, gameItems, bundle);
        gameListItemControllers.put(game._id(), controller);
        gameItems.add(controller.render());
    }

    public void destroy(){
        if (disposables != null) {
            disposables.dispose();
            disposables = null;
        }
        // Destroy and delete each sub controller
        gameListItemControllers.forEach((id, controller) -> controller.destroy());
        gameListItemControllers.clear();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public Parent render(){
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/lobbyScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        }catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        gameListView.setItems(gameItems);
        gameLabel.setText(String.format(bundle.getString("games") + " (%d)", gameItems.size()));
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
                .subscribe(() -> {
                            preferenceService.setRememberMe(false);
                            preferenceService.setRefreshToken("");
                            app.show(loginController.get());
                        },
                        throwable -> {
                            if (throwable instanceof HttpException ex) {
                                ErrorResponse response = errorService.readErrorMessage(ex);
                                String message = errorCodes.get(Integer.toString(response.statusCode()));
                                app.showHttpErrorDialog(response, message);
                            } else {
                                app.showErrorDialog(bundle.getString("connection.failed"), bundle.getString("try.again"));
                            }
                        }));
    }
}
