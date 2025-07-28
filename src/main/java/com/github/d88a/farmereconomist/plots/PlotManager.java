package com.github.d88a.farmereconomist.plots;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlotManager {

    private final Map<UUID, Plot> plots = new HashMap<>();

    public void addPlot(Player player, Plot plot) {
        plots.put(player.getUniqueId(), plot);
    }

    public Plot getPlot(Player player) {
        return plots.get(player.getUniqueId());
    }

    public boolean hasPlot(Player player) {
        return plots.containsKey(player.getUniqueId());
    }

    public Plot getPlotAt(org.bukkit.Location location) {
        for (Plot plot : plots.values()) {
            if (plot.isLocationInPlot(location)) {
                return plot;
            }
        }
        return null;
    }
} 