package com.github.d88a.farmereconomist.listeners;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.crops.CropManager;
import com.github.d88a.farmereconomist.crops.CustomCrop;
import com.github.d88a.farmereconomist.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CropListener implements Listener {

    private final FarmerEconomist plugin;
    private final CropManager cropManager;

    public CropListener(FarmerEconomist plugin) {
        this.plugin = plugin;
        this.cropManager = plugin.getCropManager();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().isSimilar(ItemManager.createTomatoSeeds())) {
            Block placedOn = event.getBlock().getRelative(0, -1, 0);
            if (placedOn.getType() == Material.FARMLAND) {
                event.setCancelled(true);
                cropManager.plantCrop(event.getBlock().getLocation(), CustomCrop.CropType.TOMATO);
                event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);
            } else {
                plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_plant_fail_not_farmland");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock.getType() != Material.PLAYER_HEAD) return;

        CustomCrop crop = cropManager.getCropAt(clickedBlock.getLocation());
        if (crop != null) {
            if (crop.getStage() >= 1) { // Check if tomato is ripe (red)
                cropManager.harvestCrop(clickedBlock.getLocation());
            }
        }
    }
} 