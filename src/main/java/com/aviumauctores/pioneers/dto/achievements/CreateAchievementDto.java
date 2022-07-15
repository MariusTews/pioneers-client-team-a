package com.aviumauctores.pioneers.dto.achievements;

public record CreateAchievementDto(
        String unlockedAt,
        int progress
) {
}
