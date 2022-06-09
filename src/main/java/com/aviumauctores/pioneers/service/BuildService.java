package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.controller.BuildMenuController;
import com.aviumauctores.pioneers.model.Building;
import com.aviumauctores.pioneers.model.Player;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import java.util.Objects;

import static com.aviumauctores.pioneers.Constants.*;
import static com.aviumauctores.pioneers.Constants.BUILDING_TYPE_ROAD;

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
    private Player player;
    private String buildingType;
    private Building foundingSettlement;
    private String selectedFieldCoordinates;

    @Inject
    public BuildService(PioneerService pioneerService, GameMemberService gameMemberService, GameService gameService, ErrorService errorService,
                        UserService userService, ColorService colorService){

        this.pioneerService = pioneerService;
        this.gameMemberService = gameMemberService;
        this.gameService = gameService;
        this.errorService = errorService;
        this.userService = userService;
        this.colorService = colorService;
        this.userID = userService.getCurrentUserID();
    }

    public void buildFoundingRoad(){
        char num = currentAction.charAt(currentAction.length() - 1);
        Building b = Building.readCoordinatesFromID(selectedField.getId());
        pioneerService.createMove(currentAction, new Building(b.x(), b.y(), b.z(), b.side(), BUILDING_TYPE_ROAD,
                        gameService.getCurrentGameID(), userID))
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                    loadBuildingImage(move.building(), player);
                    foundingSettlement = null;
                });


    }

    public void buildFoundingSettlement(){
        char num = currentAction.charAt(currentAction.length() - 1);
        Building b = Building.readCoordinatesFromID(selectedField.getId());
        System.out.println(currentAction);
        pioneerService.createMove(currentAction, new Building(b.x(), b.y(), b.z(), b.side(), BUILDING_TYPE_SETTLEMENT,
                        gameService.getCurrentGameID(), userID))
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                    loadBuildingImage(move.building(), player);
                    foundingSettlement = b;
                });

        if(num == 2) {
            collectResources();
        }


    }

    private void collectResources() {
    }

    public void buildRoad(){
        if(currentAction.startsWith("founding-road")){
            buildFoundingRoad();
            return;
        }else{
            Building b = Building.readCoordinatesFromID(selectedField.getId());
            pioneerService.createMove(currentAction, new Building(b.x(), b.y(), b.z(), b.side(), BUILDING_TYPE_ROAD,
                            gameService.getCurrentGameID(), userID))
                    .observeOn(FX_SCHEDULER)
                    .subscribe(move -> {
                        loadBuildingImage(move.building(), player);
                    });
        }
    }


    public void buildSettlement() {
        if(currentAction.startsWith("founding-settlement")){
            buildFoundingSettlement();
            return;
        }else{
            Building b = Building.readCoordinatesFromID(selectedField.getId());
            pioneerService.createMove(MOVE_BUILD, new Building(b.x(), b.y(), b.z(), b.side(), BUILDING_TYPE_SETTLEMENT,
                            gameService.getCurrentGameID(), userID))
                    .observeOn(FX_SCHEDULER)
                    .subscribe(move -> {
                        loadBuildingImage(move.building(), player);
                    });
        }
        // build a settlement (if possible), then gain 1 VP
        //gainVP(1);
    }


    public void build(){
        if(selectedField == null){
            System.out.println("Please select a Field first");
            return;
        }
        switch (buildingType){
            case BUILDING_TYPE_SETTLEMENT -> buildSettlement();
            case BUILDING_TYPE_ROAD -> buildRoad();
            case BUILDING_TYPE_CITY -> buildCity();
        }

    }

    private void buildCity() {
        //TODO: build a city
    }

    private void loadBuildingImage(String buildingID, Player player) {
        String color = colorService.getColor(player.color());
        switch (buildingType) {
            case BUILDING_TYPE_SETTLEMENT:
                selectedField.setImage(new Image(Objects.requireNonNull(Main.class.getResource
                        ("views/House/House_" + color.toUpperCase() + ".png")).toString()));
                break;
            case BUILDING_TYPE_ROAD:
                selectedField.setImage(new Image(Objects.requireNonNull(Main.class.getResource
                        ("views/Street/Street_" + color.toUpperCase() + ".png")).toString()));
                break;
        }
        selectedField.setFitWidth(25.5);
        selectedField.setFitHeight(25.5);
        selectedField.setId(buildingID);
        selectedField = null;
        selectedFieldCoordinates = null;
        //should be closed after a building is placed
        //closeBuildMenu(true);
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
            System.out.println(res);
        }
        return res;
    }

    public void setSelectedFieldCoordinates(String coordinates) {
        selectedFieldCoordinates = coordinates;
    }

    public void buildTown() {
        // upgrade a settlement to a town (if possible), then
        //gainVP(1);
    }
}
