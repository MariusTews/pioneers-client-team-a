package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.dto.rob.RobDto;
import com.aviumauctores.pioneers.model.*;
import com.aviumauctores.pioneers.service.*;
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
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.Map;
import java.util.*;
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
    @FXML
    public Button tradeButton;
    public VBox tradeRequestPopup;
    public Button viewRequestButton;
    public Label playerWantTradeLabel;
    public Label resourceLabel;
    public Text soundSliderLabelTop;
    public Text soundSliderLabelRight;
    public Label lastRollPlayerLabelPart2;
    public Text soundSliderLabelLeft;
    private Player player;

    private final EventListener eventListener;
    private final SoundService soundService;
    private String[] resourceNames;
    private Timer timer;
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
    public BorderPane ingamePane;

    public Pane mainPane;
    public Pane crossingPane;
    public Pane roadPane;
    public Pane roadAndCrossingPane;
    private Pane robberPane;
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
    private final Provider<LobbyController> lobbyController;
    private final Provider<GameReadyController> gameReadyController;
    private TradingController tradingController;
    @FXML
    private Slider soundSlider;


    private int previousResourceSum = 0;
    public List<Circle> vpCircles;

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

    GameMusic gameSound;

    private BuildMenuController buildMenuController;
    private DropMenuController dropMenuController;
    private TradeRequestController tradeRequestController;
    private Parent buildMenu;
    private Parent dropMenu;
    private Parent tradingMenu;
    private Parent requestMenu;

    private final Map<String, Boolean> enableButtons = new HashMap<>();
    private int requiredPoints;

    private boolean tradeStarter = false;


    // These are the Sound-Icons
    Image muteImage;
    Image unmuteImage;

    private Image robberView;
    private Image emptyCircle;

    private final ErrorService errorService;
    private final BuildService buildService;
    private final Provider<PostGameController> postGameController;
    private final StatService statService;

    private final AchievementsService achievementsService;
    private final MapController mapController;
    private boolean fieldsMovedAlready;
    private final List<Node> robTargets = new ArrayList<>();
    private String selectedRobberFieldId = "";

    //for tradingController
    private final TradeService tradeService;
    private String desertTileId;
    private HashMap<String, Integer> tradeRessources;
    private String tradePartner;
    private String tradePartnerAvatarUrl;
    private String tradePartnerColor;

    private boolean spectator;

    private HashMap<String, List<String>> nextHarbors;
    private boolean rejoin = false;
    private final HashMap<String, String> playerColors = new HashMap<>();


    @Inject
    public InGameController(App app,
                            LoginService loginService, UserService userService,
                            ResourceBundle bundle, ColorService colorService, PlayerResourceListController playerResourceListController,
                            GameMemberService gameMemberService, GameService gameService, PioneerService pioneerService,
                            SoundService soundService, StateService stateService, Provider<LobbyController> lobbyController,
                            EventListener eventListener, Provider<GameReadyController> gameReadyController, Provider<InGameChatController> inGameChatController,
                            ErrorService errorService, BuildService buildService, AchievementsService achievementsService, MapController mapController, TradeService tradeService,
                            Provider<PostGameController> postGameController, StatService statService) {
        super(loginService, userService);
        this.app = app;
        this.bundle = bundle;
        this.colorService = colorService;
        this.playerResourceListController = playerResourceListController;
        this.gameMemberService = gameMemberService;
        this.soundService = soundService;
        this.stateService = stateService;
        this.lobbyController = lobbyController;
        this.gameReadyController = gameReadyController;
        this.inGameChatController = inGameChatController;
        this.gameService = gameService;
        this.pioneerService = pioneerService;
        this.eventListener = eventListener;
        this.errorService = errorService;
        this.buildService = buildService;
        this.achievementsService = achievementsService;
        this.mapController = mapController;
        this.tradeService = tradeService;
        this.postGameController = postGameController;
        this.statService = statService;
        fieldsMovedAlready = false;
    }

    public void setSpectator(Boolean spectator) {
        this.spectator = (spectator);
    }

    public boolean getSpectator() {
        return this.spectator;
    }

    @Override
    public void init() {
        disposables = new CompositeDisposable();
        memberVP = 0;
        resourceNames = new String[]{RESOURCE_BRICK, RESOURCE_GRAIN, RESOURCE_LUMBER, RESOURCE_ORE, RESOURCE_WOOL};
        enableButtons.put(BUILDING_TYPE_CITY, false);
        enableButtons.put(BUILDING_TYPE_SETTLEMENT, false);
        enableButtons.put(BUILDING_TYPE_ROAD, false);


        userID = userService.getCurrentUserID();

        try {
            player = pioneerService.getPlayer(userID).blockingFirst();
        } catch (Exception ignored) {

        }
        //get player colors
        disposables.add(this.pioneerService.listPlayers()
                .observeOn(FX_SCHEDULER)
                .subscribe(players -> {
                    for (Player player : players) {
                        playerColors.put(player.userId(), player.color());
                    }
                }));

        // Initialize these objects here because else the tests would fail
        gameSound = soundService.createGameMusic(Objects.requireNonNull(Main.class.getResource("sounds/GameMusik.mp3")));
        muteImage = new Image(Objects.requireNonNull(Main.class.getResource("soundImages/mute.png")).toString());
        unmuteImage = new Image(Objects.requireNonNull(Main.class.getResource("soundImages/unmute.png")).toString());

        dice1 = new Image(Objects.requireNonNull(Main.class.getResource("views/diceImages/Dice_1.png")).toString());
        dice2 = new Image(Objects.requireNonNull(Main.class.getResource("views/diceImages/Dice_2.png")).toString());
        dice3 = new Image(Objects.requireNonNull(Main.class.getResource("views/diceImages/Dice_3.png")).toString());
        dice4 = new Image(Objects.requireNonNull(Main.class.getResource("views/diceImages/Dice_4.png")).toString());
        dice5 = new Image(Objects.requireNonNull(Main.class.getResource("views/diceImages/Dice_5.png")).toString());
        dice6 = new Image(Objects.requireNonNull(Main.class.getResource("views/diceImages/Dice_6.png")).toString());

        robberView = new Image(Objects.requireNonNull(Main.class.getResource("views/robber.png")).toString());
        emptyCircle = new Image(Objects.requireNonNull(Main.class.getResource("views/buildings/empty.png")).toString());

        // Listen to game-move events
        disposables.add(eventListener.listen(
                        "games." + gameService.getCurrentGameID() + ".moves.*.*",
                        Move.class
                )
                .observeOn(FX_SCHEDULER)
                .subscribe(this::onMoveEvent));

        disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".deleted", Game.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(game -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(bundle.getString("warning"));
                    alert.setHeaderText(bundle.getString("game.deleted"));
                    alert.showAndWait();
                    gameService.setCurrentGameID(null);
                    app.show(lobbyController.get());
                }));

        requiredPoints = gameService.getVictoryPoints();
        achievementsService.init();
        statService.init();
        buildService.init();

        disposables.add(achievementsService.getUserAchievements().observeOn(FX_SCHEDULER).subscribe());
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
        MapController controller = mapController;
        disposables.add(pioneerService.getMap()
                .observeOn(FX_SCHEDULER)
                .subscribe(map -> {
                            if (controller != null) {
                                controller.init();
                                controller.setInGameController(this);
                                controller.setGameMap(map);
                                controller.setMapRadius(gameService.getCurrentGame().blockingFirst().settings().mapRadius());
                                Pane pane = (Pane) controller.render();
                                ingamePane.setCenter(pane);
                                ingamePane.getRight().toFront();
                                ingamePane.getLeft().toFront();
                                ingamePane.getBottom().toFront();
                                pane.setTranslateX(240);
                                mainPane = controller.getMainPane();
                                roadAndCrossingPane = controller.getRoadAndCrossingPane();
                                roadPane = controller.getRoadPane();
                                crossingPane = controller.getCrossingPane();
                                robberPane = controller.getRobberPane();
                                vpCircles = new ArrayList<>();
                                for (Node vpCircle : controller.getVpBox().getChildren()) {
                                    vpCircles.add((Circle) vpCircle);
                                }
                                //put robber on desert tile
                                desertTileId = controller.getDesertTileId();
                                String desertRobberImageId = desertTileId.replace("hexagon", "robber");
                                moveRobber(desertRobberImageId);
                                runTimer();
                            }

                            resourceLabels = new Label[]{numBricksLabel, numWheatLabel, numWoodLabel, numOreLabel, numSheepLabel};
                            arrowOnDice.setFitHeight(40.0);
                            arrowOnDice.setFitWidth(40.0);
                            errorService.setErrorCodesGameMembersPost();
                            nextHarbors = Objects.requireNonNull(controller).getHarborCrossings();

                            disposables.add(gameMemberService.getMember(userID)
                                    .observeOn(FX_SCHEDULER)
                                    .subscribe(member -> {
                                        Color colour = member.color();
                                        String colourString = "-fx-background-color: #" + colour.toString().substring(2, 8);
                                        String colourName = colorService.getColor("#" + colour.toString().substring(2, 8));
                                        rollButton.setStyle(colourString);
                                        leaveGameButton.setStyle(colourString);
                                        finishMoveButton.setStyle(colourString);
                                        tradeButton.setStyle(colourString);
                                        diceImage1.setStyle(colourString);
                                        diceImage2.setStyle(colourString);
                                        try {
                                            Image arrowIcon = new Image(Objects.requireNonNull(Main.class.getResource("icons/arrow_" + colourName + ".png")).toString());
                                            arrowOnDice.setImage(arrowIcon);
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }
                                    }, errorService::handleError));

                            disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".buildings.*.*", Building.class)
                                    .observeOn(FX_SCHEDULER)
                                    .subscribe(this::onBuildEvent, errorService::handleError));

                            diceImage1.setImage(dice1);
                            diceImage2.setImage(dice1);
                            this.soundImage.setImage(muteImage);
                            playerResourceListController.init(playerList, currentPlayerID);

                            disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".state.*", State.class)
                                    .observeOn(FX_SCHEDULER)
                                    .subscribe(this::onStateUpdate, throwable -> System.out.println(throwable.toString() + "| error on state update")));

                            disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".players.*.updated", Player.class)
                                    .observeOn(FX_SCHEDULER)
                                    .subscribe(this::onPlayerUpdated));

                            errorService.setErrorCodesPioneersPost();

                            if (!rejoin && !spectator) {
                                disposables.add(pioneerService.createMove(MOVE_FOUNDING_ROLL, null, null, null, null)
                                        .observeOn(FX_SCHEDULER)
                                        .subscribe(move -> {
                                        }, errorService::handleError));
                            }

                            //get first state to prevent runtime issues
                            State firstState = pioneerService.getState().blockingFirst();
                            onStateUpdate(new EventDto<>(".created", firstState));

                            arrowOnDice.setFitHeight(40.0);
                            arrowOnDice.setFitWidth(40.0);
                            soundImage.setImage(muteImage);
                            loadChat();

                            if (rejoin) {
                                disposables.add(this.pioneerService.listBuildings()
                                        .observeOn(FX_SCHEDULER)
                                        .subscribe(buildingList -> {
                                            for (Building building : buildingList) {
                                                EventDto<Building> event = new EventDto<>(".created", building);
                                                this.onBuildEvent(event);
                                            }
                                            rejoin = false;
                                        }, throwable -> System.out.println(throwable.toString() + "| error on list buildings")));

                                disposables.add(this.pioneerService.listPlayers()
                                        .observeOn(FX_SCHEDULER)
                                        .subscribe(players -> {
                                            for (Player p : players) {
                                                onPlayerUpdated(new EventDto<>(".updated", p));
                                            }
                                        })
                                );
                            }

                        }, errorService::handleError
                ));

        return parent;
    }

    private void onStateUpdate(EventDto<State> state) {
        //update class variables
        stateService.updateState(state);
        currentPlayerID = stateService.getCurrentPlayerID();
        currentAction = stateService.getCurrentAction();
        buildService.setCurrentAction(currentAction);
        playerResourceListController.setCurrentPlayerID(currentPlayerID);
        updateVisuals();
    }

    private void onBuildEvent(EventDto<Building> buildingEventDto) {
        if (buildingEventDto.event().endsWith(".created") || buildingEventDto.event().endsWith(".updated")) {
            //listen to new and updated buildings, and load the image
            Building b = buildingEventDto.data();
            buildService.setPlayerId(b.owner());
            buildService.setBuildingType(b.type());
            ImageView position = getView(b.x(), b.y(), b.z(), b.side());
            buildService.setSelectedField(position);
            buildService.loadBuildingImage();
            String buildingImageId = position.getId().split("#")[0];
            enableBuildingColor(buildingImageId, b.owner(), b.type());
            if (b.owner().equals(userID)) {
                if (b.type().equals(BUILDING_TYPE_SETTLEMENT) || b.type().equals(BUILDING_TYPE_CITY)) {
                    gainVP(1);
                }
            }
            if (!roadAndCrossingPane.getChildren().contains(position)) {
                position.setVisible(true);
                roadAndCrossingPane.getChildren().add(position);
            }
            if (!rejoin) {
                if (soundImage.getImage() == muteImage) {
                    GameSounds buildSound = soundService
                            .createGameSounds(Objects.requireNonNull(Main.class.getResource("sounds/Hammer.mp3")));
                    if (buildSound != null) {
                        buildSound.play();
                    }
                }
                if (soundImage.getImage() == muteImage && b.type().equals(BUILDING_TYPE_ROAD)) {
                    GameSounds roadSound = soundService
                            .createGameSounds(Objects.requireNonNull(Main.class.getResource("sounds/road.mp3")));
                    if (roadSound != null) {
                        roadSound.play();
                    }
                }
                if (soundImage.getImage() == muteImage && b.type().equals(BUILDING_TYPE_SETTLEMENT)) {
                    GameSounds settlementSound = soundService
                            .createGameSounds(Objects.requireNonNull(Main.class.getResource("sounds/settlement.mp3")));
                    if (settlementSound != null) {
                        settlementSound.play();
                    }
                }
                if (soundImage.getImage() == muteImage && b.type().equals(BUILDING_TYPE_CITY)) {
                    GameSounds citySound = soundService
                            .createGameSounds(Objects.requireNonNull(Main.class.getResource("sounds/city.mp3")));
                    if (citySound != null) {
                        citySound.play();
                    }
                }
            }
        }
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
        } else if (move.rob() != null && move.rob().target() != null) {
            statService.playerRobbed(move.rob().target());
        }
        //show trade request if a player wants to trade with you
        if (move.partner() != null) {
            if (move.action().equals("build") && move.partner().equals(userID)) {
                this.showTradeRequest(move.resources(), move.userId());
            }
        }

        //accepted trade
        errorService.setErrorCodesTrading();
        if (move.resources() != null) {
            if (move.action().equals("offer") && this.tradingController != null) {
                this.tradingController.handleRequest(move.resources(), move.userId());
            }
        }

        //declined trade
        if (move.resources() == null) {
            if (move.action().equals("offer") && this.tradingController != null) {
                disposables.add(pioneerService.createMove("accept", null, null, null, null)
                        .observeOn(FX_SCHEDULER).
                        subscribe(success -> {
                                    tradingController.enableCancelButton();
                                    tradingController.showRequestDeclined(move.userId());
                                    this.setTradeStarter(false);
                                },
                                error -> {
                                    errorService.handleError(error);
                                    tradingController.enableCancelButton();
                                    this.setTradeStarter(false);
                                }
                        ));
            }
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

    private ImageView getView(int x, int y, int z, int side) {
        //create building id
        String location = "buildingX" + x + "Y" + y + "Z" + z + "R" + side;
        location = location.replace("-", "_");
        return getNodeByID(location);
    }

    private ImageView getNodeByID(String id) {
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
        if (rejoin) {
            Point3D point = stateService.getRobberPosition();
            if (point != null) {
                String id = "robberX" + point.x() + "Y" + point.y() + "Z" + point.z();
                id = id.replace("-", "_");
                moveRobber(id);
            }
        }
        if (!fieldsMovedAlready) {
            if (!currentAction.startsWith("founding")) {
                fieldsIntoOnePane();
                fieldsMovedAlready = true;
            }
        }
        //check if current player has changed
        if (stateService.getNewPlayer()) {
            playerResourceListController.hideArrow(stateService.getOldPlayerID());
            playerResourceListController.showArrow(currentPlayerID);
            playerResourceListController.onPlayerTurn();
        }
        //update visuals, depending on current action and current player
        if (currentPlayerID.equals(userID)) {
            yourTurnLabel.setVisible(true);
            if (currentAction.startsWith("founding")) {
                rollButton.setDisable(true);
                arrowOnDice.setVisible(false);
                finishMoveButton.setDisable(true);
                tradeButton.setDisable(true);
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
                switch (currentAction) {
                    case MOVE_BUILD -> {
                        rollButton.setDisable(true);
                        arrowOnDice.setVisible(false);
                        finishMoveButton.setDisable(false);
                        tradeButton.setDisable(false);
                        updateFields(true, roadAndCrossingPane);
                    }

                    case MOVE_ROLL -> {
                        freeFieldVisibility(false);
                        rollButton.setDisable(false);
                        arrowOnDice.setVisible(true);
                        finishMoveButton.setDisable(true);
                        tradeButton.setDisable(true);
                        roadAndCrossingPane.setDisable(true);
                    }
                    case MOVE_DROP -> {
                        freeFieldVisibility(false);
                        rollButton.setDisable(true);
                        arrowOnDice.setVisible(false);
                        finishMoveButton.setDisable(true);
                        tradeButton.setDisable(true);
                        roadAndCrossingPane.setDisable(true);
                        showDropWindow();
                    }
                    case MOVE_ROB -> {
                        freeFieldVisibility(false);
                        rollButton.setDisable(true);
                        arrowOnDice.setVisible(false);
                        finishMoveButton.setDisable(true);
                        tradeButton.setDisable(true);
                        roadAndCrossingPane.setDisable(true);
                        if (!(stateService.getOldAction().equals(MOVE_ROB))) {
                            enableRobberFields();
                        }
                    }
                }
            }
        } else {
            arrowOnDice.setVisible(false);
            yourTurnLabel.setVisible(false);
            rollButton.setDisable(true);
            finishMoveButton.setDisable(true);
            tradeButton.setDisable(true);
            updateFields(false, crossingPane, roadPane);
            roadAndCrossingPane.setDisable(true);
            freeFieldVisibility(false);

            //if robber position has changed (another player moved the robber), then update it
            Point3D point = stateService.getRobberPosition();
            if (point != null) {
                String id = "robberX" + point.x() + "Y" + point.y() + "Z" + point.z();
                id = id.replace("-", "_");
                moveRobber(id);
            }
        }
    }

    private void onPlayerUpdated(EventDto<Player> playerEventDto) {
        Player updatedPlayer = playerEventDto.data();
        if (updatedPlayer.victoryPoints() >= requiredPoints) {
            app.show(postGameController.get());
            return;
        }
        if (updatedPlayer.userId().equals(userID)) {
            playerResourceListController.setPlayer(updatedPlayer);
            playerResourceListController.updateOwnResources(resourceLabels, resourceNames);
            player = updatedPlayer;

            HashMap<String, Integer> resources = updatedPlayer.resources();
            int amountBrick = playerResourceListController.getResource(resources, RESOURCE_BRICK);
            int amountLumber = playerResourceListController.getResource(resources, RESOURCE_LUMBER);
            int amountWool = playerResourceListController.getResource(resources, RESOURCE_WOOL);
            int amountGrain = playerResourceListController.getResource(resources, RESOURCE_GRAIN);
            int amountOre = playerResourceListController.getResource(resources, RESOURCE_ORE);
            int resourceSum = amountBrick + amountLumber + amountWool + amountGrain + amountOre;
            if (resourceSum >= previousResourceSum) {
                previousResourceSum = resourceSum;
                disposables.add(achievementsService.putAchievement(ACHIEVEMENT_RESOURCES, resourceSum).observeOn(FX_SCHEDULER).subscribe());
            }
            enableButtons.put(BUILDING_TYPE_ROAD, amountBrick >= 1 && amountLumber >= 1 && updatedPlayer.remainingBuildings().get(BUILDING_TYPE_ROAD) > 0);
            enableButtons.put(BUILDING_TYPE_SETTLEMENT, (amountBrick >= 1 && amountLumber >= 1 && amountWool >= 1 && amountGrain >= 1 && updatedPlayer.remainingBuildings().get(BUILDING_TYPE_SETTLEMENT) > 0));
            enableButtons.put(BUILDING_TYPE_CITY, (amountOre >= 3 && amountGrain >= 2 && updatedPlayer.remainingBuildings().get(BUILDING_TYPE_CITY) > 0));

            if (tradingController != null) {
                tradingController.updatePlayer(updatedPlayer);
            }
        }
        playerResourceListController.updatePlayerLabel(updatedPlayer);
        statService.updatePlayerStats(updatedPlayer);
    }

    public void finishMove(ActionEvent actionEvent) {
        errorService.setErrorCodesPioneersPost();
        disposables.add(pioneerService.createMove(MOVE_BUILD, null, null, null, null)
                .observeOn(FX_SCHEDULER)
                .subscribe(r -> {
                }, errorService::handleError));
    }

    public void build(ActionEvent event) {
        buildService.build(nextHarbors);
    }

    //enables the circle/rectangle behind buildings and fills it with color
    private void enableBuildingColor(String id, String owner, String type) {
        Node node = null;
        for (Node n : roadAndCrossingPane.getChildren()) {
            if (n.getId().equals(id + "Colour")) {
                node = n;
            }
        }
        if (node == null) {
            return;
        }
        String color = playerColors.get(owner);
        //circles are behind settlements and cities
        if (node instanceof Circle circle) {
            if (circle.getFill().equals(Color.TRANSPARENT)) {
                circle.setFill(Color.web(color));
            }
            //increase color size in case a settlement is upgraded to a city
            if (type.equals(BUILDING_TYPE_CITY)) {
                circle.setRadius(circle.getRadius() * 1.2);
                circle.setLayoutX(circle.getLayoutX() + (circle.getRadius() / 8));
                circle.setLayoutY(circle.getLayoutY() + (circle.getRadius() / 8));
            }
        }
        //rectangles are behind roads
        if (node instanceof Rectangle rectangle) {
            if (rectangle.getFill().equals(Color.TRANSPARENT)) {
                rectangle.setFill(Color.web(color));
            }
        }
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

        errorService.setErrorCodesPioneersPost();
        disposables.add(pioneerService.createMove("roll", null, null, null, null)
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                }, errorService::handleError));
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
        String sideType;
        if (side == 0 || side == 6) {
            if (Objects.equals(buildingType, BUILDING_TYPE_CITY)) {
                return;
            }
            if (Objects.equals(buildingType, BUILDING_TYPE_SETTLEMENT)) {
                if (userID.equals(buildingOwner)) {
                    sideType = BUILDING_TYPE_CITY;
                } else {
                    return;
                }
            } else {
                sideType = BUILDING_TYPE_SETTLEMENT;
            }
        } else {
            if (Objects.equals(buildingType, BUILDING_TYPE_ROAD)) {
                return;
            }
            sideType = BUILDING_TYPE_ROAD;
        }
        buildService.setBuildingType(sideType);
        if (currentAction != null) {
            if (currentAction.startsWith("founding")) {
                buildService.build(nextHarbors);
                return;
            }

        }

        buildMenuController = new BuildMenuController(enableButtons.get(sideType), buildService, bundle, sideType, nextHarbors, this);
        buildMenu = buildMenuController.render();
        buildMenu.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
            buildMenu.setLayoutX(Math.min(source.getX() + 240, ingamePane.getWidth() - newValue.getWidth()));
            buildMenu.setLayoutY(Math.min(source.getY(), ingamePane.getHeight() - newValue.getHeight()));
        });

        ingamePane.getChildren().add(buildMenu);

        // Prevent the event handler from main pane to close the build menu immediately after this
        mouseEvent.consume();
    }

    private String coordsToPath(String source) {
        if (source.startsWith("building")) {
            return source;
        }
        return "building " + source.replace("-", "_");

    }

    @Override
    public void destroy(boolean closed) {
        super.destroy(closed);
        closeBuildMenu(closed);
        closeTradingMenu(closed);
        closeDropMenu(closed);
        if (mapController != null) {
            mapController.destroy(closed);
        }
        if (timer != null) {
            timer.cancel();
        }
        achievementsService.dispose();
    }

    public void closeBuildMenu(boolean appClosed) {
        if (buildMenuController != null) {
            buildMenuController.destroy(appClosed);
            buildMenuController = null;
        }
        if (buildMenu != null) {
            ingamePane.getChildren().remove(buildMenu);
            buildMenu = null;
        }
    }

    public void closeDropMenu(boolean appClosed) {
        if (dropMenuController != null) {
            dropMenuController.destroy(appClosed);
            dropMenuController = null;
        }
        if (dropMenu != null) {
            ingamePane.getChildren().remove(dropMenu);
            dropMenu = null;
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
        GameReadyController controller = gameReadyController.get();
        controller.setComingFromIngame(true);
        app.show(controller);
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
        for (int i = 0; i < memberVP; i++) {
            if (vpCircles.get(i).getFill() != Color.GOLD) {
                vpCircles.get(i).setFill(Color.GOLD);
                if (!rejoin) {
                    int finalI = i;
                    new Thread(() -> {
                        try {
                            vpAnimation(finalI);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                }
            }
        }
    }

    public void vpAnimation(int index) throws InterruptedException {
        double radius = 100.0;
        while (radius >= 10.0) {
            vpCircles.get(index).setRadius(radius);
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
        timer = new Timer();
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
            if (n.getClass().equals(ImageView.class)) {
                ImageView field = (ImageView) n;
                if (field.getImage().getUrl().endsWith("empty.png") || field.getImage().getUrl().endsWith("emptyRoad.png")) {
                    field.setVisible(var);
                }
            }
        }
    }

    private void showDropWindow() {
        //close the menu in case it is still open from last drop (that is a bug, and I did not find the cause yet)
        closeDropMenu(false);

        Player player = pioneerService.getPlayer(userID).blockingFirst();
        HashMap<String, Integer> resources = player.resources();
        dropMenuController = new DropMenuController(this, this.pioneerService, this.bundle, resources);
        dropMenu = dropMenuController.render();

        //maybe change that later to a dynamic value
        int layoutX = 600;
        int layoutY = 250;

        dropMenu.setLayoutX(layoutX);
        dropMenu.setLayoutY(layoutY);

        ingamePane.getChildren().add(dropMenu);
    }

    /************************************************
     ************ Robber-related methods ************
     ************************************************/

    //moves the robber image to the position given by the id
    private void moveRobber(String id) {
        ImageView oldRobberView = getCurrentRobberView();
        ImageView newRobberView = getNewRobberViewPosition(id);
        if (oldRobberView != null) {
            oldRobberView.setImage(null);
        }
        if (newRobberView != null) {
            newRobberView.setImage(robberView);
        }
    }

    //enables all robber fields on the hexagon tiles which are currently empty
    private void enableRobberFields() {
        robberPane.setDisable(false);
        for (Node n : robberPane.getChildren()) {
            //check that n has no image to prevent overwriting the image on the current robber position
            if (((ImageView) n).getImage() == null) {
                n.setDisable(false);
                ((ImageView) n).setImage(emptyCircle);
                n.setOnMouseEntered(this::showRobberField);
                n.setOnMouseExited(this::hideRobberField);
                n.setOnMouseClicked(this::initiateRobberMove);
            }
        }
    }

    private void showRobberField(MouseEvent mouseEvent) {
        ((ImageView) mouseEvent.getSource()).setImage(robberView);
    }

    private void hideRobberField(MouseEvent mouseEvent) {
        ((ImageView) mouseEvent.getSource()).setImage(emptyCircle);
    }

    //this method is called when you try to put the robber on a new position by clicking on a free robber field
    private void initiateRobberMove(MouseEvent mouseEvent) {
        String robberFieldId = ((Node) mouseEvent.getSource()).getId();
        selectedRobberFieldId = robberFieldId;

        //after you have clicked on a free robber position, all rob targets (buildings of other players) have to be marked
        int count = markBuildingsForRob(robberFieldId);

        //if no building is marked, then place the robber without robbing another player
        if (count == 0) {
            changeRobberPositionAndRobTarget(robberFieldId, null);
        }
        //otherwise enable the player to click on the building images to choose a rob target
        else {
            robberPane.setDisable(true);
            roadAndCrossingPane.setDisable(false);
        }
        resetRobberPaneAndMoveRobber(selectedRobberFieldId);
    }

    //marks all buildings of other players at the chosen tile
    private int markBuildingsForRob(String robberFieldId) {
        List<String> possibleIds = new ArrayList<>();
        Point3D p = Point3D.readCoordinatesFromID(robberFieldId);

        //add all building ids from possible buildings around the chosen tile
        possibleIds.add(("buildingX" + (p.x()) + "Y" + (p.y()) + "Z" + (p.z()) + "R0").replace("-", "_"));
        possibleIds.add(("buildingX" + (p.x()) + "Y" + (p.y()) + "Z" + (p.z()) + "R6").replace("-", "_"));
        possibleIds.add(("buildingX" + (p.x() + 1) + "Y" + (p.y()) + "Z" + (p.z() - 1) + "R6").replace("-", "_"));
        possibleIds.add(("buildingX" + (p.x()) + "Y" + (p.y() - 1) + "Z" + (p.z() + 1) + "R0").replace("-", "_"));
        possibleIds.add(("buildingX" + (p.x() - 1) + "Y" + (p.y()) + "Z" + (p.z() + 1) + "R0").replace("-", "_"));
        possibleIds.add(("buildingX" + (p.x()) + "Y" + (p.y() + 1) + "Z" + (p.z() - 1) + "R6").replace("-", "_"));

        int count = 0;
        for (Node n : roadAndCrossingPane.getChildren()) {

            //split id of n in its parts (coordinates, type, owner)
            String[] nIdParts = n.getId().split("#");

            //only nodes which are buildings contain three id parts
            if (nIdParts.length == 3) {
                //check that the building node is at the correct tile and belongs to another player
                if (possibleIds.contains(nIdParts[0]) && !(nIdParts[2].equals(userID))) {
                    n.setOnMouseClicked(this::initiateRob);
                    n.setDisable(false);

                    robTargets.add(n);

                    ((ImageView) n).setFitWidth(((ImageView) n).getFitWidth() * 1.2);
                    ((ImageView) n).setFitHeight(((ImageView) n).getFitHeight() * 1.2);
                    n.setOnMouseEntered(this::increaseGlow);
                    n.setOnMouseExited(this::decreaseGlow);
                    n.setEffect(new Glow(0.7));

                    count++;
                }
            }
        }
        return count;
    }

    private void increaseGlow(MouseEvent mouseEvent) {
        ((Node) mouseEvent.getSource()).setEffect(new Glow(1));
    }

    private void decreaseGlow(MouseEvent mouseEvent) {
        ((Node) mouseEvent.getSource()).setEffect(new Glow(0.7));
    }


    //this method is called when you click on a rob target (building of another player)
    private void initiateRob(MouseEvent mouseEvent) {
        String robberFieldId = selectedRobberFieldId;
        String target = ((Node) mouseEvent.getSource()).getId().split("#")[2];
        changeRobberPositionAndRobTarget(robberFieldId, target);
    }

    //sends a rob move to the server with the new position and the rob target
    private void changeRobberPositionAndRobTarget(String newRobberPosition, String target) {
        Point3D p = Point3D.readCoordinatesFromID(newRobberPosition);

        if (target != null) {
            if (target.equals(userID)) {
                target = null;
            }
        }

        errorService.setErrorCodesPioneersPost();
        disposables.add(this.pioneerService.createMove(MOVE_ROB, null, null, null, new RobDto(p.x(), p.y(), p.z(), target))
                .observeOn(FX_SCHEDULER)
                .subscribe(
                        move -> cleanupRobberMove(),
                        errorService::handleError
                ));
    }

    //this method is called after you clicked on a robber field to already show the robber on this position
    private void resetRobberPaneAndMoveRobber(String newRobberPosition) {
        //reset the robberPane to normal and move the robber view to its new position
        for (Node n : robberPane.getChildren()) {
            n.setDisable(true);
            n.setOnMouseEntered(null);
            n.setOnMouseExited(null);
            n.setOnMouseClicked(null);
            if (n.getId().equals(newRobberPosition)) {
                ((ImageView) n).setImage(robberView);
            } else {
                ((ImageView) n).setImage(null);
                ((ImageView) n).imageProperty().setValue(null);
            }
        }
    }

    //this method is called after a successful rob move for cleaning up
    private void cleanupRobberMove() {
        //reset all changes made to the rob targets (buildings of other players)
        for (Node n : robTargets) {
            n.setOnMouseClicked(this::onFieldClicked);
            n.setDisable(true);

            ((ImageView) n).setFitWidth(((ImageView) n).getFitWidth() / 1.2);
            ((ImageView) n).setFitHeight(((ImageView) n).getFitHeight() / 1.2);
            n.setOnMouseEntered(null);
            n.setOnMouseExited(null);
            n.setEffect(null);

        }
        robTargets.clear();
        roadAndCrossingPane.setDisable(true);
    }

    //gets the image view of the current robber position
    private ImageView getCurrentRobberView() {
        ImageView view = null;
        for (Node n : robberPane.getChildren()) {
            if (((ImageView) n).getImage() != null) {
                view = (ImageView) n;
            }
        }
        return view;
    }

    //gets the image view of the new robber position given by its id
    private ImageView getNewRobberViewPosition(String id) {
        ImageView view = null;
        for (Node n : robberPane.getChildren()) {
            String nID = n.getId();
            if (nID.equals(id)) {
                view = (ImageView) n;
            }
        }
        return view;
    }

    public void trade(ActionEvent actionEvent) {
        tradingController = new TradingController(this, bundle, achievementsService, userService, pioneerService, colorService, errorService, player, buildService.getResourceRatio(), tradeService);
        tradingController.init();
        tradingMenu = tradingController.render();
        tradingMenu.setLayoutX(255);
        tradingMenu.setLayoutY(120);
        ingamePane.getChildren().add(tradingMenu);

        // Prevent the event handler from ingame pane to close the build menu immediately after this
        actionEvent.consume();
    }


    // trade with the bank or another player
    public void showTradeRequest(HashMap<String, Integer> resources, String partner) {
        tradeRessources = resources;
        viewRequestButton.setDisable(false);
        tradeRequestPopup.setStyle("-fx-background-color: #ffffff");
        viewRequestButton.setStyle("-fx-background-color: " + player.color());

        User user = userService.getUserByID(partner).blockingFirst();
        tradePartnerAvatarUrl = user.avatar();
        tradePartner = user.name();

        Player partnerPlayer = pioneerService.getPlayer(partner).blockingFirst();
        tradePartnerColor = colorService.getColor(partnerPlayer.color());
        playerWantTradeLabel.setText(tradePartner + " " + bundle.getString("player.want.trade"));
        tradeRequestPopup.setVisible(true);
    }

    public void viewRequest(ActionEvent actionEvent) {
        tradeRequestController = new TradeRequestController(this, bundle, pioneerService, errorService, tradeRessources, tradePartner, tradePartnerAvatarUrl, tradePartnerColor, colorService.getColor(player.color()), player, tradeService);
        tradeRequestController.init();
        requestMenu = tradeRequestController.render();
        requestMenu.setLayoutX(255);
        requestMenu.setLayoutY(120);
        ingamePane.getChildren().add(requestMenu);

    }

    public void closeTradingMenu(boolean appClosed) {
        if (tradingController != null) {
            tradingController.destroy(appClosed);
            tradingController = null;
        }
        if (tradingMenu != null) {
            ingamePane.getChildren().remove(tradingMenu);
            tradingMenu = null;
        }
    }

    public void closeRequestMenu(boolean appClosed) {
        if (tradeRequestController != null) {
            tradeRequestController.destroy(appClosed);
            tradeRequestController = null;
        }
        if (requestMenu != null) {
            ingamePane.getChildren().remove(requestMenu);
            requestMenu = null;
        }
        if (tradeRequestPopup.isVisible()) {
            tradeRequestPopup.setVisible(false);
        }
    }

    public boolean isTradeStarter() {
        return tradeStarter;
    }

    public void setTradeStarter(boolean tradeStarter) {
        this.tradeStarter = tradeStarter;
    }

    public void setRejoin(boolean rejoin) {
        this.rejoin = rejoin;
    }
}