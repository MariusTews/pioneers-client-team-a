package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Building;
import com.aviumauctores.pioneers.model.Player;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import retrofit2.HttpException;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.*;

public class BuildService {

    private final PioneerService pioneerService;
    private final GameMemberService gameMemberService;
    private final GameService gameService;
    private final ErrorService errorService;
    private final UserService userService;
    private final ColorService colorService;
    private ImageView selectedField;
    private String currentAction;
    private final String userID;
    private final ResourceBundle bundle;
    private String playerId;
    private String buildingType;
    private String selectedFieldCoordinates;

    private final HashMap<String, Integer> resourceRatio;

    @Inject
    public BuildService(PioneerService pioneerService, GameMemberService gameMemberService, GameService gameService, ErrorService errorService,
                        UserService userService, ColorService colorService, ResourceBundle bundle) {

        this.pioneerService = pioneerService;
        this.gameMemberService = gameMemberService;
        this.gameService = gameService;
        this.errorService = errorService;
        this.userService = userService;
        this.colorService = colorService;
        this.userID = userService.getCurrentUserID();
        this.bundle = bundle;
        resourceRatio = new HashMap<>();
        resourceRatio.put(RESOURCE_LUMBER, 4);
        resourceRatio.put(RESOURCE_BRICK, 4);
        resourceRatio.put(RESOURCE_GRAIN, 4);
        resourceRatio.put(RESOURCE_ORE, 4);
        resourceRatio.put(RESOURCE_WOOL, 4);
    }


    public void build(HashMap<String, List<String>> harborCrossings) {
        String field = selectedField.getId().replace("_", "-");
        if (selectedField == null) {
            return;
        }
        Building b = Building.readCoordinatesFromID(selectedField.getId());
        if (b != null) {
            pioneerService.createMove(currentAction, new Building(b.x(), b.y(), b.z(), b.side(), null, buildingType,
                            gameService.getCurrentGameID(), userID), null, null, null)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(move -> {
                                if (Objects.equals(buildingType, "settlement")) {
                                    this.checkNewResourceRatio(harborCrossings, field);
                                }
                            }
                            , throwable -> {
                                if (throwable instanceof HttpException) {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    String content = bundle.getString(buildingType.equals(BUILDING_TYPE_ROAD) ? "road.location.mismatch" : "settlement.location.mismatch");
                                    alert.setContentText(content);
                                    alert.showAndWait();
                                }
                            });

        }

    }

    private void checkNewResourceRatio(HashMap<String, List<String>> harborCrossings, String field) {
        for (String crossing : harborCrossings.get(null)) {
            if (Objects.equals(crossing, field)) {
                if (resourceRatio.get(RESOURCE_BRICK) > 3) {
                    resourceRatio.put(RESOURCE_BRICK, 3);
                }
                if (resourceRatio.get(RESOURCE_ORE) > 3) {
                    resourceRatio.put(RESOURCE_ORE, 3);
                }
                if (resourceRatio.get(RESOURCE_GRAIN) > 3) {
                    resourceRatio.put(RESOURCE_GRAIN, 3);
                }
                if (resourceRatio.get(RESOURCE_LUMBER) > 3) {
                    resourceRatio.put(RESOURCE_LUMBER, 3);
                }
                if (resourceRatio.get(RESOURCE_WOOL) > 3) {
                    resourceRatio.put(RESOURCE_WOOL, 3);
                }
                break;
            }
        }
        this.checkTradingRatioTwotoOne(harborCrossings, RESOURCE_BRICK, field);
        this.checkTradingRatioTwotoOne(harborCrossings, RESOURCE_GRAIN, field);
        this.checkTradingRatioTwotoOne(harborCrossings, RESOURCE_LUMBER, field);
        this.checkTradingRatioTwotoOne(harborCrossings, RESOURCE_ORE, field);
        this.checkTradingRatioTwotoOne(harborCrossings, RESOURCE_WOOL, field);

    }

    private void checkTradingRatioTwotoOne(HashMap<String, List<String>> harborCrossings, String resource, String field) {
        for (String crossing : harborCrossings.get(resource)) {
            if (Objects.equals(crossing, field)) {
                if (resourceRatio.get(resource) > 2) {
                    resourceRatio.put(resource, 2);
                    break;
                }
            }
        }
    }


    public void loadBuildingImage() {
        if (selectedField != null) {
            switch (buildingType) {
                case BUILDING_TYPE_SETTLEMENT ->
                        selectedField.setImage(new Image(Objects.requireNonNull(Main.class.getResource
                                ("views/buildings/settlement.png")).toString()));
                case BUILDING_TYPE_ROAD ->
                        selectedField.setImage(new Image(Objects.requireNonNull(Main.class.getResource
                                ("views/buildings/road.png")).toString()));
                case BUILDING_TYPE_CITY -> {
                    selectedField.setImage(new Image(Objects.requireNonNull(Main.class.getResource(
                            "views/buildings/town.png")).toString()));
                    //increase image view size in case a settlement is upgraded to a city
                    selectedField.setFitWidth(selectedField.getFitWidth() * 1.2);
                    selectedField.setFitHeight(selectedField.getFitHeight() * 1.2);
                }
            }
            if (selectedField.getId().startsWith("building")) {
                selectedField.setId(selectedField.getId() + "#" + buildingType + "#" + playerId);
            }
            //necessary because of runtime issues on rejoin
            selectedField.setVisible(true);
        }
    }

    public void setCurrentAction(String action) {
        currentAction = action;

    }

    public void setBuildingType(String type) {
        buildingType = type;
    }

    public void setSelectedField(ImageView field) {
        selectedField = field;
    }

    public void setPlayerId(String id) {
        playerId = id;
    }


    public void setSelectedFieldCoordinates(String coordinates) {
        selectedFieldCoordinates = coordinates;
    }

    public HashMap<String, Integer> getResourceRatio() {
        return this.resourceRatio;
    }
}
