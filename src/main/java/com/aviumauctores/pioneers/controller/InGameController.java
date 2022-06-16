package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.error.ErrorResponse;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.service.*;
import com.aviumauctores.pioneers.model.*;
import com.aviumauctores.pioneers.service.GameService;
import com.aviumauctores.pioneers.sounds.GameMusic;
import com.aviumauctores.pioneers.sounds.GameSounds;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import retrofit2.HttpException;

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

    private String[] resourceNames;

    private Label[] resourceLabels;

    @FXML
    public Label numSheepLabel;


    @FXML
    public ImageView arrowOnDice;

    @FXML
    public Label yourTurnLabel;

    @FXML
    Label timeLabel;
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
    @FXML
    public ListView<HBox> playerList;
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
    private final ErrorService errorService;
    private final BuildService buildService;

    private final HashMap<String, String> errorCodes = new HashMap<>();


    @Inject
    public InGameController(App app,
                            LoginService loginService, UserService userService,
                            ResourceBundle bundle, ColorService colorService, PlayerResourceListController playerResourceListController,
                            GameMemberService gameMemberService, GameService gameService, PioneerService pioneerService,
                            SoundService soundService, StateService stateService,
                            EventListener eventListener, Provider<GameReadyController> gameReadyController, Provider<InGameChatController> inGameChatController,
                            ErrorService errorService, BuildService buildService) {
        super(loginService, userService);
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



        errorCodes.put("429", bundle.getString("limit.reached"));
    }

    protected void onMoveEvent(EventDto<Move> eventDto) {
        Move move = eventDto.data();
        if (move.action().equals("roll")) {
            int rolled = move.roll();
            new Thread(() -> {
                try {
                    rollAllDice(rolled);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            rollSum.setText(" " + rolled + " ");
        }
    }

    public void rollAllDice(int rolled) throws InterruptedException {
        int i = 4;
        while (i > 0) {
            rollOneDice(((int) (Math.random() * 6)), diceImage1);
            rollOneDice(((int) (Math.random() * 6)), diceImage2);
            TimeUnit.MILLISECONDS.sleep(200);
            i--;
        }
        switch (rolled) {
            case 2 -> {
                diceImage1.setImage(dice1);
                diceImage2.setImage(dice1);
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
        runTimer();

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
                            diceImage1.setStyle(colourString);
                            diceImage2.setStyle(colourString);
                            try {
                                Image arrowIcon = new Image(Objects.requireNonNull(Main.class.getResource("icons/arrow_" + colourName + ".png")).toString());
                                arrowOnDice.setImage(arrowIcon);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }, this::handleThrowable
                ));
        disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".buildings.*.*", Building.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(buildingEventDto -> {
                            if (buildingEventDto.event().endsWith(".created") || buildingEventDto.event().endsWith(".updated")) {
                                //listen to new and updatedbuildings, and load the image
                                Building b = buildingEventDto.data();
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
                                if (!roadAndCrossingPane.getChildren().contains(position)) {
                                    roadAndCrossingPane.getChildren().add(position);
                                }
                            }
                        }, this::handleThrowable
                ));
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
                    playerResourceListController.setCurrentPlayerID(currentPlayerID);
                    player = stateService.getUpdatedPlayer();
                    playerResourceListController.setPlayer(player);
                    updateVisuals();
                }, this::handleError));
        disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".players.*.updated", Player.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(this::onPlayerUpdated));
        disposables.add(pioneerService.createMove(MOVE_FOUNDING_ROLL, null)
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                        }
                        , this::handleThrowable
                ));
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
                String nID = n.getId().split("#")[0];
                if (nID.equals(id)) {
                    view = (ImageView) n;
                }
            }
        }
        return view;
    }

    private void updateVisuals() {
        //check if current player has changed
        if (stateService.getNewPlayer()) {
            playerResourceListController.hideArrow(stateService.getOldPlayerID());
            playerResourceListController.showArrow(currentPlayerID);
            playerResourceListController.onPlayerTurn();
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
                    }
                }
                switch (currentAction) {
                    case MOVE_BUILD -> {
                        rollButton.setDisable(true);
                        arrowOnDice.setVisible(false);
                        finishMoveButton.setDisable(false);
                        updateFields(true, roadAndCrossingPane);
                    }

                    case MOVE_ROLL -> {
                        rollButton.setDisable(false);
                        arrowOnDice.setVisible(true);
                        finishMoveButton.setDisable(true);
                        roadAndCrossingPane.setDisable(true);
                        freeFieldVisibility(false);
                    }
                }
            }
        } else {
            arrowOnDice.setVisible(false);
            yourTurnLabel.setVisible(false);
            rollButton.setDisable(true);
            finishMoveButton.setDisable(true);
            updateFields(false, crossingPane, roadPane);
            roadAndCrossingPane.setDisable(true);
            freeFieldVisibility(false);
        }
    }

    private void onPlayerUpdated(EventDto<Player> playerEventDto) {
        Player updatedPlayer = playerEventDto.data();
        if (updatedPlayer.userId().equals(userID)) {
            playerResourceListController.setPlayer(updatedPlayer);
            playerResourceListController.updateOwnResources(resourceLabels, resourceNames);
        }
        playerResourceListController.updatePlayerLabel(updatedPlayer);
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
                        }
                        ,this::handleThrowable)
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
        if (controller != null) {
            controller.init();
            controller.setInGameController(this);
            insertChat.getChildren().add(controller.render());
        }

    }

    public void rollDice(ActionEvent actionEvent) {
        if (soundImage.getImage() == muteImage) {
            GameSounds diceSound = soundService
                    .createGameSounds(Objects.requireNonNull(Main.class.getResource("sounds/Wuerfel.mp3")));
            if (diceSound != null) {
                diceSound.play();
            }
        }
        disposables.add(pioneerService.createMove("roll", null)
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                },this::handleThrowable
                ));
    }

    public void onFieldClicked(MouseEvent mouseEvent) {
        if (!(mouseEvent.getSource() instanceof ImageView source)) {
            return;
        }
        buildService.setSelectedField(source);
        String buildingID;
        String buildingType;
        String buildingOwner;
        if (source.getId().contains("#")) {
            buildingID = source.getId().split("#")[0];
            buildingType = source.getId().split("#")[1];
            buildingOwner = source.getId().split("#")[2];
        } else {
            buildingID = source.getId();
            buildingType = "";
            buildingOwner = "";
        }
        buildService.setSelectedFieldCoordinates(coordsToPath(buildingID));
        closeBuildMenu(false);
        Building coordinateHolder = Building.readCoordinatesFromID(buildingID);
        if (coordinateHolder == null) {
            return;
        }
        int side = coordinateHolder.side();
        String sideType = "";
        if (side == 0 || side == 6) {
            if (Objects.equals(buildingType, BUILDING_TYPE_SETTLEMENT)) {
                if (userID.equals(buildingOwner)) {
                    sideType = BUILDING_TYPE_CITY;
                }
            } else {
                sideType = BUILDING_TYPE_SETTLEMENT;
            }
        } else {
            sideType = BUILDING_TYPE_ROAD;
        }
        buildService.setBuildingType(sideType);
        if (soundImage.getImage() == muteImage) {
            GameSounds buildSound = soundService
                    .createGameSounds(Objects.requireNonNull(Main.class.getResource("sounds/Hammer.mp3")));
            if (buildSound != null) {
                buildSound.play();
            }
        }
        if (currentAction != null) {
            if (currentAction.startsWith("founding")) {
                buildService.build();
                return;
            }

        }
        buildMenuController = new BuildMenuController(buildService, bundle, sideType);
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
        if (source.startsWith("building")) {
            return source;
        }
        return "building " + source.replace("-", "_");

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
                if (vpCircles[i].getFill() != Color.GOLD) {
                    vpCircles[i].setFill(Color.GOLD);
                    int finalI = i;
                    new Thread(() -> {
                        try {
                            vpAnimation(finalI);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                }
            } else {
                vpCircles[i].setFill(Color.GRAY);
            }
        }
    }

    public void vpAnimation(int index) throws InterruptedException {
        double radius = 100.0;
        while (radius >= 10.0) {
            vpCircles[index].setRadius(radius);
            radius -= 10.0;
            TimeUnit.MILLISECONDS.sleep(100);
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

    private int i = 0;


    private void runTimer() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Platform.runLater(() -> timeLabel.setText(getTime(i)));
                i++;
            }
        }, i, 1000);
    }

    static String getTime(int sec) {

        int hours = 0, minutes = 0, remainderOfHours, seconds;

        if (sec >= 3600) {
            hours = sec / 3600;
            remainderOfHours = sec % 3600;

            if (remainderOfHours >= 60) {
                minutes = remainderOfHours / 60;
                seconds = remainderOfHours % 60;
            } else {
                seconds = remainderOfHours;
            }
        } else if (sec >= 60) {
            minutes = sec / 60;
            seconds = sec % 60;
        } else {
            seconds = sec;
        }


        String strHours;
        String strMins;
        String strSecs;

        if (seconds < 10)
            strSecs = "0" + seconds;
        else
            strSecs = Integer.toString(seconds);

        if (minutes < 10)
            strMins = "0" + minutes;
        else
            strMins = Integer.toString(minutes);

        if (hours < 10)
            strHours = "0" + hours;
        else
            strHours = Integer.toString(hours);

        return strHours + ":" + strMins + ":" + strSecs;
    }

    public void fieldsIntoOnePane() {
        roadAndCrossingPane.getChildren().addAll(roadPane.getChildren());
        roadAndCrossingPane.getChildren().addAll(crossingPane.getChildren());
        updateFields(true, roadAndCrossingPane);
        roadPane.getChildren().removeAll();
        updateFields(false, roadPane);
        crossingPane.getChildren().removeAll();
        updateFields(false, crossingPane);
    }

    public void freeFieldVisibility(boolean var) {
        for (Node n : roadAndCrossingPane.getChildren()) {
            ImageView field = (ImageView) n;
            if (field.getImage().getUrl().endsWith("empty.png") || field.getImage().getUrl().endsWith("emptyRoad.png")) {
                field.setVisible(var);
            }
        }
    }

    public void handleError(Throwable throwable) {
        if (throwable instanceof HttpException ex) {
            ErrorResponse response = errorService.readErrorMessage(ex);
            String message = errorCodes.get(Integer.toString(response.statusCode()));
            app.showHttpErrorDialog(response.statusCode(), response.error(), message);
        }
    }

    private void handleThrowable(Throwable throwable) {
        if (throwable instanceof HttpException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            String content;
            if (ex.code() == 429) {
                content = "HTTP 429-Error";
            } else {
                content = "Unknown error";
            }
            alert.setContentText(content);
            alert.showAndWait();
        }
    }

}
