package com.github.d88a.farmereconomist.commands;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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

        if (args.length > 0 && args[0].equalsIgnoreCase("set_npc")) {
            handleSetNpc(player);
        } else {
            handleTeleport(player);
        }

        return true;
    }

    private void handleSetNpc(Player player) {
        if (!player.hasPermission("farmereconomist.admin.set_npc")) {
            plugin.getConfigManager().sendMessage(player, "no_permission");
            return;
        }
        
        // Remove old NPC if exists
        UUID oldNpcId = plugin.getNpcManager().getNpcUniqueId();
        if (oldNpcId != null) {
            Entity oldNpc = Bukkit.getEntity(oldNpcId);
            if (oldNpc != null) {
                oldNpc.remove();
            }
        }

        // Spawn new NPC
        Location location = player.getLocation();
        Villager npc = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        npc.setAI(false);
        npc.setInvulnerable(true);
        npc.setCustomName(ChatColor.GOLD + "Старый Мирон");
        npc.setCustomNameVisible(true);
        npc.setVillagerType(Villager.Type.PLAINS); // You can customize this
        npc.setProfession(Villager.Profession.FARMER); // You can customize this
        
        plugin.getNpcManager().setNpcUniqueId(npc.getUniqueId());
        plugin.getConfigManager().sendMessage(player, "npc_set_success");
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

        player.teleport(npc.getLocation());
        plugin.getConfigManager().sendMessage(player, "npc_teleport_success");
        plugin.getSoundManager().playSound(player, "teleport");
    }
} 