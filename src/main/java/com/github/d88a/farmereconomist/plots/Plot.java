package com.github.d88a.farmereconomist.plots;

import org.bukkit.Location;

import java.util.UUID;

public class Plot {

    private final UUID owner;
    private final Location corner1;
    private final Location corner2;

    public Plot(UUID owner, Location corner1, Location corner2) {
        this.owner = owner;
        this.corner1 = corner1;
        this.corner2 = corner2;
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
} 