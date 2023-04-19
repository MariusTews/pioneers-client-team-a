package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Constants;
import com.aviumauctores.pioneers.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ResourceBundle;


public class AddressInputScreenController implements Controller{

    private final App app;
    private final ResourceBundle bundle;
    private final Provider<LoginController> loginController;
    public TextField addressTextField;

    @Inject
    public AddressInputScreenController(App app, ResourceBundle bundle, Provider<LoginController> loginController) {
        this.app = app;
        this.bundle = bundle;
        this.loginController = loginController;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy(boolean closed) {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/addressInputScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return parent;
    }


    public void setAdress(ActionEvent event) {
        Constants.setURL(addressTextField.getText());
        this.app.show(this.loginController.get());
    }
}
