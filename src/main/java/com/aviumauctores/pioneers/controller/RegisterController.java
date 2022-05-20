package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.error.ErrorResponse;
import com.aviumauctores.pioneers.service.ErrorService;
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
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

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
    private final ErrorService errorService;
    private final Provider<LoginController> loginController;
    private final ResourceBundle bundle;
    public TextField textfieldPassword_show;

    private Disposable disposable;

    private final HashMap<String, String> errorCodes = new HashMap<>();

    @Inject
    public RegisterController(App app, UserService userService, ErrorService errorService, Provider<LoginController> loginController, ResourceBundle bundle) {
        this.app = app;
        this.userService = userService;
        this.errorService = errorService;
        this.loginController = loginController;
        this.bundle = bundle;
    }

    @Override
    public void init() {
        errorCodes.put("400", bundle.getString("validation.failed"));
        errorCodes.put("409", bundle.getString("username.taken"));
        errorCodes.put("429", bundle.getString("limit.reached"));
    }

    @Override
    public void destroy() {
        if (this.disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/registerScreen.fxml"), bundle);
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

        //click enter to register, click esc to leave
        createAccountButton.setDefaultButton(true);
        leaveButton.setCancelButton(true);

        //only because of the test
        if (textfieldUsername.getText() == null) {
            textfieldUsername.setText("");
        }
        if (textfieldPassword.getText() == null) {
            textfieldPassword.setText("");
        }

        //disable createAccountButton when one or both textfields are empty
        createAccountButton.disableProperty().bind(
                Bindings.createBooleanBinding(() ->
                                textfieldUsername.getText().trim().isEmpty(), textfieldUsername.textProperty())
                        .or(Bindings.createBooleanBinding(() ->
                                textfieldPassword.getText().trim().isEmpty(), textfieldPassword.textProperty()))
        );

        return parent;
    }

    public void showPassword(ActionEvent event) {
        // toggle PasswordField and TextField with a button to show password
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
        // back to login
        final LoginController controller = loginController.get();
        app.show(controller);

    }

    public void createAccount(ActionEvent event) {
        // send username and password to server
        disposable = userService.register(textfieldUsername.getText(), textfieldPassword.getText())
                .observeOn(FX_SCHEDULER)
                .subscribe(
                        result -> {
                            final LoginController controller = loginController.get();
                            app.show(controller);
                        },
                        throwable -> {
                            if (throwable instanceof HttpException ex) {
                                ErrorResponse response = errorService.readErrorMessage(ex);
                                String message = errorCodes.get(Integer.toString(response.statusCode()));
                                Platform.runLater(() -> app.showHttpErrorDialog(response.statusCode(), response.error(), message));
                            } else {
                                app.showErrorDialog(bundle.getString("connection.failed"), bundle.getString("try.again"));
                            }
                        }

                );
    }
}
