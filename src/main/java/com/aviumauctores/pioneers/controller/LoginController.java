package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.LoginService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LoginController implements Controller {

    private final App app;
    private final LoginService loginService;

    @FXML public TextField usernameInput;

    @FXML public PasswordField passwordInput;

    @FXML public Button loginButton;

    @FXML public CheckBox rememberMeCheckBox;

    @FXML public Button registerButton;

    @FXML public Label usernameErrorLabel;

    @FXML public Label passwordErrorLabel;


    public LoginController(App app, LoginService loginService){
        this.app = app;
        this.loginService = loginService;
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
        loginService.login(username, password);

        //maybe placeholder (rest of this method; can be moved to loginService later maybe)
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
            final LobbyController controller = new LobbyController(app);
            app.show(controller);
        }
    }

    public void rememberMeToggle(ActionEvent event) {

    }

    public void toRegister(ActionEvent event) {
        //maybe placeholder
        final RegisterController controller = new RegisterController();
        app.show(controller);
    }
}
