package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Building;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import retrofit2.HttpException;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import javax.inject.Inject;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.*;

public class BuildService {

    protected CompositeDisposable disposables;

    private final PioneerService pioneerService;
    private final GameMemberService gameMemberService;
    private final GameService gameService;

    private final AchievementsService achievementsService;
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

    @Inject
    public BuildService(PioneerService pioneerService, GameMemberService gameMemberService, GameService gameService, AchievementsService achievementsService, ErrorService errorService,
                        UserService userService, ColorService colorService, ResourceBundle bundle) {

        this.pioneerService = pioneerService;
        this.gameMemberService = gameMemberService;
        this.gameService = gameService;
        this.achievementsService = achievementsService;
        this.errorService = errorService;
        this.userService = userService;
        this.colorService = colorService;
        this.userID = userService.getCurrentUserID();
        this.bundle = bundle;
    }


    public void build() {
        if (selectedField == null) {
            return;
        }
        Building b = Building.readCoordinatesFromID(selectedField.getId());
        if (b != null) {
            pioneerService.createMove(currentAction, new Building(b.x(), b.y(), b.z(), b.side(), null, buildingType,
                            gameService.getCurrentGameID(), userID), null, null, null)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(move -> {
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


    public void loadBuildingImage(String buildingID) {
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
                case BUILDING_TYPE_SETTLEMENT -> {
                    selectedField.setImage(new Image(Objects.requireNonNull(Main.class.getResource
                        ("views/buildings/settlement.png")).toString()));
                    disposables.add(achievementsService.putAchievement(ACHIEVEMENT_SETTLEMENTS, 1).observeOn(FX_SCHEDULER).subscribe());
                }
                case BUILDING_TYPE_ROAD -> {
                    selectedField.setImage(new Image(Objects.requireNonNull(Main.class.getResource
                            ("views/buildings/road.png")).toString()));
                    disposables.add(achievementsService.putAchievement(ACHIEVEMENT_ROADS, 1).observeOn(FX_SCHEDULER).subscribe());
                }
                case BUILDING_TYPE_CITY -> {
                    selectedField.setImage(new Image(Objects.requireNonNull(Main.class.getResource(
                            "views/buildings/town.png")).toString()));
                    disposables.add(achievementsService.putAchievement(ACHIEVEMENT_CITIES, 1).observeOn(FX_SCHEDULER).subscribe());
                }
            }
            if (selectedField.getId().startsWith("building")) {
                selectedField.setId(selectedField.getId() + "#" + buildingType + "#" + playerId);
            }
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
}
