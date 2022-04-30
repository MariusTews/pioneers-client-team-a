package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.LoginService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

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
        if(usernameEmpty || passwordEmpty) {
            if(usernameEmpty) {
                usernameErrorLabel.setText("Keine valide Eingabe.");
            }
            else {
                usernameErrorLabel.setText("");
            }
            if(passwordEmpty) {
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
                    .subscribe(result -> {
                        final LobbyController controller = lobbyController.get();
                        app.show(controller);
                    });
        }
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
