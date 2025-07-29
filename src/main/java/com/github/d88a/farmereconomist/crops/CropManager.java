package com.github.d88a.farmereconomist.crops;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.items.ItemManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;

public class CropManager {

    private final FarmerEconomist plugin;
    private final Map<Location, CustomCrop> crops = new ConcurrentHashMap<>();
    private static final long GROWTH_INTERVAL = 1 * 60 * 1000; // 1 minute in milliseconds

    public CropManager(FarmerEconomist plugin) {
        this.plugin = plugin;
        startGrowthTask();
        loadCrops();
    }

    public void plantCrop(Location location, CustomCrop.CropType type) {
        if (crops.containsKey(location)) return;

        CustomCrop crop = new CustomCrop(location, type);
        crops.put(location, crop);

        // Place the first stage of the crop
        updateCropBlock(crop);
    }

    public void harvestCrop(Location location) {
        CustomCrop crop = crops.remove(location);
        if (crop != null) {
            location.getBlock().setType(Material.AIR);
            // TODO: Drop loot based on crop type
            location.getWorld().dropItemNaturally(location, ItemManager.createTomato());
        }
    }

    public CustomCrop getCropAt(Location location) {
        return crops.get(location);
    }

    private void startGrowthTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                for (CustomCrop crop : crops.values()) {
                    if (currentTime - crop.getLastGrowthTime() > GROWTH_INTERVAL) {
                        growCrop(crop);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L * 60); // Check every minute
    }

    private void growCrop(CustomCrop crop) {
        if (crop.getType() == CustomCrop.CropType.TOMATO) {
            if (crop.getStage() < 1) { // Tomato has 2 stages: 0 (green) and 1 (red)
                crop.setStage(crop.getStage() + 1);
                crop.setLastGrowthTime(System.currentTimeMillis());
                updateCropBlock(crop);
            }
        }
    }

    private void updateCropBlock(CustomCrop crop) {
        if (crop.getType() == CustomCrop.CropType.TOMATO) {
            // This is a workaround as we can't directly set a textured head's BlockData
            // We set the block to a player head, then get its state and apply the texture
            Block block = crop.getLocation().getBlock();
            block.setType(Material.PLAYER_HEAD);
            if (block.getState() instanceof org.bukkit.block.Skull) {
                org.bukkit.block.Skull skullState = (org.bukkit.block.Skull) block.getState();
                ItemStack headItem = ItemManager.createTomatoStage(crop.getStage());
                skullState.setPlayerProfile(((org.bukkit.inventory.meta.SkullMeta) headItem.getItemMeta()).getPlayerProfile());
                skullState.update();
            }
        }
    }
    
    public void saveCrops() {
        ConfigurationSection cropsSection = plugin.getDataManager().getCropsConfig().createSection("crops");
        int i = 0;
        for (CustomCrop crop : crops.values()) {
            ConfigurationSection cropSection = cropsSection.createSection(String.valueOf(i++));
            cropSection.set("location", crop.getLocation());
            cropSection.set("type", crop.getType().name());
            cropSection.set("stage", crop.getStage());
            cropSection.set("last-growth", crop.getLastGrowthTime());
        }
        plugin.getDataManager().saveCropsConfig();
    }

    private void loadCrops() {
        ConfigurationSection cropsSection = plugin.getDataManager().getCropsConfig().getConfigurationSection("crops");
        if (cropsSection != null) {
            for (String key : cropsSection.getKeys(false)) {
                Location loc = cropsSection.getLocation(key + ".location");
                CustomCrop.CropType type = CustomCrop.CropType.valueOf(cropsSection.getString(key + ".type"));
                CustomCrop crop = new CustomCrop(loc, type);
                crop.setStage(cropsSection.getInt(key + ".stage"));
                crop.setLastGrowthTime(cropsSection.getLong(key + ".last-growth"));
                crops.put(loc, crop);
            }
        }
    }
} 