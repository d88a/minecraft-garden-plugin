package com.github.d88a.farmereconomist.commands;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {

    private final FarmerEconomist plugin;

    public AdminCommand(FarmerEconomist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("farmereconomist.admin")) {
            plugin.getConfigManager().sendMessage(sender, "no_permission");
            return true;
        }

        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "createnpc":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Эту команду нужно выполнять от имени игрока.");
                    return true;
                }
                Player player = (Player) sender;
                String npcName = "Старый Мирон"; // Имя по умолчанию
                if (args.length > 1) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }
                    npcName = ChatColor.translateAlternateColorCodes('&', sb.toString().trim());
                }
                plugin.getNpcManager().createNpc(player.getLocation(), npcName);
                plugin.getConfigManager().sendMessage(sender, "npc_created_success");
                break;

            case "removenpc":
                boolean success = plugin.getNpcManager().removeNpc();
                if (success) {
                    plugin.getConfigManager().sendMessage(sender, "npc_removed_success");
                } else {
                    plugin.getConfigManager().sendMessage(sender, "npc_remove_fail_no_npc");
                }
                break;

            default:
                sendHelpMessage(sender);
                break;
        }

        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§a--- Команды администратора FarmerEconomist ---");
        sender.sendMessage("§e/feadmin createnpc [имя] §7- Создать NPC-торговца в вашей позиции.");
        sender.sendMessage("§e/feadmin removenpc §7- Удалить существующего NPC-торговца.");
    }
}