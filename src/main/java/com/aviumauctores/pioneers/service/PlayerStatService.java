package com.aviumauctores.pioneers.service;

import java.util.HashMap;

import static com.aviumauctores.pioneers.Constants.*;

public class PlayerStatService {

    private final HashMap<String, Integer> stats = new HashMap<>();

    public PlayerStatService() {
        for (String stat : ALL_STAT_NAMES) {
            setStat(stat, 0);
        }
    }

    public void increaseStat(String statName) {
        increaseStat(statName, 1);
    }

    public void increaseStat(String statName, int amount) {
        if (stats.containsKey(statName)) {
            int newAmount = stats.get(statName) + amount;
            setStat(statName, newAmount);
        }
    }

    public void setStat(String statName, int newAmount) {
        stats.put(statName, newAmount);
    }

    public int getSingleStat(String statName) {
        return stats.getOrDefault(statName, 0);
    }

    public HashMap<String, Integer> getAllStats() {
        return stats;
    }
}
