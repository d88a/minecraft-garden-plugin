package com.github.d88a.farmereconomist.config;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final FarmerEconomist plugin;
    private FileConfiguration config;

    public ConfigManager(FarmerEconomist plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public Location getGridStartLocation() {
        return config.getLocation("plots.grid_start_location");
    }

    public void setGridStartLocation(Location location) {
        config.set("plots.grid_start_location", location);
        plugin.saveConfig();
    }

    public int getPlotSize() {
        return config.getInt("plots.size", 7);
    }

    public int getPlotGap() {
        return config.getInt("plots.gap", 3);
    }
    
    public Material getFenceMaterial() {
        return Material.getMaterial(config.getString("plots.fence_material", "OAK_FENCE"));
    }
    
    public Material getGateMaterial() {
        return Material.getMaterial(config.getString("plots.gate_material", "OAK_FENCE_GATE"));
    }

    public String getCurrencyName() {
        return config.getString("economy.currency_name", "монет");
    }
} 