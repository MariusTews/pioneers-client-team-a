package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Building;
import com.aviumauctores.pioneers.model.Player;
import com.aviumauctores.pioneers.model.State;
import com.aviumauctores.pioneers.service.*;
import com.aviumauctores.pioneers.sounds.GameMusic;
import com.aviumauctores.pioneers.sounds.GameSounds;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class InGameController extends LoggedInController {
    private final App app;
    private final ResourceBundle bundle;
    private final PlayerResourceListController playerResourceListController;
    private final GameMemberService gameMemberService;
    private final GameService gameService;
    private final PioneerService pioneerService;
    private final SoundService soundService;
    private final HashMap<Player, Player> moveOrder = new HashMap<>();
    private Player player;

    @FXML
    public Label numSheepLabel;
    @FXML public Pane mainPane;
    @FXML
    private ImageView soundImage;
    @FXML
    public VBox insertChat;
    public Label numWoodLabel;
    public Label numBricksLabel;
    public Label numOreLabel;
    public Label numWheatLabel;
    public Button finishMoveButton;
    public Button rollButton;
    public Button leaveGameButton;
    public Label lastRollPlayerLabel;
    public Label lastRollLabel;
    @FXML
    public VBox playerList;
    private String currentPlayerID;
    private String userID;

    private final Provider<InGameChatController> inGameChatController;
    private final Provider<GameReadyController> gameReadyController;

    @FXML
    private Slider soundSlider;

    @FXML
    public Circle vp01;
    @FXML
    public Circle vp02;
    @FXML
    public Circle vp03;
    @FXML
    public Circle vp04;
    @FXML
    public Circle vp05;
    @FXML
    public Circle vp06;
    @FXML
    public Circle vp07;
    @FXML
    public Circle vp08;
    @FXML
    public Circle vp09;
    @FXML
    public Circle vp10;

    public int memberVP;

    private BuildMenuController buildMenuController;
    private Parent buildMenu;

    GameMusic gameSound;

    // These are the Sound-Icons
    Image muteImage;
    Image unmuteImage;
    private final EventListener eventListener;

    @Inject
    public InGameController(App app, UserService userService, ResourceBundle bundle, PlayerResourceListController playerResourceListController,
                            GameMemberService gameMemberService, GameService gameService, PioneerService pioneerService,
                            SoundService soundService,
                            EventListener eventListener, Provider<GameReadyController> gameReadyController, Provider<InGameChatController> inGameChatController) {
        super(userService);
        this.app = app;
        this.bundle = bundle;
        this.playerResourceListController = playerResourceListController;
        this.gameMemberService = gameMemberService;
        this.soundService = soundService;
        this.gameReadyController = gameReadyController;
        this.inGameChatController = inGameChatController;

        this.gameService = gameService;
        this.pioneerService = pioneerService;
        this.eventListener = eventListener;
    }



    @Override
    public void init() {
        disposables = new CompositeDisposable();
        memberVP = 0;

        // Initialize these objects here because else the tests would fail
        userID = userService.getCurrentUserID();
        player = pioneerService.getPlayer(userID).blockingFirst();

        gameSound = soundService.createGameMusic(Objects.requireNonNull(Main.class.getResource("sounds/GameMusik.mp3")));
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

        disposables.add(gameMemberService.getMember(userID)
                .observeOn(FX_SCHEDULER)
                .subscribe(member -> {
                    String colourString = "-fx-background-color: #" + member.color().toString().substring(2, 8);
                    rollButton.setStyle(colourString);
                    leaveGameButton.setStyle(colourString);
                    finishMoveButton.setStyle(colourString);
                }));
        this.soundImage.setImage(muteImage);

        disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".state.*", State.class)
                        .observeOn(FX_SCHEDULER)
                                .subscribe(state -> {
                                    String oldPlayer = currentPlayerID;
                                    currentPlayerID = state.data().expectedMoves().get(0).players().get(0);
                                    String action = state.data().expectedMoves().get(0).action();
                                    if (!currentPlayerID.equals(oldPlayer)) {
                                        playerResourceListController.hideArrow(pioneerService.getPlayer(oldPlayer).blockingFirst());
                                        playerResourceListController.showArrow(pioneerService.getPlayer(currentPlayerID).blockingFirst());
                                    }
                                    if(currentPlayerID.equals(userID)){

                                        if(action.endsWith("roll")){
                                            rollButton.setDisable(false);
                                        }
                                        if( rollButton.disabledProperty().get() || action.startsWith("build")){
                                            finishMoveButton.setDisable(false);
                                        }
                                    }else{
                                        finishMoveButton.setDisable(true);
                                        rollButton.setDisable(true);
                                    }
                                    playerResourceListController.updateResourceList();
                                    updateOwnResources();
                                }));

        this.currentPlayerID = pioneerService.getState().blockingFirst().expectedMoves().get(0).players().get(0);
        soundImage.setImage(muteImage);
        loadChat();
        playerResourceListController.init(playerList, currentPlayerID);
        updateOwnResources();
        finishMoveButton.setDisable(true);

        return parent;
    }

    @Override
    public void destroy(boolean closed) {
        super.destroy(closed);
        closeBuildMenu(closed);
    }

    public void finishMove(ActionEvent actionEvent) {
        pioneerService.createMove("build", null)
                .observeOn(FX_SCHEDULER)
                .subscribe();
    }


    public void loadChat() {
        InGameChatController controller = inGameChatController.get();
        controller.init();
        controller.setInGameController(this);
        insertChat.getChildren().add(controller.render());

    }

    public void rollDice(ActionEvent actionEvent) {
        if (soundImage.getImage() == muteImage) {
            GameSounds diceSound = soundService
                    .createGameSounds(Objects.requireNonNull(Main.class.getResource("sounds/Wuerfel.mp3")));
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
        if (gameSound.isRunning()) {
            gameSound.stop();
        }
        final GameReadyController gamecontroller = gameReadyController.get();
        app.show(gamecontroller);

    }




    public void soundOnOff(MouseEvent mouseEvent) {
        if (gameSound.isRunning()) {
            soundImage.setImage(unmuteImage);
            gameSound.pause();
        } else {
            soundImage.setImage(muteImage);
            gameSound.play();

        }
    }

    public void buildSettlement() {
        // build a settlement (if possible), then gain 1 VP
        gainVP(1);
    }

    public void buildTown() {
        // upgrade a settlement to a town (if possible), then
        gainVP(1);
    }

    public void gainVP(int vpGain) {
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

    public Image getSoundImage() {
        return this.soundImage.getImage();
    }

    public void changeVolume(MouseEvent mouseEvent) {
        gameSound.soundCenter(soundSlider.getValue());
    }


    private void updateOwnResources(){
        String brick = Integer.toString(player.brick());
        String grain = Integer.toString(player.grain());
        String ore = Integer.toString(player.ore());
        String lumber = Integer.toString(player.lumber());
        String wool = Integer.toString(player.wool());
        numBricksLabel.setText(brick);
        numWheatLabel.setText(grain);
        numOreLabel.setText(ore);
        numWoodLabel.setText(lumber);
        numSheepLabel.setText(wool);
    }
}
