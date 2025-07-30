package com.github.d88a.farmereconomist.listeners;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.plots.PlotManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class OgorodListener implements Listener {

    private final FarmerEconomist plugin;
    private final PlotManager plotManager;

    public OgorodListener(FarmerEconomist plugin) {
        this.plugin = plugin;
        this.plotManager = plugin.getPlotManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§2Управление огородом")) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        Material clickedMaterial = event.getCurrentItem().getType();

        switch (clickedMaterial) {
            case GRASS_BLOCK:
                player.performCommand("ogorod get");
                break;
            case COMPASS:
                player.performCommand("ogorod home");
                break;
            case BARRIER:
                player.performCommand("ogorod delete");
                break;
            case GOLDEN_SHOVEL:
                // Расширение участка
                if (plotManager.expandPlot(player)) {
                    // Обновляем GUI после успешного расширения
                    plugin.getOgorodGUI().open(player);
                }
                break;
        }
        player.closeInventory();
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // Проверяем, что моб спавнится в огороде
        if (plotManager.getPlotAt(event.getLocation()) != null) {
            event.setCancelled(true);
        }
    }
} 