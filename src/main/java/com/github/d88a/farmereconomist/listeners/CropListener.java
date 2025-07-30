package com.github.d88a.farmereconomist.listeners;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.crops.CropManager;
import com.github.d88a.farmereconomist.crops.CustomCrop;
import com.github.d88a.farmereconomist.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CropListener implements Listener {

    private final FarmerEconomist plugin;
    private final CropManager cropManager;

    public CropListener(FarmerEconomist plugin) {
        this.plugin = plugin;
        this.cropManager = plugin.getCropManager();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().isSimilar(ItemManager.createTomatoSeeds())) {
            handleTomatoPlacement(event);
        } else if (event.getPlayer().getInventory().getItemInMainHand().isSimilar(ItemManager.createGlowshroomSpores())) {
            handleGlowshroomPlacement(event);
        }
    }

    private void handleTomatoPlacement(BlockPlaceEvent event) {
        Block placedOn = event.getBlock().getRelative(0, -1, 0);
        if (placedOn.getType() == Material.FARMLAND) {
            event.setCancelled(true);
            cropManager.plantCrop(event.getBlock().getLocation(), CustomCrop.CropType.TOMATO);
            event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
        } else {
            plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_plant_fail_not_farmland");
            event.setCancelled(true);
        }
    }

    private void handleGlowshroomPlacement(BlockPlaceEvent event) {
        Block placedOn = event.getBlock().getRelative(0, -1, 0);
        if (placedOn.getType() == Material.MYCELIUM || placedOn.getType() == Material.PODZOL) {
            event.setCancelled(true);
            cropManager.plantCrop(event.getBlock().getLocation(), CustomCrop.CropType.GLOWSHROOM);
            event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
        } else {
            plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_plant_fail_not_mycelium");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || clickedBlock.getType() != Material.PLAYER_HEAD) return;

        CustomCrop crop = cropManager.getCropAt(clickedBlock.getLocation());
        if (crop == null) return;

        // --- Обработка правой кнопки мыши (использование лейки/удобрения) ---
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
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