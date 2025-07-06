package com.minecraft.garden.managers;

import com.minecraft.garden.GardenPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlotManager {
    
    private final GardenPlugin plugin;
    private final Map<UUID, PlotData> playerPlots;
    private int nextPlotId = 0;
    
    public PlotManager(GardenPlugin plugin) {
        this.plugin = plugin;
        this.playerPlots = new HashMap<>();
        loadPlots();
    }
    
    private void loadPlots() {
        // Загрузка участков из данных
        ConfigurationSection plotsSection = plugin.getDataManager().getData().getConfigurationSection("plots");
        if (plotsSection != null) {
            for (String uuidString : plotsSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    ConfigurationSection plotSection = plotsSection.getConfigurationSection(uuidString);
                    if (plotSection != null) {
                        PlotData plotData = new PlotData();
                        plotData.id = plotSection.getInt("id");
                        plotData.x = plotSection.getInt("x");
                        plotData.z = plotSection.getInt("z");
                        plotData.size = plotSection.getInt("size", plugin.getConfigManager().getMinPlotSize());
                        playerPlots.put(uuid, plotData);
                        
                        if (plotData.id >= nextPlotId) {
                            nextPlotId = plotData.id + 1;
                        }
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Неверный UUID в данных участка: " + uuidString);
                }
            }
        }
    }
    
    public void savePlots() {
        // Сохранение участков в данные
        ConfigurationSection plotsSection = plugin.getDataManager().getData().createSection("plots");
        for (Map.Entry<UUID, PlotData> entry : playerPlots.entrySet()) {
            ConfigurationSection plotSection = plotsSection.createSection(entry.getKey().toString());
            PlotData plot = entry.getValue();
            plotSection.set("id", plot.id);
            plotSection.set("x", plot.x);
            plotSection.set("z", plot.z);
            plotSection.set("size", plot.size);
        }
        plugin.getDataManager().saveData();
    }
    
    public boolean hasPlot(UUID playerUuid) {
        return playerPlots.containsKey(playerUuid);
    }
    
    public PlotData getPlot(UUID playerUuid) {
        return playerPlots.get(playerUuid);
    }
    
    public PlotData createPlot(UUID playerUuid) {
        if (hasPlot(playerUuid)) {
            return null; // У игрока уже есть участок
        }
        
        // Простое выделение участка (в реальной версии будет более сложная логика)
        int plotX = plugin.getConfigManager().getStartX() + (nextPlotId * (plugin.getConfigManager().getMinPlotSize() + 2));
        int plotZ = plugin.getConfigManager().getStartZ();
        
        PlotData plotData = new PlotData();
        plotData.id = nextPlotId++;
        plotData.x = plotX;
        plotData.z = plotZ;
        plotData.size = plugin.getConfigManager().getMinPlotSize();
        
        playerPlots.put(playerUuid, plotData);
        savePlots();
        
        return plotData;
    }
    
    public static class PlotData {
        public int id;
        public int x;
        public int z;
        public int size;
        
        public Location getCenterLocation(World world) {
            return new Location(world, x + size/2, 64, z + size/2);
        }
        
        public boolean isInPlot(Location location) {
            return location.getBlockX() >= x && location.getBlockX() < x + size &&
                   location.getBlockZ() >= z && location.getBlockZ() < z + size;
        }
    }
} 