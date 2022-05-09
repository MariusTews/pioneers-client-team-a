package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.service.CreateGameService;
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

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class LobbyController implements Controller {

    private final App app;
    private final CreateGameService createGameService;
    private final EventListener eventListener;
    private final Provider<LoginController> loginController;
    private final Provider<ChatController> chatController;
    private final Provider<CreateGameController> createGameController;
    private final Provider<JoinGameController> joinGameController;

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
    public LobbyController(App app, CreateGameService createGameService, EventListener eventListener,
                           Provider<LoginController> loginController,
                           Provider<ChatController> chatController,
                           Provider<CreateGameController> createGameController,
                           Provider<JoinGameController> joinGameController){
        this.app = app;
        this.createGameService = createGameService;
        this.eventListener = eventListener;
        this.loginController = loginController;
        this.chatController = chatController;
        this.createGameController = createGameController;
        this.joinGameController = joinGameController;
    }




    public void init(){
        disposables = new CompositeDisposable();
        disposables.add(createGameService.listGames()
                .observeOn(FX_SCHEDULER)
                .subscribe(games -> {
                    games.forEach(this::addGameToList);
                    // This might be called before render when gameLabel is not initialized yet
                    if (gameLabel != null) {
                        gameLabel.setText(String.format("Spiele (%d)", gameItems.size()));
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
                    gameLabel.setText(String.format("Spiele (%d)", gameItems.size()));
                }));
    }

    private void addGameToList(Game game) {
        GameListItemController controller = new GameListItemController(this, game, gameItems);
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
        gameLabel.setText(String.format("Spiele (%d)", gameItems.size()));
        return parent;
    }

    public void toCreateGame(ActionEvent event) {
        final CreateGameController controller = createGameController.get();
        controller.gameName.set("");
        controller.password.set("");
        app.show(controller);
    }

    public void toJoinGame(Game game) {
        createGameService.setCurrentGameID(game._id());
        app.show(joinGameController.get());
    }

    public void toChat(ActionEvent event) {

        final ChatController controller = chatController.get();
        app.show(controller);
    }

    public void quit(ActionEvent event) {
        //maybe dont go to login but instead directly quit the application?
        final LoginController controller = loginController.get();
        app.show(controller);
    }
}
