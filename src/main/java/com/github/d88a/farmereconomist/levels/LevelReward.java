package com.github.d88a.farmereconomist.levels;

public class LevelReward {
    private final int level;
    private final int moneyReward;
    private final String description;

    public LevelReward(int level, int moneyReward, String description) {
        this.level = level;
        this.moneyReward = moneyReward;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public int getMoneyReward() {
        return moneyReward;
    }

    public String getDescription() {
        return description;
    }
} 