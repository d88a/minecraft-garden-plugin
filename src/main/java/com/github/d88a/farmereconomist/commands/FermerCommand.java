package com.github.d88a.farmereconomist.commands;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FermerCommand implements CommandExecutor {

    private final FarmerEconomist plugin;

    public FermerCommand(FarmerEconomist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда только для игроков.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("set_npc")) {
            handleSetNpc(player);
        } else {
            handleTeleport(player);
        }

        return true;
    }

    private void handleSetNpc(Player player) {
        if (!player.hasPermission("farmereconomist.admin.set_npc")) {
            player.sendMessage("У вас нет прав для этого."); // TODO: from config
            return;
        }
        plugin.getNpcManager().setNpcLocation(player.getLocation());
        player.sendMessage("Позиция NPC 'Старый Мирон' установлена здесь."); // TODO: from config
    }

    private void handleTeleport(Player player) {
        Location npcLocation = plugin.getNpcManager().getNpcLocation();
        if (npcLocation == null) {
            player.sendMessage("NPC 'Старый Мирон' еще не установлен администратором."); // TODO: from config
            return;
        }
        player.teleport(npcLocation);
        player.sendMessage("Вы телепортированы к Старому Мирону."); // TODO: from config
    }
} 