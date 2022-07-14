package com.aviumauctores.pioneers.model;

public record AchievementSummary(
        String id,
        int started,
        int unlocked,
        int progress
) {
}
