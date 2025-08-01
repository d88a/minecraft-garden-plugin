package com.github.d88a.farmereconomist.commands;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import org.bukkit.World;

public class FermerCommand implements CommandExecutor {

    private final FarmerEconomist plugin;

    public FermerCommand(FarmerEconomist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("only_for_players"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("admin")) {
            handleAdminCommand(player, args);
            return true;
        }

        handleTeleport((Player) sender);

        return true;
    }

    private void handleAdminCommand(Player player, String[] args) {
        if (!player.hasPermission("farmereconomist.admin.npc")) {
            plugin.getConfigManager().sendMessage(player, "no_permission");
            return;
        }

        if (args.length < 2) {
            player.sendMessage("§cИспользование: /fermer admin <removenpc|forceremove>");
            return;
        }

        if (args[1].equalsIgnoreCase("removenpc")) {
            handleRemoveNpc(player);
        } else if (args[1].equalsIgnoreCase("forceremove")) {
            handleForceRemoveNpc(player);
        } else {
            player.sendMessage("§cНеизвестная админ-команда. Доступно: removenpc, forceremove");
        }
    }

    private void handleRemoveNpc(Player player) {
        UUID npcId = plugin.getNpcManager().getNpcUniqueId();
        if (npcId == null) {
            plugin.getConfigManager().sendMessage(player, "npc_not_set");
            return;
        }

        Entity npc = Bukkit.getEntity(npcId);
        if (npc != null && !npc.isDead()) {
            npc.remove();
            plugin.getNpcManager().clearNpcData(); // ПРЕДПОЛОЖЕНИЕ: этот метод очищает данные NPC из конфига
            plugin.getConfigManager().sendMessage(player, "npc_remove_success");
        } else {
            player.sendMessage("§cТорговец не найден в загруженных чанках. Подойдите к нему и повторите команду.");
            player.sendMessage("§eЕсли это не поможет, используйте §f/fermer admin forceremove §eдля принудительной очистки данных.");
        }
    }

    private void handleForceRemoveNpc(Player player) {
        plugin.getNpcManager().clearNpcData(); // ПРЕДПОЛОЖЕНИЕ: этот метод очищает данные NPC из конфига
        plugin.getConfigManager().sendMessage(player, "npc_force_remove_success");
    }

    private void handleTeleport(Player player) {
        UUID npcId = plugin.getNpcManager().getNpcUniqueId();
        if (npcId == null) {
            plugin.getConfigManager().sendMessage(player, "npc_not_set");
            return;
        }

        Entity npc = Bukkit.getEntity(npcId);
        if (npc == null) {
            plugin.getConfigManager().sendMessage(player, "npc_find_fail");
            return;
        }

        Location npcLocation = npc.getLocation();
        Location teleportLocation = findSafeTeleportLocation(npcLocation);

        if (teleportLocation != null) {
            player.teleport(teleportLocation);
            plugin.getConfigManager().sendMessage(player, "npc_teleport_success");
            plugin.getSoundManager().playSound(player, "teleport");
        } else {
            plugin.getConfigManager().sendMessage(player, "npc_teleport_fail_no_safe_spot");
            player.teleport(npcLocation.add(0,1,0)); // Fallback to on top of NPC if no safe spot
        }
    }

    private Location findSafeTeleportLocation(Location npcLocation) {
        World world = npcLocation.getWorld();
        double x = npcLocation.getX();
        double y = npcLocation.getY();
        double z = npcLocation.getZ();

        // Проверяем 4 направления вокруг NPC
        Location[] possibleLocations = {
                new Location(world, x + 2, y, z), // +X
                new Location(world, x - 2, y, z), // -X
                new Location(world, x, y, z + 2), // +Z
                new Location(world, x, y, z - 2)  // -Z
        };

        for (Location loc : possibleLocations) {
            // Проверяем, что блок свободен и над ним тоже свободно
            if (loc.getBlock().getType().isAir() && loc.clone().add(0, 1, 0).getBlock().getType().isAir()) {
                // Убедимся, что игрок не телепортируется в лаву/воду и т.д.
                if (!loc.getBlock().isLiquid() && loc.clone().add(0, -1, 0).getBlock().getType().isSolid()) {
                    return loc;
                }
            }
        }
        return null; // Не найдено безопасного места
    }
} 