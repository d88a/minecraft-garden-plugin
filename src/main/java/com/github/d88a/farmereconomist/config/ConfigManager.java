package com.github.d88a.farmereconomist.config;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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

    public String getMessage(String key) {
        String message = config.getString("messages." + key, "&cСообщение не найдено: " + key);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public void sendMessage(Player player, String key, String... replacements) {
        String message = getMessage(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        player.sendMessage(message);
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