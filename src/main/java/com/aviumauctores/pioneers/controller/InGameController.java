package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.GameMemberService;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class InGameController extends LoggedInController{
    private final App app;
    private final ResourceBundle bundle;
    private final GameMemberService gameMemberService;

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

    @FXML public Circle vp01;
    @FXML public Circle vp02;
    @FXML public Circle vp03;
    @FXML public Circle vp04;
    @FXML public Circle vp05;
    @FXML public Circle vp06;
    @FXML public Circle vp07;
    @FXML public Circle vp08;
    @FXML public Circle vp09;
    @FXML public Circle vp10;

    public int memberVP;

    GameMusic gameSound = new GameMusic(Objects.requireNonNull(Main.class.getResource("sounds/GameMusik.mp3")));

    // These are the Sound-Icons
    Image soundButtonImage = new Image(Objects.requireNonNull(Main.class.getResource("soundImages/mute.png")).toString());
    Image soundButtonImage2 = new Image(Objects.requireNonNull(Main.class.getResource("soundImages/unmute.png")).toString());


    @Inject
    public InGameController(App app, UserService userService, ResourceBundle bundle, GameMemberService gameMemberService) {
        super(userService);
        this.app = app;
        this.bundle = bundle;
        this.gameMemberService = gameMemberService;
    }

    @Override
    public void init() {
        disposables = new CompositeDisposable();
        memberVP = 0;
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
        disposables.add(gameMemberService.getMember(userService.getCurrentUserID())
                .observeOn(FX_SCHEDULER)
                .subscribe(member -> {
                    String colourString = "-fx-background-color: #" + member.color().toString().substring(2,8);
                    rollButton.setStyle(colourString);
                    leaveGameButton.setStyle(colourString);
                    finishMoveButton.setStyle(colourString);
                }));
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

    public void changeVP(int vpGain) {
        memberVP += vpGain;
        switch (memberVP) {
            case 1:
                vp01.setFill(Color.GOLD);
            case 2:
                vp02.setFill(Color.GOLD);
            case 3:
                vp03.setFill(Color.GOLD);
            case 4:
                vp04.setFill(Color.GOLD);
            case 5:
                vp05.setFill(Color.GOLD);
            case 6:
                vp06.setFill(Color.GOLD);
            case 7:
                vp07.setFill(Color.GOLD);
            case 8:
                vp08.setFill(Color.GOLD);
            case 9:
                vp09.setFill(Color.GOLD);
            case 10:
                vp10.setFill(Color.GOLD);
        }
    }
}
