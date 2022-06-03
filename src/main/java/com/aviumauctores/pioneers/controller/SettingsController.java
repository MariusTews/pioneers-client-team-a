package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.users.UpdateUserDto;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class SettingsController implements Controller {

    private final ResourceBundle bundle;

    private final App app;

    private final UserService userService;

    private final LoginService loginService;

    private final Provider<LobbyController> lobbyController;

    private User currentUser;

    @FXML public ImageView avatarView;
    @FXML public VBox changeWindowVBox;
    @FXML public Label changeWindowLabel;
    @FXML public PasswordField oldPasswordField;
    @FXML public TextField newParameterField;
    @FXML public PasswordField confirmField;
    @FXML public Button acceptChangesButton;
    @FXML public Button cancelChangesButton;
    @FXML public Label currentNameLabel;
    @FXML public Label currentPasswordLabel;
    @FXML public Button changeNameButton;
    @FXML public Button changePasswordButton;
    @FXML public Button changeAvatarButton;
    @FXML public Button leaveButton;


    @Inject
    public SettingsController(ResourceBundle bundle, App app, UserService userService, LoginService loginService, Provider<LobbyController> lobbyController) {
        this.bundle = bundle;
        this.app = app;
        this.userService = userService;
        this.loginService = loginService;
        this.lobbyController = lobbyController;
    }


    public void init() {
        userService.getUserByID(userService.getCurrentUserID()).observeOn(FX_SCHEDULER).subscribe(res -> {
            currentUser = res;
            currentNameLabel.setText(res.name());
            String avatarUrl = currentUser.avatar();
            Image avatar = avatarUrl == null ? null : new Image(avatarUrl);
            avatarView.setImage(avatar);
        });
    }


    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/settingsScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        changeNameButton.setOnAction(this::onPencilClicked);
        changePasswordButton.setOnAction(this::onPencilClicked);
        changeAvatarButton.setOnAction(this::onPencilClicked);
        return parent;
    }


    public void destroy(boolean closed) {

    }

    public void toLobby(ActionEvent event) {
        final LobbyController controller = lobbyController.get();
        app.show(controller);
    }

    public void onPencilClicked(ActionEvent event) {
        if (event.getSource().equals(changeNameButton)) {
            openWindow("name");
        }
        if (event.getSource().equals(changePasswordButton)) {
            openWindow("password");
        }
        if (event.getSource().equals(changeAvatarButton)) {
            openWindow("avatar");
        }
    }

    public void openWindow(String type_of_change) {
        if (type_of_change.equals("name")) {
            changeBackToTextField();
            acceptChangesButton.setOnAction(this::changeName);
            changeWindowLabel.setText("Nutzernamen ändern");
            newParameterField.setPromptText("neuen Nutzernamen angeben");
            confirmField.setDisable(true);
            confirmField.setVisible(false);
        }
        if (type_of_change.equals("password")) {
            changeToPasswordField();
            acceptChangesButton.setOnAction(this::changePassword);
            changeWindowLabel.setText("Passwort ändern");
            confirmField.setDisable(false);
            confirmField.setVisible(true);
            confirmField.setPromptText("neues Passwort bestätigen");
        }
        if (type_of_change.equals("avatar")) {
            changeBackToTextField();
            acceptChangesButton.setOnAction(this::changeAvatar);
            changeWindowLabel.setText("Avatar ändern");
            newParameterField.setPromptText("neuen Avatar angeben");
            confirmField.setDisable(true);
            confirmField.setVisible(false);
        }
        changeWindowVBox.setDisable(false);
        changeWindowVBox.setVisible(true);
    }

    public void onCancelButtonClicked(ActionEvent event) {
        closeWindow();
    }

    public void changeName(ActionEvent event) {
        //check the old password with a login
        String oldPassword = oldPasswordField.getText();
        loginService.checkPasswordLogin(currentUser.name(), oldPassword).observeOn(FX_SCHEDULER).subscribe(res -> {
            //this only happens, if the login was successful
            //change the username to the new Parameter
            String newName = newParameterField.getText();
            if (newName.isBlank()) {
                return;
            }
            userService.updateUser(currentUser._id(), new UpdateUserDto(newName, null, null, null, null)).observeOn(FX_SCHEDULER).subscribe();
            currentNameLabel.setText(newName);
            closeWindow();
        });
    }

    public void changePassword(ActionEvent event) {
        //check the old password with a login
        String oldPassword = oldPasswordField.getText();
        loginService.checkPasswordLogin(currentUser.name(), oldPassword).observeOn(FX_SCHEDULER).subscribe(res -> {
            //this only happens, if the login was successful
            //change the password to the new Parameter
            PasswordField newPasswordField = (PasswordField) changeWindowVBox.getChildren().get(2);
            String newPassword = newPasswordField.getText();
            String confirmNewPassword = confirmField.getText();
            if (!newPassword.equals(confirmNewPassword)) {
                return;
            }
            if (newPassword.isBlank()) {
                return;
            }
            userService.updateUser(currentUser._id(), new UpdateUserDto(null, null, null, newPassword, null)).observeOn(FX_SCHEDULER).subscribe();
            closeWindow();
        });
    }

    public void changeAvatar(ActionEvent event) {
        //check the old password with a login
        String oldPassword = oldPasswordField.getText();
        loginService.checkPasswordLogin(currentUser.name(), oldPassword).observeOn(FX_SCHEDULER).subscribe(res -> {
            //this only happens, if the login was successful
            //change the avatar to the new Parameter
            String avatarUrl = newParameterField.getText();
            Image avatar = avatarUrl == null ? null : new Image(avatarUrl);
            userService.updateUser(currentUser._id(), new UpdateUserDto(null, null, avatarUrl, null, null)).observeOn(FX_SCHEDULER).subscribe();
            avatarView.setImage(avatar);
            closeWindow();
        });
    }

    public void closeWindow() {
        oldPasswordField.clear();
        newParameterField.clear();
        confirmField.clear();
        changeWindowVBox.setDisable(true);
        changeWindowVBox.setVisible(false);
    }

    public void changeToPasswordField() {
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("neues Passwort angeben");
        changeWindowVBox.getChildren().remove(2);
        changeWindowVBox.getChildren().add(2, newPasswordField);
    }

    public void changeBackToTextField() {
        changeWindowVBox.getChildren().remove(2);
        changeWindowVBox.getChildren().add(2, newParameterField);
    }
}
