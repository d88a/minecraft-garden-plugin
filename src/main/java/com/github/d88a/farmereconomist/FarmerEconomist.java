package com.github.d88a.farmereconomist;

import com.github.d88a.farmereconomist.commands.BalanceCommand;
import com.github.d88a.farmereconomist.commands.EcoCommand;
import com.github.d88a.farmereconomist.data.DataManager;
import com.github.d88a.farmereconomist.economy.EconomyManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class FarmerEconomist extends JavaPlugin {

    private DataManager dataManager;
    private EconomyManager economyManager;

    @Override
    public void onEnable() {
        // Initialize managers
        this.dataManager = new DataManager(this);
        this.economyManager = new EconomyManager(dataManager);

        // Register commands
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("eco").setExecutor(new EcoCommand(this));

        getLogger().info("FarmerEconomist has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("FarmerEconomist has been disabled!");
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }
} 