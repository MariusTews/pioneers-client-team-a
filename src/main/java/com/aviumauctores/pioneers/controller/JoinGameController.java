package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.service.GameService;
import com.aviumauctores.pioneers.service.ErrorService;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class JoinGameController implements Controller {

    private final App app;
    private final GameService gameService;
    private final ErrorService errorService;
    private final EventListener eventListener;
    private final Provider<LobbyController> lobbyController;
    private final Provider<GameReadyController> gameReadyController;
    @FXML public Label gameNameLabel;
    @FXML public PasswordField passwordTextField;
    @FXML public TextField showPasswordTextField;
    @FXML public Button showPasswordButton;
    @FXML public Button joinGameButton;
    @FXML public Button leaveButton;

    private CompositeDisposable disposables;

    @Inject
    public JoinGameController(App app, GameService gameService, ErrorService errorService,
                              EventListener eventListener,
                              Provider<LobbyController> lobbyController,
                              Provider<GameReadyController> gameReadyController) {
        this.app = app;
        this.gameService = gameService;
        this.errorService = errorService;
        this.eventListener = eventListener;
        this.lobbyController = lobbyController;
        this.gameReadyController = gameReadyController;
    }

    public void init(){
        disposables = new CompositeDisposable();
        disposables.add(gameService.getCurrentGame()
                .observeOn(FX_SCHEDULER)
                .subscribe(game -> gameNameLabel.setText(game.name()), throwable -> toLobbyScreen()));
        disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".*", Game.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(gameEventDto -> {
                    String event = gameEventDto.event();
                    Game game = gameEventDto.data();
                    if (event.endsWith("deleted")) {
                        // Game has been deleted, go back to lobby screen
                        toLobbyScreen();
                    } else {
                        // Game has been updated, update gameNameLabel
                        gameNameLabel.setText(game.name());
                    }
                }));
    }

    public void destroy(){
        if (disposables != null) {
            disposables.dispose();
            disposables = null;
        }
    }

    public Parent render(){
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/joinGameScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        passwordTextField.textProperty().bindBidirectional(showPasswordTextField.textProperty());
        showPasswordTextField.setManaged(false);
        return parent;
    }

    public void showPassword(ActionEvent actionEvent) {
        boolean passwordShowed = showPasswordTextField.isVisible();
        passwordTextField.setVisible(passwordShowed);
        passwordTextField.setManaged(passwordShowed);
        showPasswordTextField.setVisible(!passwordShowed);
        showPasswordTextField.setManaged(!passwordShowed);
    }

    public void joinGame(ActionEvent actionEvent) {
        disposables.add(gameService.joinGame(passwordTextField.getText())
                .observeOn(FX_SCHEDULER)
                .subscribe(member -> app.show(gameReadyController.get()),
                        throwable -> {
                            if (throwable instanceof HttpException ex) {
                                app.showHttpErrorDialog(errorService.readErrorMessage(ex));
                            } else {
                                app.showConnectionFailedDialog();
                            }
                        }));
    }

    public void quit(ActionEvent actionEvent) {
        toLobbyScreen();
    }

    private void toLobbyScreen() {
        gameService.setCurrentGameID(null);
        app.show(lobbyController.get());
    }
}
