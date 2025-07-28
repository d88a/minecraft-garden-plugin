package com.github.d88a.farmereconomist.listeners;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.plots.Plot;
import com.github.d88a.farmereconomist.plots.PlotManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlotProtectionListener implements Listener {

    private final PlotManager plotManager;

    public PlotProtectionListener(FarmerEconomist plugin) {
        this.plotManager = plugin.getPlotManager();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        Plot plot = plotManager.getPlotAt(location);

        if (plot != null && !plot.getOwner().equals(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage("Вы не можете ломать блоки на чужом участке."); // TODO: message from config
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        Plot plot = plotManager.getPlotAt(location);

        if (plot != null && !plot.getOwner().equals(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage("Вы не можете ставить блоки на чужом участке."); // TODO: message from config
        }
    }
} 