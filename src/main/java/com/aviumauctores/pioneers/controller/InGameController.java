package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.service.*;
import com.aviumauctores.pioneers.model.*;
import com.aviumauctores.pioneers.service.GameService;
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
import java.util.Objects;
import java.util.*;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static com.aviumauctores.pioneers.Constants.*;


public class InGameController extends LoggedInController {
    private final App app;
    private final ResourceBundle bundle;

    private final ColorService colorService;
    private final PlayerResourceListController playerResourceListController;
    private final GameMemberService gameMemberService;
    private final GameService gameService;
    private final PioneerService pioneerService;

    private Player player;
    private final EventListener eventListener;
    private final SoundService soundService;

    private String sideType;
    private String[] resourceNames;

    private Label[] resourceLabels;

    @FXML
    public Label numSheepLabel;


    @FXML
    public ImageView arrowOnDice;

    @FXML
    public Label yourTurnLabel;
    @FXML
    public Pane mainPane;
    @FXML
    public Pane crossingPane;
    @FXML
    public Pane roadPane;

    @FXML
    public Pane roadAndCrossingPane;
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
    private String currentAction;

    private final Provider<InGameChatController> inGameChatController;
    private final StateService stateService;
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
    @FXML
    public Button buildButton;


    public Circle[] vpCircles;

    public int memberVP;

    @FXML
    public Label rollSum;

    @FXML
    public ImageView diceImage1;
    @FXML
    public ImageView diceImage2;
    Image dice1;
    Image dice2;
    Image dice3;
    Image dice4;
    Image dice5;
    Image dice6;

    Image desert;
    Image fields;
    Image hills;
    Image mountains;
    Image forest;
    Image pasture;

    GameMusic gameSound;
    private BuildMenuController buildMenuController;
    private Parent buildMenu;


    // These are the Sound-Icons

    Image muteImage;
    Image unmuteImage;
    private ErrorService errorService;
    private final BuildService buildService;


    @Inject
    public InGameController(App app, UserService userService, ResourceBundle bundle, ColorService colorService, PlayerResourceListController playerResourceListController,
                            GameMemberService gameMemberService, GameService gameService, PioneerService pioneerService,
                            SoundService soundService, StateService stateService,
                            EventListener eventListener, Provider<GameReadyController> gameReadyController, Provider<InGameChatController> inGameChatController,
                            ErrorService errorService, BuildService buildService) {
        super(userService);
        this.app = app;
        this.bundle = bundle;
        this.colorService = colorService;
        this.playerResourceListController = playerResourceListController;
        this.gameMemberService = gameMemberService;
        this.soundService = soundService;
        this.stateService = stateService;
        this.gameReadyController = gameReadyController;
        this.inGameChatController = inGameChatController;
        this.gameService = gameService;
        this.pioneerService = pioneerService;
        this.eventListener = eventListener;
        this.errorService = errorService;
        this.buildService = buildService;
    }


