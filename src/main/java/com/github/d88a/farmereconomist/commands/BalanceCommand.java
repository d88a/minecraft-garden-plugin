package com.github.d88a.farmereconomist.commands;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BalanceCommand implements CommandExecutor {

    private final FarmerEconomist plugin;

    public BalanceCommand(FarmerEconomist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("only_for_players"));
            return true;
        }

        Player player = (Player) sender;
        double balance = plugin.getEconomyManager().getBalance(player);
        plugin.getConfigManager().sendMessage(player, "balance",
                "%balance%", String.format("%.2f", balance),
                "%currency%", plugin.getConfigManager().getCurrencyName());

        return true;
    }
} 