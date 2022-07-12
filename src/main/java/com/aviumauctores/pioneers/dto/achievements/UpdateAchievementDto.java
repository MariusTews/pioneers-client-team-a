package com.aviumauctores.pioneers.dto.achievements;

public record UpdateAchievementDto(
        String unlockedAt,
        int progress
) {
}
