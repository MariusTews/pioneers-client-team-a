package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.model.Tile;
import javafx.event.ActionEvent;

import javax.inject.Inject;

public class TileSubController {

    private Tile tile;

    private String contentID;
    private final TileController controller;


    public TileSubController(Tile tile, String coordinate, TileController controller){
        this.tile = tile;
        this.contentID = coordinate;
        this.controller = controller;
    }

    public void onTileClicked(ActionEvent event){

    }

    public void loadTile(){
    }


}
