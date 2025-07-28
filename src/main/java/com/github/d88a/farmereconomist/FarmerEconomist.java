package com.github.d88a.farmereconomist;

import org.bukkit.plugin.java.JavaPlugin;

public final class FarmerEconomist extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("FarmerEconomist has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("FarmerEconomist has been disabled!");
    }
} 