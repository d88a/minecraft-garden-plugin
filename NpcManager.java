package com.github.d88a.farmereconomist.npc;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class NpcManager {

    private final FarmerEconomist plugin;
    private UUID npcUniqueId;

    public NpcManager(FarmerEconomist plugin) {
        this.plugin = plugin;
        loadNpcData();
    }

    private void loadNpcData() {
        FileConfiguration config = plugin.getConfig();
        String npcIdString = config.getString("npc.uuid");
        if (npcIdString != null && !npcIdString.isEmpty()) {
            try {
                this.npcUniqueId = UUID.fromString(npcIdString);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid NPC UUID in config.yml. Please remove or fix it.");
                this.npcUniqueId = null;
            }
        }
    }

    public void saveNpcData(Entity npc) {
        this.npcUniqueId = npc.getUniqueId();
        FileConfiguration config = plugin.getConfig();
        config.set("npc.uuid", this.npcUniqueId.toString());
        plugin.saveConfig();
    }

    public void clearNpcData() {
        this.npcUniqueId = null;
        FileConfiguration config = plugin.getConfig();
        config.set("npc.uuid", null);
        plugin.saveConfig();
    }

    public UUID getNpcUniqueId() {
        return npcUniqueId;
    }

    public Entity getNpcEntity() {
        if (npcUniqueId == null) {
            return null;
        }
        return Bukkit.getEntity(npcUniqueId);
    }
}