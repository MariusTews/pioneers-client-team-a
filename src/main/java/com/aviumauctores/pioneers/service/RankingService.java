package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.model.Player;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Singleton
public class RankingService {

    private final PioneerService pioneerService;
    private final HashMap<Integer, Player>  ranking= new HashMap<>();
    @Inject
    public RankingService(PioneerService pioneerService){
        this.pioneerService = pioneerService;
    }
    public HashMap<Integer, Player> createRanking(){
        List<Player> players = pioneerService.listPlayers().blockingFirst();
        System.out.println(players);
        players.sort(Comparator.comparing(Player::victoryPoints));
        for (int i = players.size() - 1; i >= 0; i--){
            ranking.put(players.size() - i - 1, players.get(i));
        }
        return ranking;
    }

    public void clear(){
        ranking.clear();
    }

}
