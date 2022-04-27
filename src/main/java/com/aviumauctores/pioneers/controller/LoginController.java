package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.service.LoginService;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.awt.*;

public class LoginController implements Controller {

    private final LoginService loginService;

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
        final Label usernameLabel = new Label("Username");
        usernameLabel.setId("usernameLabel");
        final TextField usernameInput = new TextField();
        final Label passwordLabel = new Label("Password");
        final PasswordField passwordInput = new PasswordField();
        final Button login = new Button("Login");
        /*login.setOnAction(event -> {
            loginService.login(usernameInput.getText(), passwordInput.getText());
        });*/
        return null;
        //return new VBox(5, usernameLabel, usernameInput, passwordLabel, passwordInput, login);
        //Macht er in Vorlesung 3 ab 1:36:00
    }

    public void login(String username, String password){
        loginService.login(username, password);
    }
}
