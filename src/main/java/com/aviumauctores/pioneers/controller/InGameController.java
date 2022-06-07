package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Building;
import com.aviumauctores.pioneers.service.GameMemberService;
import com.aviumauctores.pioneers.service.UserService;
import com.aviumauctores.pioneers.sounds.GameMusic;
import com.aviumauctores.pioneers.sounds.GameSounds;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

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
    @FXML public Pane mainPane;
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

    private BuildMenuController buildMenuController;
    private Parent buildMenu;

    GameMusic gameSound;

    // These are the Sound-Icons
    Image muteImage;
    Image unmuteImage;


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

        // Initialize these objects here because else the tests would fail
        gameSound = new GameMusic(Objects.requireNonNull(Main.class.getResource("sounds/GameMusik.mp3")));
        muteImage = new Image(Objects.requireNonNull(Main.class.getResource("soundImages/mute.png")).toString());
        unmuteImage = new Image(Objects.requireNonNull(Main.class.getResource("soundImages/unmute.png")).toString());
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
        soundImage.setImage(muteImage);


        return parent;
    }

    @Override
    public void destroy(boolean closed) {
        super.destroy(closed);
        closeBuildMenu(closed);
    }

    public void finishMove(ActionEvent actionEvent) {

    }

    public void rollDice(ActionEvent actionEvent) {
     if(soundImage.getImage()==muteImage){
         GameSounds diceSound =new GameSounds(Objects.requireNonNull(Main.class.getResource("sounds/Wuerfel.mp3")));
         diceSound.play();
     }
    }

    public void onFieldClicked(MouseEvent mouseEvent) {
        if (!(mouseEvent.getSource() instanceof Node source)) {
            return;
        }
        closeBuildMenu(false);
        Building coordinateHolder = Building.readCoordinatesFromID(source.getId());
        if (coordinateHolder == null) {
            return;
        }

        int side = coordinateHolder.side();
        String buildingType;
        if (side == 0 || side == 6) {
            // TODO Check for city
            buildingType = "settlement";
        } else {
            buildingType = "road";
        }
        buildMenuController = new BuildMenuController(bundle, buildingType);
        buildMenu = buildMenuController.render();
        buildMenu.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
            buildMenu.setLayoutX(Math.min(source.getLayoutX(), mainPane.getWidth() - newValue.getWidth()));
            buildMenu.setLayoutY(Math.min(source.getLayoutY(), mainPane.getHeight() - newValue.getHeight()));
        });
        mainPane.getChildren().add(buildMenu);
        // Prevent the event handler from main pane to close the build menu immediately after this
        mouseEvent.consume();
    }

    private void closeBuildMenu(boolean appClosed) {
        if (buildMenuController != null) {
            buildMenuController.destroy(appClosed);
            buildMenuController = null;
        }
        if (buildMenu != null) {
            mainPane.getChildren().remove(buildMenu);
            buildMenu = null;
        }
    }

    public void onMainPaneClicked(MouseEvent mouseEvent) {
        closeBuildMenu(false);
    }

    public void leaveGame(ActionEvent actionEvent) {


    }

   public void soundOnOff(MouseEvent mouseEvent) {
        if (gameSound.isRunning()){
            soundImage.setImage(unmuteImage);
            gameSound.pause();
        }else {
            soundImage.setImage(muteImage);
            gameSound.play();

        }
    }
}
