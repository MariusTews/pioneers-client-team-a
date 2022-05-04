package com.aviumauctores.pioneers;

import com.aviumauctores.pioneers.controller.Controller;
import javafx.application.Application;
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
    private Stage dialogStage;
    private Scene dialogScene;
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

    public void showErrorOnLoginDialog(VBox vBox, double width) {
        dialogStage = new Stage();
        dialogStage.setTitle("Fehler");
        setAppIcon(dialogStage);

        Button button = new Button("OK");
        button.setFont(new Font(12));
        button.setPrefWidth(120);
        button.setOnAction(e ->
        {
            dialogStage.close();
        });

        vBox.getChildren().add(button);

        dialogScene = new Scene(vBox, width, 130);
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
