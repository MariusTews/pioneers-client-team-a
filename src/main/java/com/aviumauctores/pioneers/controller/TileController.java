package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.model.Map;
import com.aviumauctores.pioneers.model.Tile;
import com.aviumauctores.pioneers.service.PioneerService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.HashMap;

public class TileController {

    private final PioneerService pioneerService;

    private HashMap<String, TileSubController> tiles = new HashMap<String, TileSubController>();

    @Inject
    public TileController(PioneerService pioneerService){
        this.pioneerService = pioneerService;
        Map map = pioneerService.getMap().blockingFirst();
        for(Tile t : map.tiles()){
            String coordinate = Integer.toString(t.x()) + ", " + Integer.toString(t.y()) + ", " + Integer.toString(t.z());
            System.out.println(coordinate);
        }
    }

}
