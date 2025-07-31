package com.github.d88a.farmereconomist.plots;

import org.bukkit.Location;

import java.util.UUID;

public class Plot {

    private final UUID owner;
    private Location corner1;
    private Location corner2;
    private int level; // Уровень участка (1-4)
    private long creationTime; // Время создания участка

    public Plot(UUID owner, Location corner1, Location corner2) {
        this.owner = owner;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.level = 1;
        this.creationTime = System.currentTimeMillis();
    }

    public Plot(UUID owner, Location corner1, Location corner2, int level, long creationTime) {
        this.owner = owner;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.level = level;
        this.creationTime = creationTime;
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public boolean isLocationInPlot(Location location) {
        int x = location.getBlockX();
        int z = location.getBlockZ();

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }

    public Location getTeleportLocation() {
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int y = corner1.getBlockY();

        // Center of the front edge, just outside the gate
        return new Location(corner1.getWorld(), minX + (maxX - minX) / 2.0, y + 1.0, minZ - 2.0);
    }

    // Методы для расширения участка
    public void expandPlot() {
        if (level >= 4) return; // Максимальный уровень
        
        int currentSize = getSize();
        int newSize = getSizeForLevel(level + 1);
        int expansionPerSide = (newSize - currentSize) / 2;
        
        // Расширяем в обе стороны от центра, чтобы участок оставался на месте
        corner1 = new Location(corner1.getWorld(), 
            corner1.getBlockX() - expansionPerSide, 
            corner1.getBlockY(), 
            corner1.getBlockZ() - expansionPerSide);
            
        corner2 = new Location(corner2.getWorld(), 
            corner2.getBlockX() + expansionPerSide, 
            corner2.getBlockY(), 
            corner2.getBlockZ() + expansionPerSide);
        
        level++;
    }

    public int getSize() {
        return Math.max(
            Math.abs(corner2.getBlockX() - corner1.getBlockX()) + 1,
            Math.abs(corner2.getBlockZ() - corner1.getBlockZ()) + 1
        );
    }

    public static int getSizeForLevel(int level) {
        switch (level) {
            case 1: return 8;
            case 2: return 12;
            case 3: return 16;
            case 4: return 20;
            default: return 8;
        }
    }

    public static int getExpansionCost(int level) {
        switch (level) {
            case 1: return 1000; // Расширение до уровня 2
            case 2: return 2500; // Расширение до уровня 3
            case 3: return 5000; // Расширение до уровня 4
            default: return Integer.MAX_VALUE;
        }
    }

    public boolean canExpand() {
        return level < 4;
    }

    public boolean hasMinimumTime() {
        // 24 часа в миллисекундах
        return System.currentTimeMillis() - creationTime >= 24 * 60 * 60 * 1000;
    }
} 