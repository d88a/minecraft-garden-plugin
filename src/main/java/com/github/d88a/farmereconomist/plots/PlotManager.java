package com.github.d88a.farmereconomist.plots;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.github.d88a.farmereconomist.data.DataManager;

public class PlotManager {

    private final FarmerEconomist plugin;
    private final DataManager dataManager;
    private final Map<UUID, Plot> plots = new HashMap<>();
    private int nextPlotId = 0; // Tracks the number of plots to determine the next location

    public PlotManager(FarmerEconomist plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        loadPlots();
    }

    public void savePlots() {
        ConfigurationSection plotsSection = dataManager.getPlotsConfig().createSection("plots");
        for (Map.Entry<UUID, Plot> entry : plots.entrySet()) {
            ConfigurationSection plotSection = plotsSection.createSection(entry.getKey().toString());
            plotSection.set("corner1", entry.getValue().getCorner1());
            plotSection.set("corner2", entry.getValue().getCorner2());
        }
        dataManager.savePlotsConfig();
    }

    private void loadPlots() {
        ConfigurationSection plotsSection = dataManager.getPlotsConfig().getConfigurationSection("plots");
        if (plotsSection != null) {
            for (String uuidString : plotsSection.getKeys(false)) {
                UUID owner = UUID.fromString(uuidString);
                Location corner1 = plotsSection.getLocation(uuidString + ".corner1");
                Location corner2 = plotsSection.getLocation(uuidString + ".corner2");
                Plot plot = new Plot(owner, corner1, corner2);
                plots.put(owner, plot);
            }
        }
        nextPlotId = plots.size();
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

        // Ставим табличку у входа
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int y = corner1.getBlockY();
        int signX = minX + (maxX - minX) / 2;
        int signZ = minZ - 2;
        org.bukkit.block.Block signBlock = world.getBlockAt(signX, y + 1, signZ);
        signBlock.setType(org.bukkit.Material.OAK_SIGN);
        if (signBlock.getState() instanceof org.bukkit.block.Sign) {
            org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signBlock.getState();
            sign.setLine(0, "§aОгород");
            sign.setLine(1, player.getName());
            double balance = plugin.getEconomyManager().getBalance(player);
            sign.setLine(2, "Баланс: " + balance);
            sign.setLine(3, "");
            sign.update();
        }

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

        // Удаляем блоки сверху забора и очищаем место для таблички
        for (int x = minX - 1; x <= maxX + 1; x++) {
            // Удаляем блоки сверху забора
            for (int yClear = y + 2; yClear < y + 10; yClear++) {
                world.getBlockAt(x, yClear, minZ - 1).setType(Material.AIR);
                world.getBlockAt(x, yClear, maxZ + 1).setType(Material.AIR);
            }
            world.getBlockAt(x, y + 1, minZ - 1).setType(fence);
            // Новый код: слой земли под забором
            world.getBlockAt(x, y, minZ - 1).setType(Material.DIRT);
            world.getBlockAt(x, y + 1, maxZ + 1).setType(fence);
            world.getBlockAt(x, y, maxZ + 1).setType(Material.DIRT);
        }
        for (int z = minZ; z <= maxZ; z++) {
            // Удаляем блоки сверху забора
            for (int yClear = y + 2; yClear < y + 10; yClear++) {
                world.getBlockAt(minX - 1, yClear, z).setType(Material.AIR);
                world.getBlockAt(maxX + 1, yClear, z).setType(Material.AIR);
            }
            world.getBlockAt(minX - 1, y + 1, z).setType(fence);
            world.getBlockAt(minX - 1, y, z).setType(Material.DIRT);
            world.getBlockAt(maxX + 1, y + 1, z).setType(fence);
            world.getBlockAt(maxX + 1, y, z).setType(Material.DIRT);
        }

        // Add gate
        world.getBlockAt(minX + (maxX - minX) / 2, y + 1, minZ - 1).setType(gate);
        
        // Очищаем место для таблички
        int signX = minX + (maxX - minX) / 2;
        int signZ = minZ - 2;
        for (int yClear = y + 2; yClear < y + 10; yClear++) {
            world.getBlockAt(signX, yClear, signZ).setType(Material.AIR);
        }
    }

    public void deletePlot(Player player) {
        Plot plot = plots.remove(player.getUniqueId());
        if (plot != null) {
            // Remove from config
            dataManager.getPlotsConfig().set("plots." + player.getUniqueId().toString(), null);
            dataManager.savePlotsConfig();
            
            // Clean up the plot area
            clearPlotArea(plot);
        }
    }
    
    private void clearPlotArea(Plot plot) {
        Location corner1 = plot.getCorner1();
        Location corner2 = plot.getCorner2();
        World world = corner1.getWorld();

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        int y = corner1.getBlockY();

        // Remove fence and turn land to stone
        for (int x = minX - 1; x <= maxX + 1; x++) {
            for (int z = minZ - 1; z <= maxZ + 1; z++) {
                // Clear fence level
                world.getBlockAt(x, y + 1, z).setType(Material.AIR);
                // Turn plot land to stone
                if (x >= minX && x <= maxX && z >= minZ && z <= maxZ) {
                    world.getBlockAt(x, y, z).setType(Material.STONE);
                }
            }
        }
    }

    public void updatePlotSign(Player player) {
        Plot plot = getPlot(player);
        if (plot == null) return;
        Location corner1 = plot.getCorner1();
        Location corner2 = plot.getCorner2();
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int y = corner1.getBlockY();
        int signX = minX + (maxX - minX) / 2;
        int signZ = minZ - 2;
        World world = corner1.getWorld();
        org.bukkit.block.Block signBlock = world.getBlockAt(signX, y + 1, signZ);
        if (signBlock.getState() instanceof org.bukkit.block.Sign) {
            org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signBlock.getState();
            sign.setLine(2, "Баланс: " + plugin.getEconomyManager().getBalance(player));
            sign.update();
        }
    }
} 