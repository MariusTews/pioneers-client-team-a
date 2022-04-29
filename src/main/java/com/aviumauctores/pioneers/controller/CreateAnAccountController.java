package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.UserService;
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

import java.io.IOException;

public class CreateAnAccountController implements Controller {

    private final App app;
    private final UserService userService;
    private final LoginService loginService;

    public final SimpleStringProperty username = new SimpleStringProperty();
    public final SimpleStringProperty passwort = new SimpleStringProperty();
    @FXML
    public TextField textfieldUsername;
    @FXML
    public Button accountErstellenButton;
    @FXML
    public Button verlassenButton;
    @FXML
    public Button anzeigenButton;
    @FXML
    public PasswordField textfieldPasswort;
    public Label showPassword;

    public CreateAnAccountController(App app, UserService userService, LoginService loginService) {
        this.app = app;
        this.userService = userService;
        this.loginService = loginService;
    }

    public void init() {

    }

    public void destroy() {

    }

    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/registerScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //disable accountErstellenButton when one or both textfields are empty
        accountErstellenButton.disableProperty().bind(
                Bindings.createBooleanBinding(() ->
                                textfieldUsername.getText().trim().isEmpty(), textfieldUsername.textProperty())
                        .or(Bindings.createBooleanBinding(() ->
                                textfieldPasswort.getText().trim().isEmpty(), textfieldPasswort.textProperty()))
        );

        //take username and password from login screen
        //textfieldUsername.textProperty().bindBidirectional(username);
        //textfieldPasswort.textProperty().bindBidirectional(passwort);

        return parent;
    }

    public void anzeigen(ActionEvent event) {
        if(showPassword.getText().isEmpty()){
            String input = textfieldPasswort.getText();
            showPassword.setText("Ihr Passwort: "+ input);
        }
        else {
            showPassword.setText("");
        }
    }

    public void verlassen(ActionEvent event) {
        app.show(new LoginController(loginService));
    }

    public void accountErstellen(ActionEvent event) {
        userService.register(textfieldUsername.getText(), textfieldPasswort.getText());
    }
}
