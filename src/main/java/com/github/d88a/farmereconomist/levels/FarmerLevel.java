package com.github.d88a.farmereconomist.levels;

import java.util.UUID;

public class FarmerLevel {
    private final UUID playerId;
    private int level;
    private int experience;

    public FarmerLevel(UUID playerId) {
        this.playerId = playerId;
        this.level = 1;
        this.experience = 0;
    }

    public FarmerLevel(UUID playerId, int level, int experience) {
        this.playerId = playerId;
        this.level = level;
        this.experience = experience;
    }

    public void addExperience(int exp) {
        this.experience += exp;
        recalculateLevel();
    }

    private void recalculateLevel() {
        // Формула: уровень = корень(опыт / 100) + 1
        int newLevel = (int) Math.sqrt(experience / 100.0) + 1;
        if (newLevel > level) {
            level = newLevel;
        }
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }
} 