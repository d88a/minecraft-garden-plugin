package com.github.d88a.farmereconomist.npc;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class NpcManager {

    private final FarmerEconomist plugin;
    private final File npcFile;
    private FileConfiguration npcConfig;
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
        loadNpc();
    }

    public void createNpc(Location location, String name) {
        // Сначала удаляем старого NPC, если он был
        if (this.npcUniqueId != null) {
            removeNpc();
        }

        Villager npc = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        npc.setCustomName(name);
        npc.setCustomNameVisible(true);
        npc.setAI(false);
        npc.setSilent(true);
        npc.setInvulnerable(true);
        npc.setGravity(false); // Чтобы не падал и не подпрыгивал
        
        this.npcUniqueId = npc.getUniqueId();
        saveNpc();
    }

    public boolean removeNpc() {
        if (this.npcUniqueId == null) {
            return false; // Нет NPC для удаления
        }

        // Находим NPC в мире по его UUID
        Entity npcEntity = Bukkit.getEntity(this.npcUniqueId);
        if (npcEntity != null) {
            npcEntity.remove(); // Удаляем из мира
        }

        this.npcUniqueId = null;
        // Очищаем данные из конфига
        npcConfig.set("npc.uuid", null);
        try {
            npcConfig.save(npcFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public UUID getNpcUniqueId() {
        return npcUniqueId;
    }

    private void saveNpc() {
        if (npcUniqueId != null) {
            npcConfig.set("npc.uuid", npcUniqueId.toString());
            try {
                npcConfig.save(npcFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadNpc() {
        String uuidString = npcConfig.getString("npc.uuid");
        if (uuidString != null) {
            try {
                this.npcUniqueId = UUID.fromString(uuidString);
                // Проверяем, существует ли еще NPC в мире
                if (Bukkit.getEntity(this.npcUniqueId) == null) {
                    this.npcUniqueId = null; // Если нет, считаем, что его нет
                }
            } catch (IllegalArgumentException e) {
                this.npcUniqueId = null;
            }
        }
    }
}