    @Override
    public void init() {
        disposables = new CompositeDisposable();
        memberVP = 0;
        resourceNames = new String[]{RESOURCE_BRICK, RESOURCE_GRAIN, RESOURCE_LUMBER, RESOURCE_ORE, RESOURCE_WOOL};


        // Initialize these objects here because else the tests would fail
        userID = userService.getCurrentUserID();
        player = pioneerService.getPlayer(userID).blockingFirst();


        gameSound = soundService.createGameMusic(Objects.requireNonNull(Main.class.getResource("sounds/GameMusik.mp3")));
        muteImage = new Image(Objects.requireNonNull(Main.class.getResource("soundImages/mute.png")).toString());
        unmuteImage = new Image(Objects.requireNonNull(Main.class.getResource("soundImages/unmute.png")).toString());

        dice1 = new Image(Objects.requireNonNull(Main.class.getResource("views/diceImages/Dice_1.png")).toString());
        dice2 = new Image(Objects.requireNonNull(Main.class.getResource("views/diceImages/Dice_2.png")).toString());
        dice3 = new Image(Objects.requireNonNull(Main.class.getResource("views/diceImages/Dice_3.png")).toString());
        dice4 = new Image(Objects.requireNonNull(Main.class.getResource("views/diceImages/Dice_4.png")).toString());
        dice5 = new Image(Objects.requireNonNull(Main.class.getResource("views/diceImages/Dice_5.png")).toString());
        dice6 = new Image(Objects.requireNonNull(Main.class.getResource("views/diceImages/Dice_6.png")).toString());
        desert = new Image(Objects.requireNonNull(Main.class.getResource("views/tiles/desert.png")).toString());
        fields = new Image(Objects.requireNonNull(Main.class.getResource("views/tiles/wheat.png")).toString());
        hills = new Image(Objects.requireNonNull(Main.class.getResource("views/tiles/brick.png")).toString());
        mountains = new Image(Objects.requireNonNull(Main.class.getResource("views/tiles/ore.png")).toString());
        forest = new Image(Objects.requireNonNull(Main.class.getResource("views/tiles/forest.png")).toString());
        pasture = new Image(Objects.requireNonNull(Main.class.getResource("views/tiles/pasture.png")).toString());
        // Listen to game-move events
        disposables.add(eventListener.listen(
                        "games." + gameService.getCurrentGameID() + ".moves.*.*",
                        Move.class
                )
                .observeOn(FX_SCHEDULER)
                .subscribe(this::onMoveEvent));


    }

