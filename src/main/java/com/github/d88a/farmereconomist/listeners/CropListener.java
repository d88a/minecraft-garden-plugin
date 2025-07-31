package com.github.d88a.farmereconomist.listeners;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.crops.CropManager;
import com.github.d88a.farmereconomist.crops.CustomCrop;
import com.github.d88a.farmereconomist.crops.CustomCrop.CropType;
import com.github.d88a.farmereconomist.plots.Plot;
import com.github.d88a.farmereconomist.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockBreakEvent;
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
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.PLAYER_HEAD) {
            return;
        }

        CustomCrop crop = cropManager.getCropAt(block.getLocation());
        if (crop == null) {
            return; // Это не наше растение
        }

        Player player = event.getPlayer();
        Plot plot = plugin.getPlotManager().getPlotAt(block.getLocation());

        // Проверка прав на участок
        if (plot != null && !plot.getOwner().equals(player.getUniqueId()) && !player.hasPermission("farmereconomist.admin.bypass")) {
            plugin.getConfigManager().sendMessage(player, "plot_interact_denied");
            event.setCancelled(true);
            return;
        }

        boolean isHarvestable = (crop.getStage() == (crop.getType().getMaxStages() - 1));

        if (isHarvestable) {
            // Растение созрело. Собираем урожай.
            // Отменяем выпадение стандартного дропа (головы)
            event.setDropItems(false);
            cropManager.harvestCrop(block.getLocation());
            plugin.getSoundManager().playSound(player, "harvest_crop");
        } else {
            // Растение не созрело. Запрещаем ломать.
            plugin.getConfigManager().sendMessage(player, "crop_not_ready");
            event.setCancelled(true);
        }
    }

    private void handleToolUse(PlayerInteractEvent event, CustomCrop crop, ItemStack itemInHand) {
        Player player = event.getPlayer();
        Plot plot = plugin.getPlotManager().getPlotAt(crop.getLocation());

        // Запрещаем взаимодействовать с чужим участком
        if (plot != null && !plot.getOwner().equals(player.getUniqueId()) && !player.hasPermission("farmereconomist.admin.bypass")) {
            plugin.getConfigManager().sendMessage(player, "plot_interact_denied");
            event.setCancelled(true);
            return;
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
        }
    }

    private void handlePlanting(PlayerInteractEvent event, ItemStack itemInHand, Block clickedBlock) {
        if (itemInHand == null || itemInHand.getType().isAir()) return;
        ItemMeta meta = itemInHand.getItemMeta();
        if (meta == null) return;

        String itemId = meta.getPersistentDataContainer().get(ItemManager.ITEM_ID_KEY, PersistentDataType.STRING);
        if (itemId == null || !itemId.endsWith("_SEEDS") && !itemId.endsWith("_SPORES")) {
            return; // Это не семена
        }

        CropType cropType = getCropTypeForSeed(itemId);
        if (cropType == null) return;

        Player player = event.getPlayer();
        Plot plot = plugin.getPlotManager().getPlotAt(clickedBlock.getLocation());

        if (plot == null) {
            plugin.getConfigManager().sendMessage(player, "crop_plant_fail_not_plot");
            event.setCancelled(true);
            return;
        }
        if (!plot.getOwner().equals(player.getUniqueId()) && !player.hasPermission("farmereconomist.admin.bypass")) {
            plugin.getConfigManager().sendMessage(player, "plot_plant_fail_not_owner");
            event.setCancelled(true);
            return;
        }

        if (canPlantOn(cropType, clickedBlock.getType())) {
            Block blockAbove = clickedBlock.getRelative(BlockFace.UP);
            if (blockAbove.getType().isAir()) {
                cropManager.plantCrop(blockAbove.getLocation(), cropType);
                itemInHand.setAmount(itemInHand.getAmount() - 1);
                plugin.getSoundManager().playSound(event.getPlayer(), "plant_crop");
            } else {
                plugin.getConfigManager().sendMessage(player, "crop_plant_fail_obstructed");
            }
        } else {
            plugin.getConfigManager().sendMessage(player, "crop_plant_fail_wrong_soil");
        }
        // Более надежно отменяем стандартное поведение посадки семян
        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setCancelled(true);
    }

    // Связывает ID семян с типом растения
    private CropType getCropTypeForSeed(String seedId) {
        return CropType.fromSeedId(seedId);
    }

    // Определяет, на какой блок можно сажать
    private boolean canPlantOn(CropType cropType, Material blockType) {
        // Логика теперь находится внутри самого CropType
        return cropType.canPlantOn(blockType);
    }
}