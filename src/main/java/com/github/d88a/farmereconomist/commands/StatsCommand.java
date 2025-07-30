package com.github.d88a.farmereconomist.commands;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    private final FarmerEconomist plugin;

    public StatsCommand(FarmerEconomist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("only_for_players"));
            return true;
        }

        Player player = (Player) sender;
        
        if (args.length > 0) {
            // Просмотр статистики другого игрока (для администраторов)
            if (!player.hasPermission("farmereconomist.admin.stats")) {
                player.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
                return true;
            }
            
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cИгрок не найден.");
                return true;
            }
            
            showStats(player, target);
        } else {
            // Просмотр своей статистики
            showStats(player, player);
        }

        return true;
    }

    private void showStats(Player viewer, Player target) {
        String prefix = viewer.equals(target) ? "§a=== ВАША СТАТИСТИКА ===" : "§a=== СТАТИСТИКА " + target.getName() + " ===";
        viewer.sendMessage(prefix);
        
        // Базовая информация
        viewer.sendMessage("§7");
        viewer.sendMessage("§e💰 Баланс: §a" + plugin.getEconomyManager().getBalance(target) + " монет");
        viewer.sendMessage("§b📊 Уровень: §a" + plugin.getFarmerLevelManager().getLevelInfo(target));
        
        // Достижения
        int achievements = plugin.getAchievementManager().getAchievementProgress(target);
        int totalAchievements = plugin.getAchievementManager().getTotalAchievements();
        double completion = plugin.getAchievementManager().getCompletionPercentage(target);
        viewer.sendMessage("§d🏆 Достижения: §a" + achievements + "/" + totalAchievements + " §7(" + String.format("%.1f", completion) + "%)");
        
        // Участок
        if (plugin.getPlotManager().hasPlot(target)) {
            viewer.sendMessage("§a🌱 Участок: §aПолучен");
        } else {
            viewer.sendMessage("§a🌱 Участок: §cНе получен");
        }
        
        // Сезон и погода
        viewer.sendMessage("§6🌱 " + plugin.getSeasonManager().getSeasonInfo());
        viewer.sendMessage("§b🌤 " + plugin.getWeatherManager().getWeatherInfo());
        
        // Текущее событие
        if (plugin.getFarmingEventManager().isEventActive()) {
            viewer.sendMessage("§d🎉 " + plugin.getFarmingEventManager().getEventStatus());
        }
        
        viewer.sendMessage("§7");
    }
} 