    protected void onMoveEvent(EventDto<Move> eventDto) throws InterruptedException {
        Move move = eventDto.data();
        if (move.action().equals("roll")) {
            int rolled = move.roll();
            rollSum.setText(" " + rolled + " ");
            new Thread(() -> {
                try {
                    rollAllDice(rolled);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    public void rollAllDice(int rolled) throws InterruptedException {
        int i = 6;
        while (i > 0) {
            rollOneDice(((int) (Math.random() * 6)), diceImage1);
            rollOneDice(((int) (Math.random() * 6)), diceImage2);
            TimeUnit.MILLISECONDS.sleep(500);
            i--;
        }
        switch (rolled) {
            case 2 -> {
                diceImage1.setImage(dice1);
                diceImage1.setImage(dice1);
            }
            case 3 -> {
                diceImage1.setImage(dice1);
                diceImage2.setImage(dice2);
            }
            case 4 -> {
                diceImage1.setImage(dice3);
                diceImage2.setImage(dice1);
            }
            case 5 -> {
                diceImage1.setImage(dice2);
                diceImage2.setImage(dice3);
            }
            case 6 -> {
                diceImage1.setImage(dice2);
                diceImage2.setImage(dice4);
            }
            case 7 -> {
                diceImage1.setImage(dice2);
                diceImage2.setImage(dice5);
            }
            case 8 -> {
                diceImage1.setImage(dice5);
                diceImage2.setImage(dice3);
            }
            case 9 -> {
                diceImage1.setImage(dice4);
                diceImage2.setImage(dice5);
            }
            case 10 -> {
                diceImage1.setImage(dice6);
                diceImage2.setImage(dice4);
            }
            case 11 -> {
                diceImage1.setImage(dice5);
                diceImage2.setImage(dice6);
            }
            case 12 -> {
                diceImage1.setImage(dice6);
                diceImage2.setImage(dice6);
            }
        }
    }

    public void rollOneDice(int randomInteger, ImageView imageView) {
        switch (randomInteger) {
            case 1 -> imageView.setImage(dice1);
            case 2 -> imageView.setImage(dice2);
            case 3 -> imageView.setImage(dice3);
            case 4 -> imageView.setImage(dice4);
            case 5 -> imageView.setImage(dice5);
            case 6 -> imageView.setImage(dice6);
        }
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
        resourceLabels = new Label[]{numBricksLabel, numWheatLabel, numWoodLabel, numOreLabel, numSheepLabel};
        vpCircles = new Circle[]{vp01, vp02, vp03, vp04, vp05, vp06, vp07, vp08, vp09, vp10};


        arrowOnDice.setFitHeight(40.0);
        arrowOnDice.setFitWidth(40.0);
        disposables.add(gameMemberService.getMember(userID)
                .observeOn(FX_SCHEDULER)
                .subscribe(member -> {
                    Color colour = member.color();
                    String colourString = "-fx-background-color: #" + colour.toString().substring(2, 8);
                    String colourName = colorService.getColor("#" + colour.toString().substring(2, 8));
                    rollButton.setStyle(colourString);
                    leaveGameButton.setStyle(colourString);
                    finishMoveButton.setStyle(colourString);
                    buildButton.setStyle(colourString);
                    diceImage1.setStyle(colourString);
                    diceImage2.setStyle(colourString);
                    try {
                        Image arrowIcon = new Image(Objects.requireNonNull(Main.class.getResource("icons/arrow_" + colourName + ".png")).toString());
                        arrowOnDice.setImage(arrowIcon);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }));
        disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".buildings.*.created", Building.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(building -> {
                    //listen to new buildings, and load the image
                    Building b = building.data();
                    Player builder = pioneerService.getPlayer(b.owner()).blockingFirst();
                    buildService.setPlayer(builder);
                    buildService.setBuildingType(b.type());
                    ImageView position = getView(b.x(), b.y(), b.z(), b.side());
                    buildService.setSelectedField(position);
                    buildService.loadBuildingImage(b._id());
                    if (b.owner().equals(userID)) {
                        if (b.type().equals(BUILDING_TYPE_SETTLEMENT) || b.type().equals(BUILDING_TYPE_CITY)) {
                            gainVP(1);
                        }
                    }
                }));
        diceImage1.setImage(dice1);
        diceImage2.setImage(dice1);
        this.soundImage.setImage(muteImage);

        disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".state.*", State.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(state -> {
                    //update class variables
                    stateService.updateState(state);
                    currentPlayerID = stateService.getCurrentPlayerID();
                    currentAction = stateService.getCurrentAction();
                    buildService.setCurrentAction(currentAction);
                    player = stateService.getUpdatedPlayer();
                    playerResourceListController.setPlayer(player);
                    playerResourceListController.updateOwnResources(resourceLabels, resourceNames);
                    playerResourceListController.updateResourceList();
                    updateVisuals();
                }));
        disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".players.*.updated", Player.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(this::onPlayerUpdated));
        disposables.add(pioneerService.createMove(MOVE_FOUNDING_ROLL, null)
                .observeOn(FX_SCHEDULER)
                .subscribe());
        currentPlayerID = pioneerService.getState().blockingFirst().expectedMoves().get(0).players().get(0);
        arrowOnDice.setFitHeight(40.0);
        arrowOnDice.setFitWidth(40.0);
        yourTurnLabel.setVisible(false);
        if (currentPlayerID.equals(userID)) {
            arrowOnDice.setVisible(true);
            yourTurnLabel.setVisible(true);
            updateFields(false, roadPane);
            updateFields(true, crossingPane);
        } else {
            arrowOnDice.setVisible(false);
            yourTurnLabel.setVisible(false);
            updateFields(false, crossingPane, roadPane);
        }
        soundImage.setImage(muteImage);
        loadChat();
        playerResourceListController.init(playerList, currentPlayerID);
        finishMoveButton.setDisable(true);
        buildMap();

        return parent;
    }

    private ImageView getView(int x, int y, int z, int side) {
        //create building id
        String location = "building" + x + y + z + side;
        location = location.replace("-", "_");
        return getNodeByID(location);

    }

    private ImageView getNodeByID(String id) {
        //search for Node in road and crossingpane
        ImageView view = null;
        if (currentAction.startsWith("founding")) {
            for (Node n : crossingPane.getChildren()) {
                if (n.getId().equals(id)) {
                    view = (ImageView) n;
                }
            }
            if (view == null) {
                for (Node n : roadPane.getChildrenUnmodifiable()) {
                    if (n.getId().equals(id)) {
                        view = (ImageView) n;
                    }
                }
            }
        } else {
            for (Node n : roadAndCrossingPane.getChildren()) {
                if (n.getId().equals(id)) {
                    view = (ImageView) n;
                }
            }
        }
        return view;
    }

    private void updateVisuals() {
        //check if current player has changed
        if (stateService.getNewPlayer()) {
            playerResourceListController.hideArrow(pioneerService.getPlayer(stateService.getOldPlayerID()).blockingFirst());
            playerResourceListController.showArrow(pioneerService.getPlayer(currentPlayerID).blockingFirst());
        }
        //enable and disable road and crossingpane, depending on current action and current player
        if (currentPlayerID.equals(userID)) {
            yourTurnLabel.setVisible(true);
            if (currentAction.startsWith("founding")) {
                rollButton.setDisable(true);
                arrowOnDice.setVisible(false);
                finishMoveButton.setDisable(true);
                switch (currentAction) {
                    case MOVE_FOUNDING_ROAD + "1", MOVE_FOUNDING_ROAD + "2" -> {
                        updateFields(true, roadPane);
                        updateFields(false, crossingPane);
                    }
                    case MOVE_FOUNDING_SETTLEMENT + "1", MOVE_FOUNDING_SETTLEMENT + "2" -> {
                        updateFields(true, crossingPane);
                        updateFields(false, roadPane);
                    }
                }
            } else {
                updateFields(false, crossingPane, roadPane);
                if (stateService.getOldAction() != null) {
                    if (stateService.getOldAction().startsWith("founding") && !currentAction.startsWith("founding")) {
                        fieldsIntoOnePane();
                        System.out.println(stateService.getOldAction());
                    }
                }
                switch (currentAction) {
                    case MOVE_BUILD -> {
                        rollButton.setDisable(true);
                        arrowOnDice.setVisible(false);
                        finishMoveButton.setDisable(false);
                        //updateFields(true, crossingPane, roadPane);
                        updateFields(true, roadAndCrossingPane);
                    }
                    case MOVE_ROLL -> {
                        rollButton.setDisable(false);
                        arrowOnDice.setVisible(true);
                        finishMoveButton.setDisable(true);
                        //updateFields(false, crossingPane, roadPane);
                        updateFields(false, roadAndCrossingPane);
                    }
                }
            }
        } else {
            arrowOnDice.setVisible(false);
            yourTurnLabel.setVisible(false);
            rollButton.setDisable(true);
            finishMoveButton.setDisable(true);
            updateFields(false, crossingPane, roadPane, roadAndCrossingPane);
        }
    }

    private void onPlayerUpdated(EventDto<Player> playerEventDto) {
        Player updatedPlayer = playerEventDto.data();
        if (updatedPlayer.userId().equals(userID)) {
            playerResourceListController.setPlayer(player);
            playerResourceListController.updateOwnResources(resourceLabels, resourceNames);
        } else {
            playerResourceListController.updatePlayerLabel(updatedPlayer);
        }
    }

    public void buildMap() {
        disposables.add(pioneerService.getMap()
                .observeOn(FX_SCHEDULER)
                .subscribe(map -> {
                    List<Tile> tiles = map.tiles();
                    for (Tile tile : tiles) {
                        String hexID = "" + tile.x() + tile.y() + tile.z();
                        hexID = hexID.replace('-', '_');
                        ImageView tileImage = (ImageView) mainPane.lookup("#hexagon" + hexID);
                        switch (tile.type()) {
                            case "desert" -> tileImage.setImage(desert);
                            case "fields" -> tileImage.setImage(fields);
                            case "hills" -> tileImage.setImage(hills);
                            case "mountains" -> tileImage.setImage(mountains);
                            case "forest" -> tileImage.setImage(forest);
                            case "pasture" -> tileImage.setImage(pasture);
                        }
                        Label tileLabel = (Label) mainPane.lookup("#label" + hexID);
                        tileLabel.setText("" + tile.numberToken());
                    }
                })
        );
    }

    @Override
    public void destroy(boolean closed) {
        super.destroy(closed);
        closeBuildMenu(closed);
    }

    public void finishMove(ActionEvent actionEvent) {
        pioneerService.createMove(MOVE_BUILD, null)
                .observeOn(FX_SCHEDULER)
                .subscribe();
    }

    public void build(ActionEvent event) {
        buildService.build();
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
            if (diceSound != null) {
                diceSound.play();
            }
        }
        //For test purpose: First move needs to be "founding-roll"
        disposables.add(pioneerService.createMove("roll", null)
                .observeOn(FX_SCHEDULER)
                .subscribe());
    }

    public void onFieldClicked(MouseEvent mouseEvent) {
        if (!(mouseEvent.getSource() instanceof ImageView source)) {
            return;
        }
        buildService.setSelectedField(source);
        buildService.setSelectedFieldCoordinates(coordsToPath(source.getId()));
        closeBuildMenu(false);
        Building coordinateHolder = Building.readCoordinatesFromID(source.getId());
        if (coordinateHolder == null) {
            return;
        }
        int side = coordinateHolder.side();
        if (side == 0 || side == 6) {
            sideType = BUILDING_TYPE_SETTLEMENT;

        } else {
            sideType = BUILDING_TYPE_ROAD;
        }
        buildService.setBuildingType(sideType);
        if (currentAction != null) {
            if (currentAction.startsWith("founding")) {
                buildService.build();
                return;
            }
        }
        buildMenuController = new BuildMenuController(bundle, sideType);
        buildMenu = buildMenuController.render();
        buildMenu.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
            buildMenu.setLayoutX(Math.min(source.getLayoutX(), mainPane.getWidth() - newValue.getWidth()));
            buildMenu.setLayoutY(Math.min(source.getLayoutY(), mainPane.getHeight() - newValue.getHeight()));
        });
        mainPane.getChildren().add(buildMenu);
        // Prevent the event handler from main pane to close the build menu immediately after this
        mouseEvent.consume();

    }

