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
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(plugin.getConfigManager().getMessage("eco_usage"));
            return true;
        }

        String action = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        double amount;

        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("eco_amount_must_be_number"));
            return true;
        }
        
        if (target.getPlayer() == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("eco_player_not_found"));
            return true;
        }

        Player targetPlayer = target.getPlayer();
        String amountStr = String.format("%.2f", amount);

        switch (action.toLowerCase()) {
            case "give":
                plugin.getEconomyManager().addBalance(targetPlayer, amount);
                sender.sendMessage(plugin.getConfigManager().getMessage("eco_give_sender")
                        .replace("%amount%", amountStr)
                        .replace("%currency%", plugin.getConfigManager().getCurrencyName())
                        .replace("%player%", target.getName()));
                if (target.isOnline()) {
                    ((Player) target).sendMessage(plugin.getConfigManager().getMessage("eco_give_receiver")
                            .replace("%amount%", amountStr)
                            .replace("%currency%", plugin.getConfigManager().getCurrencyName()));
                }
                break;
            case "take":
                plugin.getEconomyManager().takeBalance(targetPlayer, amount);
                sender.sendMessage(plugin.getConfigManager().getMessage("eco_take_sender")
                        .replace("%amount%", amountStr)
                        .replace("%currency%", plugin.getConfigManager().getCurrencyName())
                        .replace("%player%", target.getName()));
                if (target.isOnline()) {
                    ((Player) target).sendMessage(plugin.getConfigManager().getMessage("eco_take_receiver")
                            .replace("%amount%", amountStr)
                            .replace("%currency%", plugin.getConfigManager().getCurrencyName()));
                }
                break;
            case "set":
                plugin.getEconomyManager().setBalance(targetPlayer, amount);
                sender.sendMessage(plugin.getConfigManager().getMessage("eco_set_sender")
                        .replace("%amount%", amountStr)
                        .replace("%currency%", plugin.getConfigManager().getCurrencyName())
                        .replace("%player%", target.getName()));
                if (target.isOnline()) {
                    ((Player) target).sendMessage(plugin.getConfigManager().getMessage("eco_set_receiver")
                            .replace("%amount%", amountStr)
                            .replace("%currency%", plugin.getConfigManager().getCurrencyName()));
                }
                break;
            default:
                sender.sendMessage(plugin.getConfigManager().getMessage("unknown_command"));
                break;
        }

        return true;
    }
} 