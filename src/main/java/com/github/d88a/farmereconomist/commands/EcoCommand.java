package com.github.d88a.farmereconomist.commands;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EcoCommand implements CommandExecutor {

    private final FarmerEconomist plugin;

    public EcoCommand(FarmerEconomist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("farmereconomist.admin.eco")) {
            sender.sendMessage("У вас нет прав для использования этой команды.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("Использование: /eco <give|take|set> <игрок> <сумма>");
            return true;
        }

        String action = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        double amount;

        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Сумма должна быть числом.");
            return true;
        }
        
        if (target.getPlayer() == null) {
            sender.sendMessage("Игрок не найден.");
            return true;
        }

        Player targetPlayer = target.getPlayer();

        switch (action.toLowerCase()) {
            case "give":
                plugin.getEconomyManager().addBalance(targetPlayer, amount);
                sender.sendMessage("Вы выдали " + amount + " монет игроку " + target.getName());
                if (target.isOnline()) {
                    targetPlayer.sendMessage("Вам выдали " + amount + " монет.");
                }
                break;
            case "take":
                plugin.getEconomyManager().takeBalance(targetPlayer, amount);
                sender.sendMessage("Вы забрали " + amount + " монет у игрока " + target.getName());
                if (target.isOnline()) {
                    targetPlayer.sendMessage("У вас забрали " + amount + " монет.");
                }
                break;
            case "set":
                plugin.getEconomyManager().setBalance(targetPlayer, amount);
                sender.sendMessage("Вы установили баланс " + amount + " монет игроку " + target.getName());
                if (target.isOnline()) {
                    targetPlayer.sendMessage("Ваш баланс установлен на " + amount + " монет.");
                }
                break;
            default:
                sender.sendMessage("Неизвестное действие. Используйте give, take или set.");
                break;
        }

        return true;
    }
} 