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

    public DataManager(FarmerEconomist plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
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