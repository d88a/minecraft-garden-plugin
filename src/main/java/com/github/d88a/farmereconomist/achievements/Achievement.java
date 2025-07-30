package com.github.d88a.farmereconomist.achievements;

public class Achievement {
    private final String id;
    private final String name;
    private final String description;
    private final int reward;
    private final String icon;

    public Achievement(String id, String name, String description, int reward, String icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.reward = reward;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getReward() {
        return reward;
    }

    public String getIcon() {
        return icon;
    }
} 