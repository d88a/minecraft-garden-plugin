package com.minecraft.garden.managers;

import com.minecraft.garden.GardenPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DataManager {
    
    private final GardenPlugin plugin;
    private File dataFile;
    private FileConfiguration dataConfig;
    
    public DataManager(GardenPlugin plugin) {
        this.plugin = plugin;
        loadData();
    }
    
    private void loadData() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            plugin.saveResource("data.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
    
    public void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить данные: " + e.getMessage());
        }
    }
    
    public void saveAllData() {
        saveData();
    }
    
    public FileConfiguration getData() {
        return dataConfig;
    }
    
    public void reloadData() {
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
} 