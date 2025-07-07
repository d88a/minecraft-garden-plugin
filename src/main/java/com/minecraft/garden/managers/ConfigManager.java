package com.minecraft.garden.managers;

import com.minecraft.garden.GardenPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final GardenPlugin plugin;
    private FileConfiguration config;
    
    public ConfigManager(GardenPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    private void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    // Геттеры для конфигурации
    public int getStartingBalance() {
        return config.getInt("economy.starting_balance", 10);
    }
    
    public int getAreaStartX() {
        return config.getInt("plots.area_start_x", 198);
    }
    
    public int getAreaEndX() {
        return config.getInt("plots.area_end_x", 314);
    }
    
    public int getAreaStartZ() {
        return config.getInt("plots.area_start_z", 1334);
    }
    
    public int getAreaEndZ() {
        return config.getInt("plots.area_end_z", 1467);
    }
    
    public int getAreaHeight() {
        return config.getInt("plots.area_height", 62);
    }
    
    public int getInitialPlotSize() {
        return config.getInt("plots.initial_size", 15);
    }
    
    public int getMaxPlotSize() {
        return config.getInt("plots.max_size", 60);
    }
    
    public int getPathWidth() {
        return config.getInt("plots.path_width", 2);
    }
    
    public int getFenceSpacing() {
        return config.getInt("plots.fence_spacing", 1);
    }
    
    public String getWorldName() {
        return config.getString("plots.world", "world");
    }
    
    public int getStartX() {
        return config.getInt("plots.start_x", 1000);
    }
    
    public int getStartZ() {
        return config.getInt("plots.start_z", 1000);
    }
    
    public int getPlotSpacing() {
        return config.getInt("plots.spacing", 2);
    }
    
    // Методы для растений
    public int getCropGrowthTime(String cropName) {
        return config.getInt("plants." + cropName + ".growth_time", 60);
    }
    
    public int getCropStages(String cropName) {
        return config.getInt("plants." + cropName + ".stages", 8);
    }
    
    // Методы для экономики
    public int getSeedPrice(String seedName) {
        return config.getInt("economy.seed_prices." + seedName, 5);
    }
    
    public int getCropPrice(String cropName) {
        return config.getInt("economy.crop_prices." + cropName, 10);
    }
    
    public int getPlotExpansionCost() {
        return config.getInt("economy.plot_expansion_cost", 1);
    }
    
    public int getAutoWateringCost() {
        return config.getInt("economy.auto_watering_cost", 5);
    }
} 