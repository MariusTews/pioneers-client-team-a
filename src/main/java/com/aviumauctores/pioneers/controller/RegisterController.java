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
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

public class RegisterController implements Controller{
    private final App app;
    private final UserService userService;

    public final SimpleStringProperty username = new SimpleStringProperty();
    public final SimpleStringProperty password = new SimpleStringProperty();
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
    private final Provider<LoginController> loginController;

    @Inject
    public RegisterController(App app, UserService userService, Provider<LoginController> loginController){
        this.app = app;
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
        textfieldUsername.textProperty().bindBidirectional(username);
        textfieldPasswort.textProperty().bindBidirectional(password);

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
        final LoginController controller = loginController.get();
        app.show(controller);

    }

    public void accountErstellen(ActionEvent event) {
        userService.register(textfieldUsername.getText(), textfieldPasswort.getText());
    }
}
