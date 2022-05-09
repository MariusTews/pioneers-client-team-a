package com.aviumauctores.pioneers;

import com.aviumauctores.pioneers.controller.Controller;
import com.aviumauctores.pioneers.dto.error.ErrorResponse;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Objects;

import java.util.Optional;

import static com.aviumauctores.pioneers.Constants.*;

public class App extends Application {

    private Stage stage;
    private Controller controller;

    public App(){
        final MainComponent mainComponent = DaggerMainComponent.builder().mainApp(this).build();
        controller = mainComponent.loginController();
    }

    public App(Controller controller){
        this.controller = controller;
    }

    @Override
    public void start(Stage primaryStage){

        this.stage = primaryStage;
        stage.setWidth(SCREEN_WIDTH);
        stage.setHeight(SCREEN_HEIGHT);
        stage.setTitle(GAME_TITLE);

        //The label in the following line has to be replaced with an fxml-file in order to show the right screen
        final Scene scene = new Scene(new Label("Loading..."));
        stage.setScene(scene);

        setAppIcon(stage);
        setTaskBarIcon();

        primaryStage.show();

        if (controller != null){
            show(controller);
        }

    }

    private void setAppIcon(Stage stage){
        final Image image = new Image(Objects.requireNonNull(getClass().getResource("settlement.png")).toString());
        stage.getIcons().add(image);
    }

    private void setTaskBarIcon(){
        if(GraphicsEnvironment.isHeadless()){
            return;
        }
        try{
            final Taskbar taskbar = Taskbar.getTaskbar();
            final java.awt.Image image = ImageIO.read(Objects.requireNonNull(Main.class.getResource("settlement.png")));
            taskbar.setIconImage(image);
        }catch (Exception ignored){

        }
    }
    @Override
    public void stop(){
        cleanup();
    }

    public void show(Controller controller){
        cleanup();
        this.controller = controller;
        controller.init();
        stage.getScene().setRoot(controller.render());

    }

    @SuppressWarnings("UnusedReturnValue")
    public Optional<ButtonType> showHttpErrorDialog(ErrorResponse errorResponse) {
        String header = errorResponse.statusCode() + " " + errorResponse.error();
        String content = errorResponse.message();
        content = content != null ? content : "";
        return showErrorDialog(header, content);
    }

    @SuppressWarnings("UnusedReturnValue")
    public Optional<ButtonType> showConnectionFailedDialog() {
        return showErrorDialog("Connection failed", "Try later again");
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

    private void cleanup(){
        if(controller != null){
            controller.destroy();
            controller = null;
        }
    }
}
