package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.model.Map;
import com.aviumauctores.pioneers.model.Tile;
import com.aviumauctores.pioneers.service.PioneerService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.HashMap;

public class TileController {

    private final PioneerService pioneerService;
    protected final InGameController inGameController;

    private HashMap<String, TileSubController> tiles = new HashMap<>();

    @Inject
    public TileController(PioneerService pioneerService, InGameController inGameController){
        this.pioneerService = pioneerService;
        this.inGameController = inGameController;
        Map map = pioneerService.getMap().blockingFirst();
        for(Tile t : map.tiles()){
            String tileCoordinate = getCoordinate(t);
            TileSubController controller = new TileSubController(t, tileCoordinate.replace("-", "_"), this);
            tiles.put(tileCoordinate, controller);
        }
    }

    public void onTileSelected(){
    }

    public String getCoordinate(Tile tile){
        return tile.x() + Integer.toString(tile.y()) + tile.z();
    }



}
