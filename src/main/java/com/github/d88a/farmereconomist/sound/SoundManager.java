package com.github.d88a.farmereconomist.sound;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.NamespacedKey;

public class SoundManager {

    private final FarmerEconomist plugin;

    public SoundManager(FarmerEconomist plugin) {
        this.plugin = plugin;
    }

    public void playSound(Player player, String soundKey) {
        ConfigurationSection soundSection = plugin.getConfig().getConfigurationSection("sounds." + soundKey);

        if (soundSection != null && soundSection.getBoolean("enabled", false)) {
            try {
                String soundName = soundSection.getString("sound", "");
                Sound sound = Sound.valueOf(soundName.toUpperCase());
                float volume = (float) soundSection.getDouble("volume", 1.0);
                float pitch = (float) soundSection.getDouble("pitch", 1.0);
                player.playSound(player.getLocation(), sound, volume, pitch);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid sound name in config.yml for key: " + soundKey);
            }
        }
    }
} 