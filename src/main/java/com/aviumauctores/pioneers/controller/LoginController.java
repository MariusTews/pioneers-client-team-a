package com.aviumauctores.pioneers.controller;

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

    private final LoginService loginService;

    @FXML public TextField usernameInput;

    @FXML public PasswordField passwordInput;

    @FXML public Button loginButton;

    @FXML public CheckBox rememberMeCheckBox;

    @FXML public Button registerButton;


    public LoginController(LoginService loginService){
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

    }

    public void rememberMeToggle(ActionEvent event) {

    }

    public void toRegister(ActionEvent event) {

    }
}
