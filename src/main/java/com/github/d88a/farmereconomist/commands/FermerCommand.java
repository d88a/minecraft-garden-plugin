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
        player.sendMessage("Позиция NPC 'Старый Мирон' установлена здесь."); // TODO: from config
    }

    private void handleTeleport(Player player) {
        UUID npcId = plugin.getNpcManager().getNpcUniqueId();
        if (npcId == null) {
            player.sendMessage("NPC 'Старый Мирон' еще не установлен администратором."); // TODO: from config
            return;
        }

        Entity npc = Bukkit.getEntity(npcId);
        if (npc == null) {
            player.sendMessage("Не удалось найти NPC 'Старый Мирон'. Возможно, он был удален. Попросите администратора переустановить его.");
            return;
        }

        player.teleport(npc.getLocation());
        player.sendMessage("Вы телепортированы к Старому Мирону."); // TODO: from config
    }
} 