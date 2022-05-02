package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.LoginService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static com.aviumauctores.pioneers.Constants.*;
import static com.aviumauctores.pioneers.Constants.HTTP_429;

public class LoginController implements Controller {

    private final App app;
    private final LoginService loginService;
    private final Provider<RegisterController> registerController;
    private final Provider<LobbyController> lobbyController;

    @FXML public TextField usernameInput;

    @FXML public PasswordField passwordInput;

    @FXML public Button loginButton;

    @FXML public CheckBox rememberMeCheckBox;

    @FXML public Button registerButton;

    @FXML public Label usernameErrorLabel;

    @FXML public Label passwordErrorLabel;

    @Inject
    public LoginController(App app, LoginService loginService, Provider<RegisterController> registerController, Provider<LobbyController> lobbyController){
        this.app = app;
        this.loginService = loginService;
        this.registerController = registerController;
        this.lobbyController = lobbyController;
    }

    @Override
    public void init(){

    }

    @Override
    public void destroy(){

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
                usernameErrorLabel.setText("Keine valide Eingabe.");
            }
            else {
                usernameErrorLabel.setText("");
            }
            if (passwordEmpty) {
                passwordErrorLabel.setText("Keine valide Eingabe.");
            }
            else {
                passwordErrorLabel.setText("");
            }
        }
        else {
            usernameErrorLabel.setText("");
            passwordErrorLabel.setText("");

            loginService.login(username, password)
                    .subscribeOn(FX_SCHEDULER)
                    .subscribe(
                            result -> {
                                //I dont know how to pass the user to the LobbyController yet
                                User user = new User(result._id(), result.name(), result.status(), result.avatar());
                                final LobbyController controller = lobbyController.get();
                                app.show(controller);
                            },
                            error -> {
                                Platform.runLater(() -> {
                                    this.createDialog(error.getMessage());
                                });
                            }
                    );
        }
    }

    private void createDialog(String message) {
        VBox vBox = new VBox(18);
        vBox.setAlignment(Pos.CENTER);

        Label label = new Label();
        label.setFont(new Font(18));

        double width;

        if (message.equals(HTTP_400)) {
            label.setText("Validierung fehlgeschlagen.");
            width = 300;
        }
        else if (message.equals(HTTP_401)) {
            label.setText("Falscher Benutzername oder falsches Passwort.");
            width = 400;
        }
        else if (message.equals(HTTP_429)) {
            label.setText("Bitte warten Sie einen Moment und versuchen es dann erneut.");
            width = 540;
        }
        else {
            label.setText("Keine Verbindung zum Server.");
            width = 300;
        }

        vBox.getChildren().add(label);

        app.showErrorOnLoginDialog(vBox, width);
    }

    public void rememberMeToggle(ActionEvent event) {

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
