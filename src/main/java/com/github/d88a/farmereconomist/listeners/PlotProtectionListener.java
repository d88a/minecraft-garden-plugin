package com.github.d88a.farmereconomist.listeners;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.items.ItemManager;
import com.github.d88a.farmereconomist.plots.Plot;
import com.github.d88a.farmereconomist.plots.PlotManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlotProtectionListener implements Listener {

    private final FarmerEconomist plugin;
    private final PlotManager plotManager;

    public PlotProtectionListener(FarmerEconomist plugin) {
        this.plugin = plugin;
        this.plotManager = plugin.getPlotManager();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();
        Plot plot = plotManager.getPlotAt(location);

        if (plot != null && !plot.getOwner().equals(player.getUniqueId())) {
            event.setCancelled(true);
            plugin.getConfigManager().sendMessage(player, "plot_break_denied");
            return;
        }

        // Custom crop logic
        if (plot != null && block.getType() == Material.WHEAT) {
            Ageable ageable = (Ageable) block.getBlockData();
            if (ageable.getAge() == ageable.getMaximumAge()) {
                event.setCancelled(true); // We handle the drop ourselves
                block.setType(Material.AIR);

                boolean isWatered = block.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.FARMLAND &&
                                  ((org.bukkit.block.data.type.Farmland) block.getLocation().subtract(0, 1, 0).getBlock().getBlockData()).getMoisture() > 0;

                block.getWorld().dropItemNaturally(location, ItemManager.createLettuce(isWatered));
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        Plot plot = plotManager.getPlotAt(location);

        if (plot != null && !plot.getOwner().equals(player.getUniqueId())) {
            event.setCancelled(true);
            plugin.getConfigManager().sendMessage(player, "plot_place_denied");
            return;
        }
        
        // Custom seed planting logic
        ItemStack itemInHand = event.getItemInHand();
        if (plot != null && itemInHand.isSimilar(ItemManager.createLettuceSeeds())) {
            Block block = event.getBlockPlaced();
            if(block.getRelative(0, -1, 0).getType() == Material.FARMLAND) {
                // This is a valid planting, but we let default behavior place the seeds.
                // For more complex plants, we would cancel the event and set the block manually.
            } else {
                event.setCancelled(true);
                plugin.getConfigManager().sendMessage(player, "crop_plant_fail_not_farmland");
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;

        Block clickedBlock = event.getClickedBlock();
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (clickedBlock == null || itemInHand.getType() != Material.IRON_HOE || !itemInHand.hasItemMeta() || itemInHand.getItemMeta().getCustomModelData() != 1) {
            return; // Not our watering can
        }
        
        Plot plot = plotManager.getPlotAt(clickedBlock.getLocation());
        if (plot == null || !plot.getOwner().equals(player.getUniqueId())) {
            return; // Not on their plot
        }

        if (clickedBlock.getType() == Material.FARMLAND) {
            org.bukkit.block.data.type.Farmland farmland = (org.bukkit.block.data.type.Farmland) clickedBlock.getBlockData();
            farmland.setMoisture(farmland.getMaximumMoisture());
            clickedBlock.setBlockData(farmland);
            plugin.getConfigManager().sendMessage(player, "crop_watered");
        }
    }
} 