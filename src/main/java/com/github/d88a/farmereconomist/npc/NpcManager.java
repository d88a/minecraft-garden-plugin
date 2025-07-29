package com.github.d88a.farmereconomist.npc;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class NpcManager {

    private final FarmerEconomist plugin;
    private final File npcFile;
    private final FileConfiguration npcConfig;
    private UUID npcUniqueId;

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
        if (npcConfig.contains("npc-uuid")) {
            this.npcUniqueId = UUID.fromString(npcConfig.getString("npc-uuid"));
        }
    }

    public UUID getNpcUniqueId() {
        return npcUniqueId;
    }

    public void setNpcUniqueId(UUID npcUniqueId) {
        this.npcUniqueId = npcUniqueId;
        if (npcUniqueId != null) {
            npcConfig.set("npc-uuid", npcUniqueId.toString());
        } else {
            npcConfig.set("npc-uuid", null);
        }
        try {
            npcConfig.save(npcFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 