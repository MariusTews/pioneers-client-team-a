package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.dto.error.ErrorResponse;
import com.aviumauctores.pioneers.dto.error.ValidationErrorResponse;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.CryptoService;
import com.aviumauctores.pioneers.service.ErrorService;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.PreferenceService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseEvent;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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

    private Disposable disposable;

    private final HashMap<String, String> errorCodes = new HashMap<>();

    @FXML public TextField usernameInput;
    @FXML public PasswordField passwordInput;
    @FXML public Button loginButton;
    @FXML public CheckBox rememberMeCheckBox;
    @FXML public Button registerButton;
    @FXML public Label usernameErrorLabel;
    @FXML public Label passwordErrorLabel;

    @Inject
    public LoginController(App app, LoginService loginService, Provider<RegisterController> registerController,
                           Provider<LobbyController> lobbyController, Provider<LoginController> loginController,
                           PreferenceService preferenceService, CryptoService cryptoService, ResourceBundle bundle,
                           ErrorService errorService){
        this.app = app;
        this.loginService = loginService;
        this.registerController = registerController;
        this.lobbyController = lobbyController;
        this.loginController = loginController;
        this.preferenceService = preferenceService;
        this.cryptoService = cryptoService;
        this.bundle = bundle;
        this.errorService = errorService;
    }

    @Override
    public void init(){
        errorCodes.put("400", bundle.getString("validation.failed"));
        errorCodes.put("401", bundle.getString("invalid.username.password"));
        errorCodes.put("429", bundle.getString("limit.reached"));
    }

    @Override
    public void destroy(){
        if (this.disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public Parent render(){
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/loginScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        }catch(IOException e) {
            e.printStackTrace();
            return null;
        }

        //necessary for fast testing; otherwise you would have to move the mouse to the input first, which takes time
        Platform.runLater(() -> usernameInput.requestFocus());

        return parent;
    }

    public Boolean getRememberMeStatus(){
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
            }
            else {
                usernameErrorLabel.setText("");
            }
            if (passwordEmpty) {
                passwordErrorLabel.setText(bundle.getString("invalid.input"));
            }
            else {
                passwordErrorLabel.setText("");
            }
        }
        else {
            usernameErrorLabel.setText("");
            passwordErrorLabel.setText("");

            disposable = loginService.login(username, password)
                    .subscribeOn(FX_SCHEDULER)
                    .subscribe(
                            result -> {
                                if (rememberMeCheckBox.isSelected()){
                                    preferenceService.setRememberMe(true);
                                    String encodedToken = cryptoService.encode(result.refreshToken());
                                    preferenceService.setRefreshToken(encodedToken);
                                }
                                else{
                                    preferenceService.setRememberMe(false);
                                    preferenceService.setRefreshToken("");
                                }

                                toLobby(result);
                            },
                            //temporary solution
                            throwable -> {
                                if (throwable instanceof HttpException ex) {
                                    Object object = errorService.readErrorMessage(ex);
                                    if (object instanceof ErrorResponse response) {
                                        String message = errorCodes.get(Integer.toString(response.statusCode()));
                                        Platform.runLater(() -> app.showHttpErrorDialog(response.statusCode(), response.error(), message));
                                    }
                                    else {
                                        ValidationErrorResponse response = (ValidationErrorResponse) object;
                                        String message = errorCodes.get(Integer.toString(response.statusCode()));
                                        Platform.runLater(() -> app.showHttpErrorDialog(response.statusCode(), response.error(), message));
                                    }
                                } else {
                                    app.showErrorDialog(bundle.getString("connection.failed"), bundle.getString("try.again"));
                                }
                            }
                    );
        }
    }

    public Observable<LoginResult> tryTokenLogin() {
        String token = "";
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

    public void toLobby(LoginResult result) {
        final LobbyController controller = lobbyController.get();
        User user = new User(result._id(), result.name(), result.status(), result.avatar());
        controller.setUser(user);
        app.show(controller);
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
