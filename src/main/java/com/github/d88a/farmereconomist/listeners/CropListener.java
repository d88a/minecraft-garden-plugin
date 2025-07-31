package com.github.d88a.farmereconomist.listeners;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.crops.CropManager;
import com.github.d88a.farmereconomist.crops.CustomCrop;
import com.github.d88a.farmereconomist.crops.CustomCrop.CropType;
import com.github.d88a.farmereconomist.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CropListener implements Listener {

    private final FarmerEconomist plugin;
    private final CropManager cropManager;

    public CropListener(FarmerEconomist plugin) {
        this.plugin = plugin;
        this.cropManager = plugin.getCropManager();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // --- Логика использования инструментов и посадки семян ---
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) return;

            // Попытка использовать инструмент на растении
            if (clickedBlock.getType() == Material.PLAYER_HEAD) {
                CustomCrop crop = cropManager.getCropAt(clickedBlock.getLocation());
                if (crop != null) {
                    handleToolUse(event, crop, itemInHand);
                    return; // Завершаем, если это было взаимодействие с растением
                }
            }

            // Попытка посадить семена
            handlePlanting(event, itemInHand, clickedBlock);
            return;
        }

        // --- Обработка левой кнопки мыши (сбор урожая) ---
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null || clickedBlock.getType() != Material.PLAYER_HEAD) return;

            CustomCrop crop = cropManager.getCropAt(clickedBlock.getLocation());
            if (crop == null) return;

            boolean isHarvestable = (crop.getStage() == (crop.getType().getMaxStages() - 1));

            if (isHarvestable) {
                cropManager.harvestCrop(clickedBlock.getLocation());
                plugin.getSoundManager().playSound(event.getPlayer(), "harvest_crop");
            } else {
                plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_not_ready");
            }
            event.setCancelled(true);
        }
    }

    private void handleToolUse(PlayerInteractEvent event, CustomCrop crop, ItemStack itemInHand) {
        if (itemInHand.isSimilar(ItemManager.createWateringCan())) {
            if (!crop.isWatered()) {
                crop.setWatered(true);
                crop.setWateredTime(System.currentTimeMillis());
                plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_watered");
                plugin.getSoundManager().playSound(event.getPlayer(), "water_crop");
                event.setCancelled(true);
            } else {
                plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_already_watered");
            }
            return;
        }
        if (itemInHand.isSimilar(ItemManager.createFertilizer())) {
            if (!crop.isFertilized()) {
                crop.setFertilized(true);
                crop.setFertilizerTime(System.currentTimeMillis());
                itemInHand.setAmount(itemInhand.getAmount() - 1);
                plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_fertilized");
                plugin.getSoundManager().playSound(event.getPlayer(), "fertilize_crop");
                event.setCancelled(true);
            } else {
                plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_already_fertilized");
            }
        }
    }

    private void handlePlanting(PlayerInteractEvent event, ItemStack itemInHand, Block clickedBlock) {
        if (itemInHand == null || itemInHand.getType().isAir()) return;
        ItemMeta meta = itemInHand.getItemMeta();
        if (meta == null) return;

        String itemId = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "item_id"), PersistentDataType.STRING);
        if (itemId == null || !itemId.endsWith("_SEEDS") && !itemId.endsWith("_SPORES")) {
            return; // Это не семена
        }

        CropType cropType = getCropTypeForSeed(itemId);
        if (cropType == null) return;

        if (canPlantOn(cropType, clickedBlock.getType())) {
            Block blockAbove = clickedBlock.getRelative(BlockFace.UP);
            if (blockAbove.getType().isAir()) {
                cropManager.plantCrop(blockAbove.getLocation(), cropType);
                itemInHand.setAmount(itemInHand.getAmount() - 1);
                plugin.getSoundManager().playSound(event.getPlayer(), "plant_crop");
                event.setCancelled(true);
            }
        }
    }

    // Связывает ID семян с типом растения
    private CropType getCropTypeForSeed(String seedId) {
        for (CropType type : CropType.values()) {
            // Простое сопоставление: TOMATO_SEEDS -> TOMATO
            String expectedSeedId = type.name() + "_SEEDS";
            String expectedSporeId = type.name() + "_SPORES";
            if (expectedSeedId.equals(seedId) || expectedSporeId.equals(seedId)) {
                return type;
            }
        }
        return null;
    }

    // Определяет, на какой блок можно сажать
    private boolean canPlantOn(CropType cropType, Material blockType) {
        switch (cropType) {
            case TOMATO:
            case STRAWBERRY:
            case RADISH:
            case WATERMELON:
            case LUNAR_BERRY:
            case MYSTIC_ROOT:
            case STAR_FRUIT:
            case PREDATOR_FLOWER:
            case ELECTRO_PUMPKIN:
            case MANDRAKE_LEAF:
            case FLYING_FRUIT:
            case SUN_PINEAPPLE:
            case FOG_BERRY:
                return blockType == Material.FARMLAND;
            case GLOWSHROOM:
            case RAINBOW_MUSHROOM:
            case WITCH_MUSHROOM:
                return blockType == Material.MYCELIUM || blockType == Material.PODZOL;
            case CRYSTAL_CACTUS:
            case SAND_MELON:
                return blockType == Material.SAND;
            case FLAME_PEPPER:
                return blockType == Material.SOUL_SAND || blockType == Material.SOUL_SOIL; // Например
            case SNOW_MINT:
                return blockType == Material.SNOW_BLOCK;
            default:
                return false;
        }
    }
}
            if (itemInHand.isSimilar(ItemManager.createWateringCan())) {
                if (!crop.isWatered()) {
                    crop.setWatered(true);
                    crop.setWateredTime(System.currentTimeMillis());
                    plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_watered");
                    plugin.getSoundManager().playSound(event.getPlayer(), "water_crop");
                    event.setCancelled(true);
                } else {
                    plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_already_watered");
                }
                return;
            }
            if (itemInHand.isSimilar(ItemManager.createFertilizer())) {
                if (!crop.isFertilized()) {
                    crop.setFertilized(true);
                    crop.setFertilizerTime(System.currentTimeMillis());
                itemInHand.setAmount(itemInHand.getAmount() - 1);
                    plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_fertilized");
                    plugin.getSoundManager().playSound(event.getPlayer(), "fertilize_crop");
                    event.setCancelled(true);
                } else {
                    plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_already_fertilized");
                }
                return;
            }
        }

        // --- Обработка левой кнопки мыши (сбор урожая) ---
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            boolean isHarvestable = false;
            // Теперь проверяем, если растение достигло последней стадии
            if(crop.getStage() == (crop.getType().getMaxStages() - 1)) {
                isHarvestable = true;
            }
            if (isHarvestable) {
                cropManager.harvestCrop(clickedBlock.getLocation());
                plugin.getSoundManager().playSound(event.getPlayer(), "harvest_crop");
            } else {
                plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_not_ready");
            }
            event.setCancelled(true);
        }
    }
} 