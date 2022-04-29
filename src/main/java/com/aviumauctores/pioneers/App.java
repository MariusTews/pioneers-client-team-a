package com.aviumauctores.pioneers;

import com.aviumauctores.pioneers.controller.Controller;
import com.aviumauctores.pioneers.controller.CreateAnAccountController;
import com.aviumauctores.pioneers.controller.LoginController;
import com.aviumauctores.pioneers.rest.UsersApiService;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.UserService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;


import static com.aviumauctores.pioneers.Constants.*;

public class App extends Application {

    private Stage stage;
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.stage = primaryStage;
        stage.setWidth(SCREEN_HEIGHT);
        stage.setHeight(SCREEN_HEIGHT);
        stage.setTitle(GAME_TITLE);

        //The label in the following line has to be replaced with an fxml-file in order to show the right screen
        final Scene scene = new Scene(new Label("Loading..."));
        stage.setScene(scene);

        setAppIcon(stage);
        setTaskBarIcon();

        primaryStage.show();

        //final LoginService loginService = new LoginService();
        //show(new LoginController(loginService));
        UserService userservice = new UserService();
        LoginService loginService = new LoginService();
        show(new CreateAnAccountController(this,userservice, loginService));
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
    public void stop() throws Exception{
        cleanup();
    }

    public void show(Controller controller){
        cleanup();
        this.controller = controller;
        controller.init();
        stage.getScene().setRoot(controller.render());

    }

    private void cleanup(){
        if(controller != null){
            controller.destroy();
            controller = null;
        }
    }
}
