package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.dto.error.ErrorResponse;
import com.aviumauctores.pioneers.dto.error.ValidationErrorResponse;
import com.aviumauctores.pioneers.dto.users.UpdateUserDto;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.*;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class LoginController implements Controller {

    private final App app;
    private final LoginService loginService;
    private final Provider<RegisterController> registerController;
    private final Provider<LobbyController> lobbyController;
    private final Provider<LoginController> loginController;
    private final PreferenceService preferenceService;
    private final CryptoService cryptoService;
    private final ResourceBundle bundle;
    private final ErrorService errorService;
    private final HashMap<String, String> errorCodes = new HashMap<>();

    @FXML
    public TextField usernameInput;
    @FXML
    public PasswordField passwordInput;
    @FXML
    public Button loginButton;
    @FXML
    public CheckBox rememberMeCheckBox;
    @FXML
    public Button registerButton;
    @FXML
    public Label usernameErrorLabel;
    @FXML
    public Label passwordErrorLabel;


    private Disposable loginDisposable;
    private Disposable toLobbyDisposable;
    private final UserService userService;

    @Inject
    public LoginController(App app, LoginService loginService, Provider<RegisterController> registerController,
                           Provider<LobbyController> lobbyController, Provider<LoginController> loginController,
                           PreferenceService preferenceService, CryptoService cryptoService, ResourceBundle bundle,
                           ErrorService errorService, UserService userService) {
        this.app = app;
        this.loginService = loginService;
        this.registerController = registerController;
        this.lobbyController = lobbyController;
        this.loginController = loginController;
        this.preferenceService = preferenceService;
        this.cryptoService = cryptoService;
        this.bundle = bundle;
        this.errorService = errorService;
        this.userService = userService;
    }

    @Override
    public void init() {
        errorService.setErrorCodesLogin();
    }


    @Override
    public void destroy(boolean closed) {
        if (this.loginDisposable != null) {
            this.loginDisposable.dispose();
        }
        if (this.toLobbyDisposable != null) {
            this.toLobbyDisposable.dispose();
        }
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/loginScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        loginButton.setDefaultButton(true);
        //necessary for fast testing; otherwise you would have to move the mouse to the input first, which takes time
        Platform.runLater(() -> usernameInput.requestFocus());

        return parent;
    }

    public Boolean getRememberMeStatus() {
        return preferenceService.getRememberMe();
    }

    public void login(ActionEvent event) {
        String username = usernameInput.getText();
        String password = passwordInput.getText();

        Boolean usernameEmpty = username.isEmpty();
        Boolean passwordEmpty = password.isEmpty();

        //check whether username or password is empty
        if (usernameEmpty || passwordEmpty) {
            if (usernameEmpty) {
                usernameErrorLabel.setText(bundle.getString("invalid.input"));
            } else {
                usernameErrorLabel.setText("");
            }
            if (passwordEmpty) {
                passwordErrorLabel.setText(bundle.getString("invalid.input"));
            } else {
                passwordErrorLabel.setText("");
            }
        } else {
            usernameErrorLabel.setText("");
            passwordErrorLabel.setText("");

            loginDisposable = loginService.login(username, password)
                    .subscribeOn(FX_SCHEDULER)
                    .subscribe(
                            result -> {
                                if (rememberMeCheckBox.isSelected()) {
                                    preferenceService.setRememberMe(true);
                                    String encodedToken = cryptoService.encode(result.refreshToken());
                                    preferenceService.setRefreshToken(encodedToken);
                                } else {
                                    preferenceService.setRememberMe(false);
                                    preferenceService.setRefreshToken("");
                                }

                                toLobby(result);
                            }, errorService::handleError
                    );
        }
    }

    public Observable<LoginResult> tryTokenLogin() {
        String token;
        try {
            token = cryptoService.decode(preferenceService.getRefreshToken());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return loginService.login(token);
    }

    public void toRegister(ActionEvent event) {
        String username = usernameInput.getText();
        String password = passwordInput.getText();

        final RegisterController controller = registerController.get();

        controller.username.set(username);
        controller.password.set(password);

        app.show(controller);
    }

    public void toLobby(LoginResult loginResult) {
        errorService.setErrorCodesUsers();
        final LobbyController controller = lobbyController.get();
        User user = new User(loginResult._id(), loginResult.name(), "online", loginResult.avatar(),null);
        toLobbyDisposable = userService.updateUser(loginResult._id(), new UpdateUserDto(user.name(), user.status(), user.avatar(),null,null))
                .observeOn(FX_SCHEDULER)
                .subscribe(
                        result -> {
                            controller.setUser(user);
                            app.show(controller);
                        }, errorService::handleError

                );
    }

    public void setGerman(MouseEvent event) {
        preferenceService.setLocale(Locale.GERMAN);
        app.show(loginController.get());
    }

    public void setEnglish(MouseEvent event) {
        preferenceService.setLocale(Locale.ENGLISH);
        app.show(loginController.get());
    }

    public void selectLight(MouseEvent mouseEvent) {
        app.setTheme("light");
    }

    public void selectDark(MouseEvent mouseEvent) {
        app.setTheme("dark");
    }
}
