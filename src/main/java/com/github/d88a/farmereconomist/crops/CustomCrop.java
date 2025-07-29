package com.github.d88a.farmereconomist.crops;

import org.bukkit.Location;

public class CustomCrop {

    public enum CropType {
        TOMATO,
        GLOWSHROOM
    }

    private final Location location;
    private final CropType type;
    private int stage;
    private long lastGrowthTime;

    public CustomCrop(Location location, CropType type) {
        this.location = location;
        this.type = type;
        this.stage = 0;
        this.lastGrowthTime = System.currentTimeMillis();
    }

    public Location getLocation() {
        return location;
    }

    public CropType getType() {
        return type;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public long getLastGrowthTime() {
        return lastGrowthTime;
    }

    public void setLastGrowthTime(long lastGrowthTime) {
        this.lastGrowthTime = lastGrowthTime;
    }
} 