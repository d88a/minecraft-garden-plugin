package com.github.d88a.farmereconomist.commands;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.events.FarmingEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class EventCommand implements CommandExecutor {

    private final FarmerEconomist plugin;

    public EventCommand(FarmerEconomist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("only_for_players"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            showEventHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "status":
                showEventStatus(player);
                break;
            case "list":
                showEventList(player);
                break;
            case "start":
                if (!player.hasPermission("farmereconomist.admin.event")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /event start <событие>");
                    return true;
                }
                startEvent(player, args[1]);
                break;
            case "stop":
                if (!player.hasPermission("farmereconomist.admin.event")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
                    return true;
                }
                stopEvent(player);
                break;
            default:
                showEventHelp(player);
                break;
        }

        return true;
    }

    private void showEventHelp(Player player) {
        player.sendMessage("§a=== КОМАНДЫ СОБЫТИЙ ===");
        player.sendMessage("§7");
        player.sendMessage("§e/event status §7- Показать текущее событие");
        player.sendMessage("§e/event list §7- Список всех событий");
        
        if (player.hasPermission("farmereconomist.admin.event")) {
            player.sendMessage("§e/event start <событие> §7- Запустить событие");
            player.sendMessage("§e/event stop §7- Остановить текущее событие");
        }
        
        player.sendMessage("§7");
    }

    private void showEventStatus(Player player) {
        if (plugin.getFarmingEventManager().isEventActive()) {
            FarmingEvent currentEvent = plugin.getFarmingEventManager().getCurrentEvent();
            player.sendMessage("§a=== ТЕКУЩЕЕ СОБЫТИЕ ===");
            player.sendMessage("§7");
            player.sendMessage("§d🎉 " + currentEvent.getName());
            player.sendMessage("§7" + currentEvent.getDescription());
            player.sendMessage("§7" + plugin.getFarmingEventManager().getEventStatus());
        } else {
            player.sendMessage("§7Сейчас нет активных событий.");
        }
    }

    private void showEventList(Player player) {
        player.sendMessage("§a=== СПИСОК СОБЫТИЙ ===");
        player.sendMessage("§7");
        
        Map<String, FarmingEvent> events = plugin.getFarmingEventManager().getAllEvents();
        for (FarmingEvent event : events.values()) {
            String status = plugin.getFarmingEventManager().isEventActive(event.getId()) ? "§a●" : "§7○";
            player.sendMessage(status + " §e" + event.getName() + " §7- " + event.getDescription());
        }
        
        player.sendMessage("§7");
    }

    private void startEvent(Player player, String eventId) {
        try {
            plugin.getFarmingEventManager().startEvent(eventId);
            player.sendMessage("§aСобытие '" + eventId + "' успешно запущено!");
        } catch (Exception e) {
            player.sendMessage("§cОшибка при запуске события: " + e.getMessage());
        }
    }

    private void stopEvent(Player player) {
        if (plugin.getFarmingEventManager().isEventActive()) {
            plugin.getFarmingEventManager().stopCurrentEvent();
            player.sendMessage("§aТекущее событие остановлено!");
        } else {
            player.sendMessage("§7Сейчас нет активных событий.");
        }
    }
} 