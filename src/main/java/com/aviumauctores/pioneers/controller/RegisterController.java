package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.UserService;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static com.aviumauctores.pioneers.Constants.*;

public class RegisterController implements Controller {
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

    private Disposable disposable;

    @Inject
    public RegisterController(App app, UserService userService, Provider<LoginController> loginController) {
        this.app = app;
        this.userService = userService;
        this.loginController = loginController;
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
        if (this.disposable != null) {
            disposable.dispose();
        }
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

        //take username and password from login screen
        textfieldUsername.textProperty().bindBidirectional(username);
        textfieldPasswort.textProperty().bindBidirectional(password);

        //only because of the test
        if (textfieldUsername.getText() == null) {
            textfieldUsername.setText("");
        }
        if (textfieldPasswort.getText() == null) {
            textfieldPasswort.setText("");
        }
        //disable accountErstellenButton when one or both textfields are empty
        accountErstellenButton.disableProperty().bind(
                Bindings.createBooleanBinding(() ->
                                textfieldUsername.getText().trim().isEmpty(), textfieldUsername.textProperty())
                        .or(Bindings.createBooleanBinding(() ->
                                textfieldPasswort.getText().trim().isEmpty(), textfieldPasswort.textProperty()))
        );

        return parent;
    }

    public void showPassword(ActionEvent event) {
        //TODO make prettier
        if (showPassword.getText().isEmpty()) {
            String input = textfieldPasswort.getText();
            showPassword.setText("Ihr Passwort: " + input);
        } else {
            showPassword.setText("");
        }
    }

    public void leave(ActionEvent event) {
        final LoginController controller = loginController.get();
        app.show(controller);

    }

    public void createAccount(ActionEvent event) {
        //TODO fix closed application runs in console after server communication
        disposable = userService.register(textfieldUsername.getText(), textfieldPasswort.getText())
                .observeOn(FX_SCHEDULER)
                .subscribe(
                        result -> {
                            final LoginController controller = loginController.get();
                            app.show(controller);
                        },
                        error -> Platform.runLater(() -> this.createDialog(error.getMessage()))

                );
    }

    private void createDialog(String message) {
        VBox vBox = new VBox(18);
        vBox.setAlignment(Pos.CENTER);

        Label label = new Label();
        label.setFont(new Font(14));

        double width;

        switch (message) {
            case HTTP_400 -> {
                label.setText("Validierung fehlgeschlagen. (Passwort zu kurz)");
                width = 300;
            }
            case HTTP_409 -> {
                label.setText("Username schon vergeben");
                width = 400;
            }
            case HTTP_429 -> {
                label.setText("Bitte warten Sie einen Moment und versuchen es dann erneut.");
                width = 540;
            }
            default -> {
                label.setText("Keine Verbindung zum Server.");
                width = 300;
            }
        }

        vBox.getChildren().add(label);

        app.showErrorOnLoginDialog(vBox, width);
    }



}
