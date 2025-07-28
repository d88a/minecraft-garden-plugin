package com.github.d88a.farmereconomist.plots;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlotManager {

    private final FarmerEconomist plugin;
    private final Map<UUID, Plot> plots = new HashMap<>();
    private int nextPlotId = 0; // Tracks the number of plots to determine the next location

    public PlotManager(FarmerEconomist plugin) {
        this.plugin = plugin;
        // TODO: Load plots from storage and update nextPlotId
    }

    public void addPlot(Player player, Plot plot) {
        plots.put(player.getUniqueId(), plot);
        nextPlotId++;
    }

    public Plot getPlot(Player player) {
        return plots.get(player.getUniqueId());
    }

    public boolean hasPlot(Player player) {
        return plots.containsKey(player.getUniqueId());
    }

    public Plot getPlotAt(org.bukkit.Location location) {
        for (Plot plot : plots.values()) {
            if (plot.isLocationInPlot(location)) {
                return plot;
            }
        }
        return null;
    }

    public Plot createNewPlotFor(Player player) {
        Location startLocation = plugin.getConfigManager().getGridStartLocation();
        if (startLocation == null) {
            return null; // Grid start not set
        }

        int plotSize = plugin.getConfigManager().getPlotSize();
        int plotGap = plugin.getConfigManager().getPlotGap();
        int totalSize = plotSize + plotGap;

        // Spiral algorithm to find the next plot location
        int x = 0, z = 0;
        int dx = 0, dz = -1;
        int layer = 0;
        for (int i = 0; i < nextPlotId; i++) {
            if (x == layer && z == -layer) {
                dx = 0; dz = 1;
                x++;
                layer++;
            } else if (x == layer && z == layer) { dx = -1; dz = 0; z--; }
            else if (x == -layer && z == layer) { dx = 0; dz = -1; x--; }
            else if (x == -layer && z == -layer) { dx = 1; dz = 0; z++; }
            else { x += dx; z += dz; }
        }

        World world = startLocation.getWorld();
        int plotX = startLocation.getBlockX() + x * totalSize;
        int plotZ = startLocation.getBlockZ() + z * totalSize;
        int plotY = startLocation.getBlockY();

        Location corner1 = new Location(world, plotX, plotY, plotZ);
        Location corner2 = new Location(world, plotX + plotSize - 1, plotY, plotZ + plotSize - 1);

        terraformAndBuild(corner1, corner2);

        Plot newPlot = new Plot(player.getUniqueId(), corner1, corner2);
        addPlot(player, newPlot);
        return newPlot;
    }

    private void terraformAndBuild(Location corner1, Location corner2) {
        World world = corner1.getWorld();
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        int y = corner1.getBlockY();

        // Terraforming
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                world.getBlockAt(x, y, z).setType(Material.DIRT);
                // Clear space above
                for (int yClear = y + 1; yClear < y + 10; yClear++) {
                    world.getBlockAt(x, yClear, z).setType(Material.AIR);
                }
            }
        }

        // Build fence
        Material fence = plugin.getConfigManager().getFenceMaterial();
        Material gate = plugin.getConfigManager().getGateMaterial();

        for (int x = minX - 1; x <= maxX + 1; x++) {
            world.getBlockAt(x, y + 1, minZ - 1).setType(fence);
            world.getBlockAt(x, y + 1, maxZ + 1).setType(fence);
        }
        for (int z = minZ; z <= maxZ; z++) {
            world.getBlockAt(minX - 1, y + 1, z).setType(fence);
            world.getBlockAt(maxX + 1, y + 1, z).setType(fence);
        }

        // Add gate
        world.getBlockAt(minX + (maxX - minX) / 2, y + 1, minZ - 1).setType(gate);
    }
} 