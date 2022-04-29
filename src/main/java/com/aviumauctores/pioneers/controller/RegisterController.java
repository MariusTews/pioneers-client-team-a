package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;

public class RegisterController implements Controller{

    private final UserService userService;
    private final Provider<LoginController> loginController;

    public final SimpleStringProperty username = new SimpleStringProperty();
    public final SimpleStringProperty password = new SimpleStringProperty();
    @Inject
    public RegisterController(UserService userService, Provider<LoginController> loginController){

        this.userService = userService;
        this.loginController = loginController;
    }
    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        //placeholder
        return new VBox();
    }
}
