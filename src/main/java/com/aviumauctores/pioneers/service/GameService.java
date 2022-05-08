package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.games.CreateGameDto;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;
import javax.inject.Inject;
import java.util.List;




public class GameService {

    private final GamesApiService service;


    @Inject
    public GameService(GamesApiService service){
        this.service = service;
    }

    public Observable<String> create(String name, String password){
        return service.createGame(new CreateGameDto(name, password))
                .map(Game::_id);

    }

    public Observable<List<Game>> listGames(){
        return this.service.listGames();
    }

    public void deleteGame(String id){

    }
}
