package com.minecraft.garden.managers;

import com.minecraft.garden.GardenPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {
    
    private final GardenPlugin plugin;
    private final Map<UUID, Integer> playerBalances;
    
    public EconomyManager(GardenPlugin plugin) {
        this.plugin = plugin;
        this.playerBalances = new HashMap<>();
        loadBalances();
    }
    
    private void loadBalances() {
        // Загрузка балансов из данных
        ConfigurationSection balancesSection = plugin.getDataManager().getData().getConfigurationSection("balances");
        if (balancesSection != null) {
            for (String uuidString : balancesSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    int balance = balancesSection.getInt(uuidString);
                    playerBalances.put(uuid, balance);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Неверный UUID в данных баланса: " + uuidString);
                }
            }
        }
    }
    
    public void saveBalances() {
        // Сохранение балансов в данные
        ConfigurationSection balancesSection = plugin.getDataManager().getData().createSection("balances");
        for (Map.Entry<UUID, Integer> entry : playerBalances.entrySet()) {
            balancesSection.set(entry.getKey().toString(), entry.getValue());
        }
        plugin.getDataManager().saveData();
    }
    
    public int getBalance(Player player) {
        return playerBalances.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void setBalance(Player player, int amount) {
        playerBalances.put(player.getUniqueId(), amount);
        saveBalances();
    }
    
    public boolean hasMoney(Player player, int amount) {
        return getBalance(player) >= amount;
    }
    
    public boolean withdrawMoney(Player player, int amount) {
        if (hasMoney(player, amount)) {
            int currentBalance = getBalance(player);
            setBalance(player, currentBalance - amount);
            return true;
        }
        return false;
    }
    
    public void addMoney(Player player, int amount) {
        int currentBalance = getBalance(player);
        setBalance(player, currentBalance + amount);
    }
    
    /**
     * Выдает начальный баланс игроку при получении участка
     */
    public void givePlotStartingBalance(Player player) {
        if (!playerBalances.containsKey(player.getUniqueId())) {
            int startingBalance = plugin.getConfigManager().getPlotStartingBalance();
            setBalance(player, startingBalance);
            player.sendMessage("§aПолучен начальный капитал: §e" + startingBalance + " рублей");
        }
    }
    
    /**
     * Проверяет, есть ли у игрока баланс (участвовал ли он в экономике)
     */
    public boolean hasParticipatedInEconomy(Player player) {
        return playerBalances.containsKey(player.getUniqueId());
    }
} 