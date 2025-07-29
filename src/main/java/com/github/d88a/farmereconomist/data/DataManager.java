package com.github.d88a.farmereconomist.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.github.d88a.farmereconomist.FarmerEconomist;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DataManager {

    private final FarmerEconomist plugin;
    private final File dataFolder;
    private FileConfiguration plotsConfig = null;
    private File plotsFile = null;
    private FileConfiguration cropsConfig = null;
    private File cropsFile = null;

    public DataManager(FarmerEconomist plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public void reloadCropsConfig() {
        if (cropsFile == null) {
            cropsFile = new File(plugin.getDataFolder(), "crops.yml");
        }
        cropsConfig = YamlConfiguration.loadConfiguration(cropsFile);
    }

    public FileConfiguration getCropsConfig() {
        if (cropsConfig == null) {
            reloadCropsConfig();
        }
        return cropsConfig;
    }

    public void saveCropsConfig() {
        if (cropsConfig == null || cropsFile == null) {
            return;
        }
        try {
            getCropsConfig().save(cropsFile);
        } catch (IOException ex) {
            plugin.getLogger().severe("Could not save config to " + cropsFile);
            ex.printStackTrace();
        }
    }

    public void reloadPlotsConfig() {
        if (plotsFile == null) {
            plotsFile = new File(plugin.getDataFolder(), "plots.yml");
        }
        plotsConfig = YamlConfiguration.loadConfiguration(plotsFile);
    }

    public FileConfiguration getPlotsConfig() {
        if (plotsConfig == null) {
            reloadPlotsConfig();
        }
        return plotsConfig;
    }

    public void savePlotsConfig() {
        if (plotsConfig == null || plotsFile == null) {
            return;
        }
        try {
            getPlotsConfig().save(plotsFile);
        } catch (IOException ex) {
            plugin.getLogger().severe("Could not save config to " + plotsFile);
            ex.printStackTrace();
        }
    }

    public FileConfiguration getPlayerData(UUID uuid) {
        File playerFile = new File(dataFolder, uuid.toString() + ".yml");
        return YamlConfiguration.loadConfiguration(playerFile);
    }

    public void savePlayerData(UUID uuid, FileConfiguration config) {
        File playerFile = new File(dataFolder, uuid.toString() + ".yml");
        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player data for " + uuid);
            e.printStackTrace();
        }
    }
} 