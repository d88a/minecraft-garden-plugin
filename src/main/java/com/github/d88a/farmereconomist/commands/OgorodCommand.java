package com.github.d88a.farmereconomist.commands;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.plots.Plot;
import com.github.d88a.farmereconomist.plots.PlotManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OgorodCommand implements CommandExecutor {

    private final FarmerEconomist plugin;
    private final PlotManager plotManager;

    public OgorodCommand(FarmerEconomist plugin) {
        this.plugin = plugin;
        this.plotManager = plugin.getPlotManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("only_for_players"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            plugin.getOgorodGUI().open(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "get":
                handleGet(player);
                break;
            case "home":
                handleHome(player);
                break;
            case "delete":
                handleDelete(player);
                break;
            case "set_start":
                if (player.hasPermission("farmereconomist.admin.ogorod")) {
                    plugin.getConfigManager().setGridStartLocation(player.getLocation());
                    plugin.getConfigManager().sendMessage(player, "plot_set_start_success");
                } else {
                    plugin.getConfigManager().sendMessage(player, "no_permission");
                }
                break;
            default:
                plugin.getConfigManager().sendMessage(player, "unknown_command");
                break;
        }
        return true;
    }

    private void handleDelete(Player player) {
        if (!plotManager.hasPlot(player)) {
            plugin.getConfigManager().sendMessage(player, "plot_delete_fail_no_plot");
            return;
        }
        plotManager.deletePlot(player);
        plugin.getConfigManager().sendMessage(player, "plot_delete_success");
    }

    private void handleHome(Player player) {
        Plot plot = plotManager.getPlot(player);
        if (plot == null) {
            plugin.getConfigManager().sendMessage(player, "plot_home_fail_no_plot");
            return;
        }
        player.teleport(plot.getTeleportLocation());
        plugin.getConfigManager().sendMessage(player, "plot_home_success");
        plugin.getSoundManager().playSound(player, "teleport");
    }

    private void handleGet(Player player) {
        if (plotManager.hasPlot(player)) {
            plugin.getConfigManager().sendMessage(player, "plot_get_fail_has_plot");
            return;
        }

        if (plugin.getConfigManager().getGridStartLocation() == null) {
            plugin.getConfigManager().sendMessage(player, "plot_grid_not_set");
            return;
        }

        Plot newPlot = plotManager.createNewPlotFor(player);

        if (newPlot != null) {
            plugin.getConfigManager().sendMessage(player, "plot_get_success");
        } else {
            plugin.getConfigManager().sendMessage(player, "plot_create_fail");
        }
    }
} 