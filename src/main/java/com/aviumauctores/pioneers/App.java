package com.aviumauctores.pioneers;

import com.aviumauctores.pioneers.controller.Controller;
import com.aviumauctores.pioneers.controller.LoginController;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.UserService;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;


import static com.aviumauctores.pioneers.Constants.*;

public class App extends Application {

    private Stage stage;
    private Stage popupStage;
    private Scene popupScene;
    private Controller controller;

    @Override
    public void start(Stage primaryStage){
        final MainComponent mainComponent = DaggerMainComponent.builder().mainApp(this).build();

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

        show(mainComponent.loginController());
    }

    private void setAppIcon(Stage stage){
        final Image image = new Image(getClass().getResource("settlement.png").toString());
        stage.getIcons().add(image);
    }

    private void setTaskBarIcon(){
        if(GraphicsEnvironment.isHeadless()){
            return;
        }
        try{
            final Taskbar taskbar = Taskbar.getTaskbar();
            final java.awt.Image image = ImageIO.read(Main.class.getResource("settlement.png"));
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

    public void showErrorOnLoginPopup(Throwable error) {
        popupStage = new Stage();
        popupStage.setTitle("Fehler");
        setAppIcon(popupStage);

        VBox vBox = new VBox(18);
        vBox.setAlignment(Pos.CENTER);

        Label label = new Label();
        label.setFont(new Font(18));

        Button button = new Button("OK");
        button.setFont(new Font(12));
        button.setPrefWidth(120);
        button.setOnAction(e ->
        {
            popupStage.close();
        });

        vBox.getChildren().add(label);
        vBox.getChildren().add(button);

        int width;

        if (error.getMessage().equals(HTTP_400)) {
            label.setText("Validierung fehlgeschlagen.");
            width = 300;
        }
        else if (error.getMessage().equals(HTTP_401)) {
            label.setText("Falscher Benutzername oder falsches Passwort.");
            width = 400;
        }
        else if (error.getMessage().equals(HTTP_429)) {
            label.setText("Bitte warten Sie einen Moment und versuchen es dann erneut.");
            width = 540;
        }
        else {
            label.setText("Keine Verbindung zum Server.");
            width = 300;
        }

        popupScene = new Scene(vBox, width, 130);
        popupStage.setScene(popupScene);

        popupStage.show();
    }

    private void cleanup(){
        if(controller != null){
            controller.destroy();
            controller = null;
        }
    }
}
