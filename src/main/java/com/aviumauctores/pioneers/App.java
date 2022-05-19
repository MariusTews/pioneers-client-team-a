package com.aviumauctores.pioneers;

import com.aviumauctores.pioneers.controller.Controller;
import com.aviumauctores.pioneers.controller.LoginController;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static com.aviumauctores.pioneers.Constants.*;

public class App extends Application {

    private Stage stage;
    private Controller controller;

    private Disposable disposable;

    public App() {
        final MainComponent mainComponent = DaggerMainComponent.builder().mainApp(this).build();
        controller = mainComponent.loginController();
    }

    public App(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void start(Stage primaryStage) {

        this.stage = primaryStage;
        stage.setWidth(SCREEN_WIDTH);
        stage.setHeight(SCREEN_HEIGHT);
        stage.setTitle(GAME_TITLE);

        //The label in the following line has to be replaced with an fxml-file in order to show the right screen
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("views/loadingScreen.fxml")));
            final Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        stage.getScene().getStylesheets().add(Objects.requireNonNull(Main.class.getResource("views/light-theme.css")).toString());

        setAppIcon(stage);
        setTaskBarIcon();

        primaryStage.show();

        if (controller != null) {
            show(controller);
        }

    }

    public void letterbox(final Pane contentPane) {
        final double initWidth = stage.getScene().getWidth();
        final double initHeight = stage.getScene().getHeight();
        final double ratio = initWidth / initHeight;



        SceneSizeChangeListener sizeListener = new SceneSizeChangeListener(stage.getScene(), ratio, initHeight, initWidth, contentPane);
        stage.getScene().widthProperty().addListener(sizeListener);
        stage.getScene().heightProperty().addListener(sizeListener);
    }




    public void setWindow(Pane contentPane) {
        final double newWidth = stage.getScene().getWidth();
        final double newHeight = stage.getScene().getHeight();
        final int ratio = SCREEN_WIDTH/SCREEN_HEIGHT;

        double scaleFactor = newWidth / newHeight > ratio ? newHeight / SCREEN_HEIGHT : newWidth / SCREEN_WIDTH;
        if (scaleFactor >= 1) {
            Scale scale = new Scale(scaleFactor, scaleFactor);
            scale.setPivotX(0);
            scale.setPivotY(0);
            stage.getScene().getRoot().getTransforms().setAll(scale);

            contentPane.setPrefWidth(newWidth / scaleFactor);
            contentPane.setPrefHeight(newHeight / scaleFactor);
        } else {
            contentPane.setPrefWidth(Math.max(SCREEN_WIDTH, newWidth));
            contentPane.setPrefHeight(Math.max(SCREEN_HEIGHT, newHeight));
        }


    }

    public record SceneSizeChangeListener(Scene scene, double ratio, double initHeight, double initWidth,
                                          Pane contentPane) implements ChangeListener<Number> {

        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
            final double newWidth = scene.getWidth();
            final double newHeight = scene.getHeight();

            double scaleFactor = newWidth / newHeight > ratio ? newHeight / initHeight : newWidth / initWidth;

            if (scaleFactor >= 1) {
                Scale scale = new Scale(scaleFactor, scaleFactor);
                scale.setPivotX(0);
                scale.setPivotY(0);
                scene.getRoot().getTransforms().setAll(scale);

                contentPane.setPrefWidth(newWidth / scaleFactor);
                contentPane.setPrefHeight(newHeight / scaleFactor);
            } else {

                contentPane.setPrefWidth(Math.max(initWidth, newWidth));
                contentPane.setPrefHeight(Math.max(initHeight, newHeight));
            }
        }
    }

    private void setAppIcon(Stage stage) {
        final Image image = new Image(Objects.requireNonNull(getClass().getResource("settlement.png")).toString());
        stage.getIcons().add(image);
    }

    private void setTaskBarIcon() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        try {
            final Taskbar taskbar = Taskbar.getTaskbar();
            final java.awt.Image image = ImageIO.read(Objects.requireNonNull(Main.class.getResource("settlement.png")));
            taskbar.setIconImage(image);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void stop() {
        cleanup();
    }

    public void show(Controller controller) {
        cleanup();
        this.controller = controller;

        /*if controller is a logincontroller and remember me is enabled then try to login with the refresh token
        if this is not implemented here but in the logincontroller, then the login controller will show shortly
        before a successful token login leads to the lobby screen*/
        if((controller instanceof LoginController loginController) && loginController.getRememberMeStatus()){
            disposable = loginController.tryTokenLogin().subscribeOn(FX_SCHEDULER)
                    .subscribe(
                            //on success show the lobby screen (token login was successful)
                            loginController::toLobby,
                            //on error show the login screen (token login was not successful)
                            error -> {
                                loginController.init();
                                stage.getScene().setRoot(loginController.render());
                                Pane root = (Pane) stage.getScene().getRoot();
                                if(stage.isMaximized()){
                                    this.setWindow(root);
                                    this.letterbox(root);
                                } else{
                                    this.setWindow(root);
                                    this.letterbox(root);

                                }
                            }
                    );
        }
        //if controller is not a logincontroller or remember me is not set then do a normal controller init and render
        else {
            controller.init();
            stage.getScene().setRoot(controller.render());
            Pane root = (Pane) stage.getScene().getRoot();
            if(stage.isMaximized()){
                this.setWindow(root);
                this.letterbox(root);
            } else{
                this.setWindow(root);
                this.letterbox(root);

            }

        }

    }

    public void setTheme(String theme) {
        if (theme.equals("light")) {
            stage.getScene().getStylesheets().clear();
            stage.getScene().getStylesheets().add(Objects.requireNonNull(Main.class.getResource("views/light-theme.css")).toString());
        } else if (theme.equals("dark")) {
            stage.getScene().getStylesheets().clear();
            stage.getScene().getStylesheets().add(Objects.requireNonNull(Main.class.getResource("views/dark-theme.css")).toString());
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public Optional<ButtonType> showHttpErrorDialog(int code, String error, String message) {
        String header = code + " " + error;
        String content = message;
        content = content != null ? content : "";
        return showErrorDialog(header, content);
    }

    @SuppressWarnings("UnusedReturnValue")
    public Optional<ButtonType> showErrorDialog(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        setAppIcon((Stage) alert.getDialogPane().getScene().getWindow());
        return alert.showAndWait();
    }

    public void showDialogWithOkButton(VBox vBox, double width) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Error");
        setAppIcon(dialogStage);

        Button button = new Button("OK");
        button.setFont(new Font(12));
        button.setPrefWidth(120);
        button.setOnAction(e -> dialogStage.close());

        vBox.getChildren().add(button);

        Scene dialogScene = new Scene(vBox, width, 130);
        dialogStage.setScene(dialogScene);

        dialogStage.show();
    }

    private void cleanup() {

        if (this.disposable != null) {
            disposable.dispose();
        }

        if (controller != null) {
            controller.destroy();
            controller = null;
        }
    }
}
