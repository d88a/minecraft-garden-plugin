package com.github.d88a.farmereconomist.listeners;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.crops.CustomCrop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FarmingListener implements Listener {

    private final FarmerEconomist plugin;

    public FarmingListener(FarmerEconomist plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCropHarvest(BlockBreakEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player player = event.getPlayer();
        CustomCrop crop = plugin.getCropManager().getCropAt(event.getBlock().getLocation());
        
        if (crop != null) {
            // Даем опыт за сбор урожая
            int baseExp = 10;
            
            // Бонусы от событий
            double expMultiplier = plugin.getFarmingEventManager().getEventMultiplier(
                com.github.d88a.farmereconomist.events.EventType.EXPERIENCE_BOOST
            );
            baseExp = (int) (baseExp * expMultiplier);
            
            // Бонусы за сезонные культуры
            String cropTypeName = crop.getType().name();
            if (plugin.getSeasonManager().isSeasonalCrop(cropTypeName)) {
                baseExp += 5; // +5 опыта за сезонные культуры
            }
            
            // Бонусы за экзотические культуры
            if (isExoticCrop(crop.getType())) {
                baseExp += 10; // +10 опыта за экзотические культуры
            }
            
            plugin.getFarmerLevelManager().addExperience(player, baseExp);
            
            // Проверяем достижения
            checkHarvestAchievements(player, crop);
        }
    }

    @EventHandler
    public void onCropPlant(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT_CLICK")) return;
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item != null && isSeedItem(item)) {
            // Даем опыт за посадку
            int baseExp = 5;
            
            // Бонусы от событий
            double expMultiplier = plugin.getFarmingEventManager().getEventMultiplier(
                com.github.d88a.farmereconomist.events.EventType.EXPERIENCE_BOOST
            );
            baseExp = (int) (baseExp * expMultiplier);
            
            plugin.getFarmerLevelManager().addExperience(player, baseExp);
            
            // Проверяем достижения
            checkPlantAchievements(player, item);
        }
    }

    private boolean isSeedItem(ItemStack item) {
        String itemName = item.getItemMeta().getDisplayName();
        return itemName != null && itemName.contains("Семена");
    }

    private boolean isExoticCrop(CustomCrop.CropType type) {
        return type == CustomCrop.CropType.LUNAR_BERRY ||
               type == CustomCrop.CropType.RAINBOW_MUSHROOM ||
               type == CustomCrop.CropType.CRYSTAL_CACTUS ||
               type == CustomCrop.CropType.FLAME_PEPPER ||
               type == CustomCrop.CropType.MYSTIC_ROOT ||
               type == CustomCrop.CropType.STAR_FRUIT ||
               type == CustomCrop.CropType.PREDATOR_FLOWER ||
               type == CustomCrop.CropType.ELECTRO_PUMPKIN ||
               type == CustomCrop.CropType.MANDRAKE_LEAF ||
               type == CustomCrop.CropType.FLYING_FRUIT ||
               type == CustomCrop.CropType.SNOW_MINT ||
               type == CustomCrop.CropType.SUN_PINEAPPLE ||
               type == CustomCrop.CropType.FOG_BERRY ||
               type == CustomCrop.CropType.SAND_MELON ||
               type == CustomCrop.CropType.WITCH_MUSHROOM;
    }

    private void checkHarvestAchievements(Player player, CustomCrop crop) {
        // Достижение "Первые шаги"
        plugin.getAchievementManager().unlockAchievement(player, "first_plant");
        
        // Достижение "Король урожая" (проверяется в другом месте)
        // Достижение "Экзотический фермер"
        if (isExoticCrop(crop.getType())) {
            plugin.getAchievementManager().unlockAchievement(player, "exotic_farmer");
        }
        
        // Достижение "Ночной фермер"
        if (crop.getType() == CustomCrop.CropType.GLOWSHROOM || 
            crop.getType() == CustomCrop.CropType.LUNAR_BERRY) {
            plugin.getAchievementManager().unlockAchievement(player, "night_farmer");
        }
        
        // Достижение "Идеальный фермер"
        if (crop.isFertilized() && crop.isWatered()) {
            plugin.getAchievementManager().unlockAchievement(player, "perfect_farmer");
        }
        
        // Достижение "Сезонный фермер"
        String cropTypeName = crop.getType().name();
        if (plugin.getSeasonManager().isSeasonalCrop(cropTypeName)) {
            plugin.getAchievementManager().unlockAchievement(player, "seasonal_farmer");
        }
    }

    private void checkPlantAchievements(Player player, ItemStack seed) {
        // Достижение "Мастер посадки" (проверяется в другом месте)
        // Достижение "Коллекционер" (проверяется в другом месте)
    }
} 