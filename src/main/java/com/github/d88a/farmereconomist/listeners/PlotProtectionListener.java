package com.github.d88a.farmereconomist.listeners;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.items.ItemManager;
import com.github.d88a.farmereconomist.plots.Plot;
import com.github.d88a.farmereconomist.plots.PlotManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

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

        // Запрещаем ломать блоки на чужом участке, если нет прав на обход
        if (plot != null && !plot.getOwner().equals(player.getUniqueId()) && !player.hasPermission("farmereconomist.admin.bypass")) {
            event.setCancelled(true);
            plugin.getConfigManager().sendMessage(player, "plot_break_denied");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        Plot plot = plotManager.getPlotAt(location);

        // Запрещаем ставить блоки на чужом участке, если нет прав на обход
        if (plot != null && !plot.getOwner().equals(player.getUniqueId()) && !player.hasPermission("farmereconomist.admin.bypass")) {
            event.setCancelled(true);
            plugin.getConfigManager().sendMessage(player, "plot_place_denied");
        }
    }
}