package com.minecraft.garden.commands;

import com.minecraft.garden.GardenPlugin;
import com.minecraft.garden.managers.PlotManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GardenCommand implements CommandExecutor {
    
    private final GardenPlugin plugin;
    
    public GardenCommand(GardenPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда только для игроков!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            // Главное меню
            showMainMenu(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "plot":
                showPlotInfo(player);
                break;
            case "create":
                createPlot(player);
                break;
            case "shop":
                showShop(player);
                break;
            case "sell":
                showSellMenu(player);
                break;
            case "invite":
                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /garden invite <игрок>");
                    return true;
                }
                invitePlayer(player, args[1]);
                break;
            case "expand":
                showExpandMenu(player);
                break;
            case "help":
                showHelp(player);
                break;
            default:
                player.sendMessage("§cНеизвестная команда. Используйте /garden help для справки.");
                break;
        }
        
        return true;
    }
    
    private void showMainMenu(Player player) {
        player.sendMessage("§6=== Меню сада ===");
        
        if (!plugin.getPlotManager().hasPlot(player.getUniqueId())) {
            player.sendMessage("§e/garden create §7- Получить участок");
        } else {
            player.sendMessage("§e/garden plot §7- Информация об участке");
        }
        
        player.sendMessage("§e/garden shop §7- Магазин семян");
        player.sendMessage("§e/garden sell §7- Продажа урожая");
        player.sendMessage("§e/garden invite <игрок> §7- Пригласить игрока");
        player.sendMessage("§e/garden expand §7- Расширить участок");
        player.sendMessage("§e/garden help §7- Справка");
        
        // Показываем баланс
        int balance = plugin.getEconomyManager().getBalance(player);
        player.sendMessage("§aВаш баланс: §e" + balance + " §aрублей");
    }
    
    private void createPlot(Player player) {
        if (plugin.getPlotManager().hasPlot(player.getUniqueId())) {
            player.sendMessage("§cУ вас уже есть участок!");
            return;
        }
        
        PlotManager.PlotData plot = plugin.getPlotManager().createPlot(player.getUniqueId());
        if (plot != null) {
            player.sendMessage("§aУчасток успешно создан!");
            player.sendMessage("§eID: §7" + plot.id);
            player.sendMessage("§eРазмер: §7" + plot.size + "x" + plot.size);
            player.sendMessage("§eКоординаты: §7X:" + plot.x + " Z:" + plot.z);
            player.sendMessage("§eТелепорт: §6/garden tp");
        } else {
            player.sendMessage("§cОшибка при создании участка!");
        }
    }
    
    private void showPlotInfo(Player player) {
        if (!plugin.getPlotManager().hasPlot(player.getUniqueId())) {
            player.sendMessage("§cУ вас нет участка! Используйте §6/garden create §cдля получения участка.");
            return;
        }
        
        PlotManager.PlotData plot = plugin.getPlotManager().getPlot(player.getUniqueId());
        player.sendMessage("§6=== Ваш участок ===");
        player.sendMessage("§eID: §7" + plot.id);
        player.sendMessage("§eРазмер: §7" + plot.size + "x" + plot.size);
        player.sendMessage("§eКоординаты: §7X:" + plot.x + " Z:" + plot.z);
        player.sendMessage("§eТелепорт: §6/garden tp");
    }
    
    private void showShop(Player player) {
        player.sendMessage("§6=== Магазин семян ===");
        player.sendMessage("§eПшеница: §72 рубля");
        player.sendMessage("§eМорковь: §73 рубля");
        player.sendMessage("§eКартофель: §73 рубля");
        player.sendMessage("§eСвекла: §72 рубля");
        player.sendMessage("§eТыква: §75 рублей");
        player.sendMessage("§eАрбуз: §75 рублей");
        player.sendMessage("§cМагазин пока не работает!");
    }
    
    private void showSellMenu(Player player) {
        player.sendMessage("§6=== Продажа урожая ===");
        player.sendMessage("§cФункция продажи пока не реализована!");
    }
    
    private void invitePlayer(Player player, String targetName) {
        player.sendMessage("§cСистема приглашений пока не реализована!");
    }
    
    private void showExpandMenu(Player player) {
        player.sendMessage("§6=== Расширение участка ===");
        player.sendMessage("§cФункция расширения пока не реализована!");
    }
    
    private void showHelp(Player player) {
        player.sendMessage("§6=== Справка по командам ===");
        player.sendMessage("§e/garden §7- Главное меню");
        player.sendMessage("§e/garden create §7- Получить участок");
        player.sendMessage("§e/garden plot §7- Информация об участке");
        player.sendMessage("§e/garden shop §7- Магазин семян");
        player.sendMessage("§e/garden sell §7- Продажа урожая");
        player.sendMessage("§e/garden invite <игрок> §7- Пригласить игрока");
        player.sendMessage("§e/garden expand §7- Расширить участок");
        player.sendMessage("§e/garden help §7- Эта справка");
    }
} 