package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.CryptoService;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.PreferenceService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static com.aviumauctores.pioneers.Constants.*;

public class LoginController implements Controller {

    private final App app;
    private final LoginService loginService;
    private final Provider<RegisterController> registerController;
    private final Provider<LobbyController> lobbyController;
    private final PreferenceService preferenceService;
    private final CryptoService cryptoService;

    private Disposable disposable;
    private String username;
    private String password;

    @FXML public TextField usernameInput;

    @FXML public PasswordField passwordInput;

    @FXML public Button loginButton;

    @FXML public CheckBox rememberMeCheckBox;

    @FXML public Button registerButton;

    @FXML public Label usernameErrorLabel;

    @FXML public Label passwordErrorLabel;

    @Inject
    public LoginController(App app, LoginService loginService, Provider<RegisterController> registerController, Provider<LobbyController> lobbyController, PreferenceService preferenceService, CryptoService cryptoService){
        this.app = app;
        this.loginService = loginService;
        this.registerController = registerController;
        this.lobbyController = lobbyController;
        this.preferenceService = preferenceService;
        this.cryptoService = cryptoService;
    }

    @Override
    public void init(){
    }

    @Override
    public void destroy(){
        if (this.disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public Parent render(){
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/loginScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        }catch(IOException e) {
            e.printStackTrace();
            return null;
        }

        if (preferenceService.getRememberMe()){
            username = cryptoService.decode(preferenceService.getUsername());
            password = cryptoService.decode(preferenceService.getPassword());
            usernameInput.setText(username);
            passwordInput.setText(password);
            rememberMeCheckBox.fire();
        }

        return parent;
    }

    public void login(ActionEvent event) {
        String username = usernameInput.getText();
        String password = passwordInput.getText();

        Boolean usernameEmpty = username.isEmpty();
        Boolean passwordEmpty = password.isEmpty();

        //check whether username or password is empty
        if (usernameEmpty || passwordEmpty) {
            if (usernameEmpty) {
                usernameErrorLabel.setText("Invalid input.");
            }
            else {
                usernameErrorLabel.setText("");
            }
            if (passwordEmpty) {
                passwordErrorLabel.setText("Invalid input.");
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
                                //I dont know how to pass the user to the LobbyController yet
                                User user = new User(result._id(), result.name(), result.status(), result.avatar());

                                if (rememberMeCheckBox.isSelected()){
                                    preferenceService.setRememberMe(true);
                                    String encodedUsername = cryptoService.encode(username);
                                    String encodedPassword = cryptoService.encode(password);
                                    preferenceService.setUsername(encodedUsername);
                                    preferenceService.setPassword(encodedPassword);
                                }
                                else{
                                    preferenceService.setRememberMe(false);
                                    preferenceService.setUsername("");
                                    preferenceService.setPassword("");
                                }

                                final LobbyController controller = lobbyController.get();
                                app.show(controller);
                            },
                            error -> Platform.runLater(() -> {
                                this.createDialog(error.getMessage());
                            })
                    );
        }
    }

    private void createDialog(String message) {
        VBox vBox = new VBox(18);
        vBox.setAlignment(Pos.CENTER);

        Label label = new Label();
        label.setFont(new Font(18));
        label.setId("dialogLabel");

        double width;

        if (message.equals(HTTP_400)) {
            label.setText("Validation failed.");
            width = 300;
        }
        else if (message.equals(HTTP_401)) {
            label.setText("Invalid username or password.");
            width = 400;
        }
        else if (message.equals(HTTP_429)) {
            label.setText("Rate limit reached.");
            width = 540;
        }
        else {
            label.setText("No connection to the Server.");
            width = 300;
        }

        vBox.getChildren().add(label);

        app.showDialogWithOkButton(vBox, width);
    }

    public void toRegister(ActionEvent event) {
        String username = usernameInput.getText();
        String password = passwordInput.getText();
        final RegisterController controller = registerController.get();
        controller.username.set(username);
        controller.password.set(password);
        app.show(controller);
    }
}
