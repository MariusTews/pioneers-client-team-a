package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.controller.PlayerResourceListController;
import com.aviumauctores.pioneers.model.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;

import static com.aviumauctores.pioneers.Constants.*;

@Singleton
public class StatService {

    private final HashMap<String, PlayerStatService> playerServices = new HashMap<>();

    private final PioneerService pioneerService;
    private final PlayerResourceListController playerResourceListController;

    @Inject
    public StatService(PioneerService pioneerService, PlayerResourceListController playerResourceListController){
        this.pioneerService = pioneerService;
        this.playerResourceListController = playerResourceListController;
    }

    public void init(){
        for (Player p : pioneerService.listPlayers().blockingFirst()){
            String playerID = p.userId();
            PlayerStatService service = new PlayerStatService();
            playerServices.put(playerID, service);
        }
        System.out.println(playerServices);
    }

    public HashMap<String, Integer> getStats(String playerID){
        PlayerStatService service = playerServices.get(playerID);
        return service.getAllStats();
    }

    public int getSingleStat(String playerID, String statName){
        PlayerStatService service = playerServices.get(playerID);
        return service.getSingleStat(statName);
    }


    public void updatePlayerStats(Player player){
        String id = player.userId();
        updateResourceStat(id);
        setLongestRoad(id, player.longestRoad());
    }

    private void updateResourceStat(String id){
        int gain = playerResourceListController.getResources(id) - playerResourceListController.getPreviousResources(id);
        if (gain > 0){
            playerServices.get(id).increaseStat(STAT_RESOURCES_GAINED, gain);
        }
    }

    private void setLongestRoad(String id, int length){
        if (playerServices.containsKey(id)) {
            playerServices.get(id).setStat(STAT_LONGEST_ROAD, length);
        }
    }

    public void onBuildingBuilt(String owner, String type) {
        PlayerStatService service = playerServices.get(owner);
        service.increaseStat(getBuildingStat(type));
    }

    private String getBuildingStat(String type) {
        if (type.equals(BUILDING_TYPE_CITY)){
            return STAT_CITIES_BUILT;
        }
        return type.equals(BUILDING_TYPE_SETTLEMENT) ? STAT_SETTLEMENTS_BUILT : STAT_ROADS_BUILT;
    }

    public void playerRobbed(String id) {
        int amount = playerResourceListController.getPreviousResources(id) - playerResourceListController.getResources(id) ;
        playerServices.get(id).increaseStat(STAT_RESOURCES_LOST, amount);
    }
}
