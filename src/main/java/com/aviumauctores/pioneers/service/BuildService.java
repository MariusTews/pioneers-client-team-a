package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Building;
import com.aviumauctores.pioneers.model.Player;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import retrofit2.HttpException;

import javax.inject.Inject;
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
    private String userID;
    private final ResourceBundle bundle;
    private Player player;
    private String buildingType;
    private String selectedFieldCoordinates;

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
    }


    public void build() {
        if (selectedField == null) {
            return;
        }
        Building b = Building.readCoordinatesFromID(selectedField.getId());
        pioneerService.createMove(currentAction, new Building(b.x(), b.y(), b.z(), b.side(), null, buildingType,
                        gameService.getCurrentGameID(), userID),null,null, null)
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


    public void loadBuildingImage(String buildingID) {
        String color = colorService.getColor(player.color());
        switch (buildingType) {
            case BUILDING_TYPE_SETTLEMENT ->
                    selectedField.setImage(new Image(Objects.requireNonNull(Main.class.getResource
                            ("views/House/house_" + color.toLowerCase() + ".png")).toString()));
            case BUILDING_TYPE_ROAD -> selectedField.setImage(new Image(Objects.requireNonNull(Main.class.getResource
                    ("views/Street/street_" + color.toLowerCase() + ".png")).toString()));
            case BUILDING_TYPE_CITY -> selectedField.setImage(new Image(Objects.requireNonNull(Main.class.getResource(
                    "views/Town/town_" + color.toLowerCase() + ".png")).toString()));
        }
        if (selectedField.getId().startsWith("building")) {
            selectedField.setId(selectedField.getId() + "#" + buildingType + "#" + player.userId());
            return;
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

    public void setPlayer(Player updatedPlayer) {
        player = updatedPlayer;
    }


    public void setSelectedFieldCoordinates(String coordinates) {
        selectedFieldCoordinates = coordinates;
    }

}
