package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.UserService;
import com.aviumauctores.pioneers.sounds.GameMusic;
import com.aviumauctores.pioneers.sounds.GameSounds;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

public class InGameController extends LoggedInController{
    private final App app;
    private final ResourceBundle bundle;

    @FXML public Label numSheepLabel;
    @FXML
    private ImageView soundImage;
    public Label numWoodLabel;
    public Label numBricksLabel;
    public Label numOreLabel;
    public Label numWheatLabel;
    public Button finishMoveButton;
    public Button rollButton;
    public Button leaveGameButton;
    public Label lastRollPlayerLabel;
    public Label lastRollLabel;


    GameMusic gameSound = new GameMusic(Objects.requireNonNull(Main.class.getResource("sounds/GameMusik.mp3")));

    // This are the Sound-Icons
    Image soundButtonImage = new Image(Objects.requireNonNull(Main.class.getResource("soundImages/mute.png")).toString());
    Image soundButtonImage2 = new Image(Objects.requireNonNull(Main.class.getResource("soundImages/unmute.png")).toString());


    @Inject
    public InGameController(App app, UserService userService, ResourceBundle bundle) {
        super(userService);
        this.app = app;
        this.bundle = bundle;
    }

    @Override
    public void init() {
        disposables = new CompositeDisposable();
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/ingameScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        soundImage.setImage(soundButtonImage);


        return parent;
    }

    public void finishMove(ActionEvent actionEvent) {

    }

    public void rollDice(ActionEvent actionEvent) {
     if(soundImage.getImage()==soundButtonImage){
         GameSounds diceSound =new GameSounds(Objects.requireNonNull(Main.class.getResource("sounds/Wuerfel.mp3")));
         diceSound.play();
     }
    }

    public void leaveGame(ActionEvent actionEvent) {

    }

   public void soundOnOff(MouseEvent mouseEvent) {
        if (gameSound.isRunning()){
            soundImage.setImage(soundButtonImage2);
            gameSound.pause();
        }else {
            soundImage.setImage(soundButtonImage);
            gameSound.play();

        }
    }
}
