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
    
    public int getMinPlotSize() {
        return config.getInt("plots.min_size", 15);
    }
    
    public int getMaxPlotSize() {
        return config.getInt("plots.max_size", 60);
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
} 