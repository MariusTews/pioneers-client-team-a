package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.model.Player;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Singleton
public class RankingService {

    private final PioneerService pioneerService;
    private final List<Player> ranking = new ArrayList<>();

    @Inject
    public RankingService(PioneerService pioneerService){
        this.pioneerService = pioneerService;
    }
//TODO create HashMap containing the ranking
    public List<Player> createRanking(){
        List<Player> players = pioneerService.listPlayers().blockingFirst();
        System.out.println(players);
        players.sort(Comparator.comparing(Player::victoryPoints));
        for (Player p : players){
            if (players.indexOf(p) == 4){
                break;
            }
            ranking.add(p);
        }
        return ranking;
    }

    public void clear(){
        ranking.clear();
    }
    public List<Player> getRanking(){
        return ranking;
    }

}
