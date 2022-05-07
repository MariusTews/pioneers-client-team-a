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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.Objects;

import static com.aviumauctores.pioneers.Constants.*;

public class RegisterController implements Controller {
    private final App app;
    private final UserService userService;

    public final SimpleStringProperty username = new SimpleStringProperty();
    public final SimpleStringProperty password = new SimpleStringProperty();
    @FXML
    public TextField textfieldUsername;
    @FXML
    public Button createAccountButton;
    @FXML
    public Button leaveButton;
    @FXML
    public Button showPasswordButton;
    @FXML
    public PasswordField textfieldPassword;
    private final Provider<LoginController> loginController;
    public TextField textfieldPassword_show;
    public Label errorLabel;

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
        textfieldPassword.textProperty().bindBidirectional(password);

        //click enter to register
        createAccountButton.setDefaultButton(true);

        //only because of the test
        if (textfieldUsername.getText() == null) {
            textfieldUsername.setText("");
        }
        if (textfieldPassword.getText() == null) {
            textfieldPassword.setText("");
        }

        //disable accountErstellenButton when one or both textfields are empty
        createAccountButton.disableProperty().bind(
                Bindings.createBooleanBinding(() ->
                                textfieldUsername.getText().trim().isEmpty(), textfieldUsername.textProperty())
                        .or(Bindings.createBooleanBinding(() ->
                                textfieldPassword.getText().trim().isEmpty(), textfieldPassword.textProperty()))
        );

        return parent;
    }

    public void showPassword(ActionEvent event) {
        if (textfieldPassword.isVisible() && !Objects.equals(textfieldPassword.getText(), "")) {
            textfieldPassword.setVisible(false);
            textfieldPassword_show.setText(textfieldPassword.getText());
            textfieldPassword_show.setVisible(true);
        } else if (!textfieldPassword.isVisible()) {
            textfieldPassword_show.setVisible(false);
            textfieldPassword.setVisible(true);
        }
    }

    public void leave(ActionEvent event) {
        final LoginController controller = loginController.get();
        app.show(controller);

    }

    public void createAccount(ActionEvent event) {
        disposable = userService.register(textfieldUsername.getText(), textfieldPassword.getText())
                .observeOn(FX_SCHEDULER)
                .subscribe(
                        result -> {
                            errorLabel.setText("");
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
        label.setId("errorLabel");

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
        this.showErrorOnLoginDialog(vBox, width);
    }

    public void showErrorOnLoginDialog(VBox vBox, double width) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Fehler");
        Button button = new Button("OK");
        button.setFont(new Font(12));
        button.setPrefWidth(120);

        button.setOnAction(e ->
                dialogStage.close());
        vBox.getChildren().add(button);

        Scene dialogScene = new Scene(vBox, width, 130);
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }


}
