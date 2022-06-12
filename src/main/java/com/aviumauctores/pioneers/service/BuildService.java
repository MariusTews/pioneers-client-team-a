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
                        UserService userService, ColorService colorService, ResourceBundle bundle){

        this.pioneerService = pioneerService;
        this.gameMemberService = gameMemberService;
        this.gameService = gameService;
        this.errorService = errorService;
        this.userService = userService;
        this.colorService = colorService;
        this.userID = userService.getCurrentUserID();
        this.bundle = bundle;
    }



    public void build(){
        if(selectedField == null){
            return;
        }
        Building b = Building.readCoordinatesFromID(selectedField.getId());
        pioneerService.createMove(currentAction, new Building(b.x(), b.y(), b.z(), b.side(), buildingType,
                        gameService.getCurrentGameID(), userID))
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                    loadBuildingImage(move.building(), player);
                }, throwable -> {
                    if (throwable instanceof HttpException ex) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        String content = bundle.getString(buildingType.equals(BUILDING_TYPE_ROAD) ? "road.location.mismatch" : "settlement.location.mismatch");
                        alert.setContentText(content);
                        alert.showAndWait();
                    }
                });

    }


    private void loadBuildingImage(String buildingID, Player player) {
        String color = colorService.getColor( "#" + player.color().substring(2,8));
        switch (buildingType) {
            case BUILDING_TYPE_SETTLEMENT ->
                    selectedField.setImage(new Image(Objects.requireNonNull(Main.class.getResource
                            ("views/House/House_" + color.toUpperCase() + ".png")).toString()));
            case BUILDING_TYPE_ROAD -> selectedField.setImage(new Image(Objects.requireNonNull(Main.class.getResource
                    ("views/Street/Street_" + color.toUpperCase() + ".png")).toString()));
        }
        selectedField.setFitWidth(25.5);
        selectedField.setFitHeight(25.5);
        selectedField.setId(buildingID);

    }

    public void setCurrentAction(String action){
        currentAction = action;

    }

    public void setBuildingType(String type){
        buildingType = type;
    }

    public void setSelectedField(ImageView field) {
        selectedField = field;
    }

    public void setPlayer(Player updatedPlayer){
        player = updatedPlayer;
    }

    private String coordsToPath(String source) {
        String res = null;
        if(source.startsWith("building")){
            return res;
        }
        res = "building " + source.replace("-", "_");
        return res;

    }

    private String pathToCoords(String source){
        String res  = null;
        if(source.startsWith("building")){
            res =  source.substring(8).replace("_", "-");
        }
        return res;
    }

    public void setSelectedFieldCoordinates(String coordinates) {
        selectedFieldCoordinates = coordinates;
    }

}
