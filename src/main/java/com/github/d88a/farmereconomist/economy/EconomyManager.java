package com.github.d88a.farmereconomist.economy;

import com.github.d88a.farmereconomist.data.DataManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class EconomyManager {

    private final DataManager dataManager;

    public EconomyManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public double getBalance(Player player) {
        FileConfiguration playerData = dataManager.getPlayerData(player.getUniqueId());
        return playerData.getDouble("balance", 0.0);
    }

    public void setBalance(Player player, double amount) {
        FileConfiguration playerData = dataManager.getPlayerData(player.getUniqueId());
        playerData.set("balance", amount);
        dataManager.savePlayerData(player.getUniqueId(), playerData);
    }

    public void addBalance(Player player, double amount) {
        double currentBalance = getBalance(player);
        setBalance(player, currentBalance + amount);
    }

    public void takeBalance(Player player, double amount) {
        double currentBalance = getBalance(player);
        setBalance(player, Math.max(0, currentBalance - amount));
    }
} 