package com.minecraft.garden.commands;

import com.minecraft.garden.GardenPlugin;
import com.minecraft.garden.managers.PlotManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
            case "tp":
            case "teleport":
                teleportToPlot(player);
                break;
            case "delete":
            case "remove":
                deletePlot(player);
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
            player.sendMessage("§e/garden tp §7- Телепорт на участок");
            player.sendMessage("§c/garden delete §7- Удалить участок");
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
            player.sendMessage("§e" + plot.getPlotInfo());
            player.sendMessage("§eКоординаты: §7" + plot.getCoordinates());
            player.sendMessage("§eТелепорт: §6/garden tp");
            
            // Показываем информацию о соседях
            String neighbors = getNeighborPlotsInfo(plot);
            if (!neighbors.equals("нет соседей")) {
                player.sendMessage("§eСоседние участки: §7" + neighbors);
            }
        } else {
            player.sendMessage("§cОшибка при создании участка!");
        }
    }
    
    private void teleportToPlot(Player player) {
        if (!plugin.getPlotManager().hasPlot(player.getUniqueId())) {
            player.sendMessage("§cУ вас нет участка! Используйте §6/garden create §cдля получения участка.");
            return;
        }
        
        PlotManager.PlotData plot = plugin.getPlotManager().getPlot(player.getUniqueId());
        if (plot == null) {
            player.sendMessage("§cОшибка: данные участка не найдены!");
            return;
        }
        
        String worldName = plugin.getConfigManager().getWorldName();
        World world = Bukkit.getWorld(worldName);
        
        if (world == null) {
            player.sendMessage("§cОшибка: мир '" + worldName + "' не найден!");
            player.sendMessage("§eДоступные миры: " + Bukkit.getWorlds().stream().map(World::getName).reduce("", (a, b) -> a + ", " + b));
            return;
        }
        
        // Используем новую логику телепортации
        Location teleportLocation = plot.getTeleportLocation(world);
        
        // Проверяем, что место безопасно для телепортации
        if (teleportLocation.getBlock().getType().isSolid()) {
            int highestY = world.getHighestBlockYAt(teleportLocation);
            teleportLocation.setY(highestY + 2);
            player.sendMessage("§eНайдена безопасная высота: Y=" + (highestY + 2));
        }
        
        // Дополнительная проверка безопасности
        Location safeLocation = findSafeLocation(world, teleportLocation);
        
        try {
            player.teleport(safeLocation);
            player.sendMessage("§aТелепорт на участок выполнен!");
            player.sendMessage("§eКоординаты участка: §7" + plot.getCoordinates());
            player.sendMessage("§eРазмер: §7" + plot.size + "x" + plot.size);
            player.sendMessage("§eТелепорт: §7X=" + (int)safeLocation.getX() + " Y=" + (int)safeLocation.getY() + " Z=" + (int)safeLocation.getZ());
        } catch (Exception e) {
            player.sendMessage("§cОшибка при телепортации: " + e.getMessage());
            plugin.getLogger().warning("Ошибка телепортации для игрока " + player.getName() + ": " + e.getMessage());
        }
    }
    
    private void deletePlot(Player player) {
        if (!plugin.getPlotManager().hasPlot(player.getUniqueId())) {
            player.sendMessage("§cУ вас нет участка для удаления!");
            return;
        }
        
        PlotManager.PlotData plot = plugin.getPlotManager().getPlot(player.getUniqueId());
        
        // Удаляем участок
        boolean success = plugin.getPlotManager().deletePlot(player.getUniqueId());
        
        if (success) {
            player.sendMessage("§aУчасток успешно удален!");
            player.sendMessage("§eУдаленный участок: §7ID " + plot.id + ", " + plot.getCoordinates());
            player.sendMessage("§eДля создания нового участка используйте: §6/garden create");
        } else {
            player.sendMessage("§cОшибка при удалении участка!");
        }
    }
    
    private Location findSafeLocation(World world, Location location) {
        // Ищем безопасное место для телепортации
        int x = location.getBlockX();
        int z = location.getBlockZ();
        
        // Проверяем центральную точку
        int y = world.getHighestBlockYAt(x, z) + 2;
        Location safeLoc = new Location(world, x, y, z);
        
        // Проверяем, что место безопасно
        if (isLocationSafe(world, safeLoc)) {
            return safeLoc;
        }
        
        // Если центральная точка небезопасна, ищем рядом
        for (int radius = 1; radius <= 3; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx == 0 && dz == 0) continue; // Пропускаем центральную точку
                    
                    int checkX = x + dx;
                    int checkZ = z + dz;
                    int checkY = world.getHighestBlockYAt(checkX, checkZ) + 2;
                    Location checkLoc = new Location(world, checkX, checkY, checkZ);
                    
                    if (isLocationSafe(world, checkLoc)) {
                        return checkLoc;
                    }
                }
            }
        }
        
        // Если ничего не нашли, возвращаем исходную локацию
        return safeLoc;
    }
    
    private boolean isLocationSafe(World world, Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        
        // Проверяем, что блок под ногами твёрдый
        if (world.getBlockAt(x, y - 1, z).getType().isAir()) {
            return false;
        }
        
        // Проверяем, что место для телепортации свободно
        if (world.getBlockAt(x, y, z).getType().isSolid()) {
            return false;
        }
        
        // Проверяем, что над головой есть место
        if (world.getBlockAt(x, y + 1, z).getType().isSolid()) {
            return false;
        }
        
        return true;
    }
    
    private void showPlotInfo(Player player) {
        if (!plugin.getPlotManager().hasPlot(player.getUniqueId())) {
            player.sendMessage("§cУ вас нет участка! Используйте §6/garden create §cдля получения участка.");
            return;
        }
        
        PlotManager.PlotData plot = plugin.getPlotManager().getPlot(player.getUniqueId());
        player.sendMessage("§6=== Ваш участок ===");
        player.sendMessage("§e" + plot.getPlotInfo());
        player.sendMessage("§eКоординаты: §7" + plot.getCoordinates());
        player.sendMessage("§eТелепорт: §6/garden tp");
        
        // Показываем соседние участки
        player.sendMessage("§eСоседние участки: §7" + getNeighborPlotsInfo(plot));
    }
    
    private String getNeighborPlotsInfo(PlotManager.PlotData currentPlot) {
        // В новой системе участки размещаются в области 116x133 блока
        // Примерно 3 участка в ряду (116 / (60+2+2) ≈ 3)
        int plotsPerRow = 3;
        int currentRow = currentPlot.id / plotsPerRow;
        int currentCol = currentPlot.id % plotsPerRow;
        
        StringBuilder info = new StringBuilder();
        
        // Проверяем соседние участки
        int[] neighbors = {
            currentPlot.id - 1, // слева
            currentPlot.id + 1, // справа
            currentPlot.id - plotsPerRow, // сверху
            currentPlot.id + plotsPerRow  // снизу
        };
        
        for (int neighborId : neighbors) {
            if (neighborId >= 0) {
                // Ищем владельца соседнего участка
                for (var entry : plugin.getPlotManager().getAllPlots().entrySet()) {
                    if (entry.getValue().id == neighborId) {
                        String ownerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
                        if (ownerName != null) {
                            info.append(ownerName).append(", ");
                        }
                        break;
                    }
                }
            }
        }
        
        if (info.length() > 0) {
            return info.substring(0, info.length() - 2); // Убираем последнюю запятую
        } else {
            return "нет соседей";
        }
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
        player.sendMessage("§e/garden tp §7- Телепорт на участок");
        player.sendMessage("§c/garden delete §7- Удалить участок");
        player.sendMessage("§e/garden shop §7- Магазин семян");
        player.sendMessage("§e/garden sell §7- Продажа урожая");
        player.sendMessage("§e/garden invite <игрок> §7- Пригласить игрока");
        player.sendMessage("§e/garden expand §7- Расширить участок");
        player.sendMessage("§e/garden help §7- Эта справка");
    }
} 