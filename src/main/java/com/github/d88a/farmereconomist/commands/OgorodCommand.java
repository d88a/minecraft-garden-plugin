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
                    plugin.getConfigManager().setGridStartLocation(player.getLocation());
                    player.sendMessage("Начальная точка для сетки огородов установлена здесь."); // TODO: msg from config
                } else {
                    player.sendMessage("У вас нет прав."); // TODO: msg from config
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
            player.sendMessage("У вас уже есть участок."); // TODO: msg from config
            return;
        }

        if (plugin.getConfigManager().getGridStartLocation() == null) {
            player.sendMessage("Сетка огородов еще не настроена администратором."); // TODO: msg from config
            return;
        }

        Plot newPlot = plotManager.createNewPlotFor(player);

        if (newPlot != null) {
            player.sendMessage("Поздравляем! Вы получили новый участок."); // TODO: msg from config
        } else {
            // This might happen if start location is suddenly null, or other errors.
            player.sendMessage("Не удалось создать участок. Обратитесь к администратору.");
        }
    }
} 