    private String coordsToPath(String source) {
        String res = null;
        if (source.startsWith("building")) {
            return res;
        }
        res = "building " + source.replace("-", "_");
        return res;

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
        if (buildButton != null) {
            buildButton.setDisable(appClosed);
            buildButton.setVisible(!appClosed);
        }
    }

    public void onMainPaneClicked(MouseEvent mouseEvent) {
        closeBuildMenu(false);
        buildButton.setDisable(true);
        buildButton.setVisible(false);
        buildService.setSelectedField(null);
        buildService.setSelectedFieldCoordinates(null);
    }

    public void leaveGame(ActionEvent actionEvent) {
        if (gameSound.isRunning()) {
            gameSound.stop();
        }
        app.show(gameReadyController.get());

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


    public void gainVP(int vpGain) {
        memberVP += vpGain;
        for (int i = 0; i < 10; i++) {
            if (memberVP > i) {
                vpCircles[i].setFill(Color.GOLD);
                int finalI = i;
                new Thread(() -> {
                    /*try {
                        vpAnimation(finalI);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }*/
                }).start();
            } else {
                vpCircles[i].setFill(Color.GRAY);
            }
        }
    }

    public void vpAnimation(int index) throws InterruptedException {
        double radius = 100.0;
        while (radius >= 10.0) {
            vpCircles[index].setRadius(radius);
            radius -= 1.0;
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }

    public Image getSoundImage() {
        return this.soundImage.getImage();
    }

    public void changeVolume(MouseEvent mouseEvent) {
        gameSound.soundCenter(soundSlider.getValue());
    }

    public void updateFields(boolean val, Pane... panes) {
        for (Pane pane : panes) {
            pane.setVisible(val);
            pane.setDisable(!val);
            for (Node node : pane.getChildren()) {
                node.setVisible(val);
                node.setDisable(!val);

            }
        }
    }

    public void fieldsIntoOnePane() {
        roadAndCrossingPane.getChildren().setAll(roadPane.getChildren());
        roadAndCrossingPane.getChildren().addAll(crossingPane.getChildren());
        roadPane.getChildren().removeAll();
        crossingPane.getChildren().removeAll();
    }

}
