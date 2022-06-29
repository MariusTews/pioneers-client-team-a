package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.error.ErrorResponse;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.service.GameService;
import com.aviumauctores.pioneers.service.ErrorService;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.UserService;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class JoinGameController extends LoggedInController {

    private final App app;
    private final GameService gameService;
    private final ErrorService errorService;
    private final EventListener eventListener;
    private final ResourceBundle bundle;
    private final Provider<LobbyController> lobbyController;
    private final Provider<GameReadyController> gameReadyController;
    @FXML
    public Label gameNameLabel;
    @FXML
    public PasswordField passwordTextField;
    @FXML
    public TextField showPasswordTextField;
    @FXML
    public Button showPasswordButton;
    @FXML
    public Button joinGameButton;
    @FXML
    public Button leaveButton;

    @FXML
    public ImageView viewPassword;

    private boolean hidePassword = true;

    public final SimpleStringProperty password = new SimpleStringProperty();

    Image show;
    Image hide;


    private final HashMap<String, String> errorCodes = new HashMap<>();

    @Inject
    public JoinGameController(App app,
                              LoginService loginService, UserService userService,
                              GameService gameService, ErrorService errorService,
                              EventListener eventListener,
                              ResourceBundle bundle,
                              Provider<LobbyController> lobbyController,
                              Provider<GameReadyController> gameReadyController) {
        super(loginService, userService);
        this.app = app;
        this.gameService = gameService;
        this.errorService = errorService;
        this.eventListener = eventListener;
        this.bundle = bundle;
        this.lobbyController = lobbyController;
        this.gameReadyController = gameReadyController;
    }

    public void init() {
        disposables = new CompositeDisposable();
        show = new Image(Objects.requireNonNull(Main.class.getResource("views/showPassword.png")).toString());
        hide = new Image(Objects.requireNonNull(Main.class.getResource("views/notShowPassword.png")).toString());
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

        errorService.setErrorCodesJoinGameController();
    }

    @Override
    public void destroy(boolean closed) {
        super.destroy(closed);
    }

    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/joinGameScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        //press esc to leave
        leaveButton.setCancelButton(true);

        passwordTextField.textProperty().bindBidirectional(password);

        if (passwordTextField.getText() == null) {
            passwordTextField.setText("");
        }

        joinGameButton.disableProperty().bind(
                Bindings.createBooleanBinding(() ->
                        passwordTextField.getText().trim().isEmpty(), passwordTextField.textProperty())
        );


        return parent;
    }

    public void showPassword(ActionEvent actionEvent) {

        //check button status
        if (passwordTextField.getText().isEmpty()) {
            return;
        }

        //set source for Image and show/hide password depending on hidePassword
        if (hidePassword) {
            viewPassword.setImage(hide);
            String password = passwordTextField.getText();
            showPasswordTextField.setText(password);
        } else {
            viewPassword.setImage(show);
            showPasswordTextField.setText("");
        }
        showPasswordTextField.setVisible(hidePassword);
        passwordTextField.setVisible(!hidePassword);
        hidePassword = !hidePassword;

    }

    public void joinGame(ActionEvent actionEvent) {
        disposables.add(gameService.joinGame(passwordTextField.getText())
                .observeOn(FX_SCHEDULER)
                .subscribe(member -> {
                    app.show(gameReadyController.get());
                }, errorService::handleError));
    }

    public void quit(ActionEvent actionEvent) {
        toLobbyScreen();
    }

    private void toLobbyScreen() {
        gameService.setCurrentGameID(null);
        app.show(lobbyController.get());
    }
}
