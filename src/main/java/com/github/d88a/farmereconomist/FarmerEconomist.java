package com.github.d88a.farmereconomist;

import com.github.d88a.farmereconomist.commands.BalanceCommand;
import com.github.d88a.farmereconomist.commands.EcoCommand;
import com.github.d88a.farmereconomist.commands.OgorodCommand;
import com.github.d88a.farmereconomist.config.ConfigManager;
import com.github.d88a.farmereconomist.data.DataManager;
import com.github.d88a.farmereconomist.economy.EconomyManager;
import com.github.d88a.farmereconomist.listeners.PlotProtectionListener;
import com.github.d88a.farmereconomist.plots.PlotManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class FarmerEconomist extends JavaPlugin {

    private DataManager dataManager;
    private EconomyManager economyManager;
    private PlotManager plotManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.dataManager = new DataManager(this);
        this.economyManager = new EconomyManager(dataManager);
        this.plotManager = new PlotManager(this);

        // Register commands
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("eco").setExecutor(new EcoCommand(this));
        getCommand("ogorod").setExecutor(new OgorodCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlotProtectionListener(this), this);

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

    public PlotManager getPlotManager() {
        return plotManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
} 