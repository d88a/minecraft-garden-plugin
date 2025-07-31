package com.github.d88a.farmereconomist.npc;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.data.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.UUID;

public class NpcManager {

    private final FarmerEconomist plugin;
    private final DataManager dataManager;
    private UUID npcUniqueId;

    public NpcManager(FarmerEconomist plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
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
        dataManager.getNpcConfig().set("npc", null);
        dataManager.saveNpcConfig();
        return true;
    }

    public UUID getNpcUniqueId() {
        return npcUniqueId;
    }

    private void saveNpc() {
        if (npcUniqueId != null) {
            dataManager.getNpcConfig().set("npc.uuid", npcUniqueId.toString());
            dataManager.saveNpcConfig();
        }
    }

    private void loadNpc() {
        String uuidString = dataManager.getNpcConfig().getString("npc.uuid");
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