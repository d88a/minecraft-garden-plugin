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
            sender.sendMessage("Эта команда только для игроков.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("Использование: /ogorod <get|home|...");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "get":
                handleGet(player);
                break;
            case "home":
                player.sendMessage("Эта функция в разработке.");
                break;
            case "set_start":
                if (player.hasPermission("farmereconomist.admin.ogorod")) {
                    player.sendMessage("Эта функция в разработке.");
                } else {
                    player.sendMessage("У вас нет прав.");
                }
                break;
            default:
                player.sendMessage("Неизвестная подкоманда.");
                break;
        }
        return true;
    }

    private void handleGet(Player player) {
        if (plotManager.hasPlot(player)) {
            player.sendMessage("У вас уже есть участок.");
            return;
        }

        // TODO: Replace with real plot generation logic
        Location corner1 = new Location(player.getWorld(), 0, 64, 0);
        Location corner2 = new Location(player.getWorld(), 6, 64, 6);
        Plot newPlot = new Plot(player.getUniqueId(), corner1, corner2);

        plotManager.addPlot(player, newPlot);
        player.sendMessage("Поздравляем! Вы получили участок. (Тестовая зона)");
    }
} 