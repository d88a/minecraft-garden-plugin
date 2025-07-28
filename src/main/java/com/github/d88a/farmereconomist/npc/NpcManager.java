package com.github.d88a.farmereconomist.npc;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class NpcManager {

    private final FarmerEconomist plugin;
    private final File npcFile;
    private final FileConfiguration npcConfig;
    private Location npcLocation;

    public NpcManager(FarmerEconomist plugin) {
        this.plugin = plugin;
        this.npcFile = new File(plugin.getDataFolder(), "npc.yml");
        if (!npcFile.exists()) {
            try {
                npcFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.npcConfig = YamlConfiguration.loadConfiguration(npcFile);
        this.npcLocation = npcConfig.getLocation("location");
    }

    public Location getNpcLocation() {
        return npcLocation;
    }

    public void setNpcLocation(Location location) {
        this.npcLocation = location;
        npcConfig.set("location", location);
        try {
            npcConfig.save(npcFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 