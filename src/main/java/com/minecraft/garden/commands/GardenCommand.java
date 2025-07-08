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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;

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
            case "buy":
                if (args.length < 3) {
                    player.sendMessage("§cИспользование: /garden buy <семя> <количество>");
                    player.sendMessage("§eПример: /garden buy wheat 5");
                    return true;
                }
                buySeeds(player, args[1], args[2]);
                break;
            case "sell":
                if (args.length < 2) {
                    showSellMenu(player);
                    return true;
                }
                if (args[1].equalsIgnoreCase("all")) {
                    sellAllCrops(player);
                } else if (args.length >= 3) {
                    sellCrops(player, args[1], args[2]);
                } else {
                    player.sendMessage("§cИспользование: /garden sell <урожай> <количество>");
                    player.sendMessage("§eИли: /garden sell all");
                }
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
            case "balance":
                int balance = plugin.getEconomyManager().getBalance(player);
                player.sendMessage("§aВаш баланс: §e" + balance + " §aрублей");
                break;
            case "test":
                testCustomSeeds(player);
                break;
            case "debug":
                debugSystem(player);
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
        // Открываем графический интерфейс вместо текстового меню
        plugin.getGuiManager().openMainMenu(player);
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
        // Открываем графический интерфейс магазина
        plugin.getGuiManager().openShop(player);
    }
    
    private void buySeeds(Player player, String seedName, String amountStr) {
        // Проверяем количество
        int amount;
        try {
            amount = Integer.parseInt(amountStr);
            if (amount <= 0 || amount > 64) {
                player.sendMessage("§cКоличество должно быть от 1 до 64!");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§cНеверное количество!");
            return;
        }
        
        // Определяем тип семени
        Material seedType = getSeedMaterial(seedName);
        if (seedType == null) {
            player.sendMessage("§cНеизвестное семя: " + seedName);
            player.sendMessage("§eДоступные семена: wheat, carrot, potato, beetroot, pumpkin, melon");
            return;
        }
        
        // Получаем цену
        int price = plugin.getConfigManager().getSeedPrice(seedName);
        int totalCost = price * amount;
        
        // Проверяем баланс
        if (!plugin.getEconomyManager().hasMoney(player, totalCost)) {
            player.sendMessage("§cНедостаточно денег! Нужно: §e" + totalCost + " §cрублей");
            return;
        }
        
        // Списываем деньги
        plugin.getEconomyManager().withdrawMoney(player, totalCost);
        
        // Даем семена
        ItemStack customSeed = plugin.getCustomItemManager().getCustomSeed(seedType);
        customSeed.setAmount(amount);
        
        // Проверяем, есть ли уже такие семена в инвентаре
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack existingItem = contents[i];
            if (existingItem != null && 
                existingItem.getType() == customSeed.getType() && 
                existingItem.hasItemMeta() && 
                existingItem.getItemMeta().hasDisplayName() &&
                existingItem.getItemMeta().getDisplayName().equals(customSeed.getItemMeta().getDisplayName())) {
                
                // Добавляем к существующему стаку
                int spaceInStack = 64 - existingItem.getAmount();
                if (spaceInStack > 0) {
                    int toAdd = Math.min(amount, spaceInStack);
                    existingItem.setAmount(existingItem.getAmount() + toAdd);
                    amount -= toAdd;
                    
                    if (amount <= 0) break;
                }
            }
        }
        
        // Если остались семена, добавляем новый стак
        if (amount > 0) {
            customSeed.setAmount(amount);
            player.getInventory().addItem(customSeed);
        }
        
        // Вычисляем общее количество выданных семян
        int totalGiven = Integer.parseInt(amountStr) - amount;
        
        player.sendMessage("§aПокупка успешна!");
        player.sendMessage("§eПолучено: §7" + totalGiven + "x " + getSeedDisplayName(seedName));
        player.sendMessage("§eСтоимость: §7" + totalCost + " рублей");
        
        // Обновляем баланс
        int newBalance = plugin.getEconomyManager().getBalance(player);
        player.sendMessage("§aНовый баланс: §e" + newBalance + " §aрублей");
    }
    
    private Material getSeedMaterial(String seedName) {
        switch (seedName.toLowerCase()) {
            case "wheat": return Material.WHEAT_SEEDS;
            case "carrot": return Material.CARROT;
            case "potato": return Material.POTATO;
            case "beetroot": return Material.BEETROOT_SEEDS;
            case "pumpkin": return Material.PUMPKIN_SEEDS;
            case "melon": return Material.MELON_SEEDS;
            default: return null;
        }
    }
    
    private String getSeedDisplayName(String seedName) {
        switch (seedName.toLowerCase()) {
            case "wheat": return "Семена пшеницы";
            case "carrot": return "Морковь";
            case "potato": return "Картофель";
            case "beetroot": return "Семена свеклы";
            case "pumpkin": return "Семена тыквы";
            case "melon": return "Семена арбуза";
            default: return seedName;
        }
    }
    
    private void showSellMenu(Player player) {
        // Открываем графический интерфейс продажи
        plugin.getGuiManager().openSellMenu(player);
    }
    
    private void invitePlayer(Player player, String targetName) {
        player.sendMessage("§cСистема приглашений пока не реализована!");
    }
    
    private void showExpandMenu(Player player) {
        // Открываем графический интерфейс расширения
        plugin.getGuiManager().openExpandMenu(player);
    }
    
    private void showHelp(Player player) {
        player.sendMessage("§6=== Команды плагина 'Вырасти сад' ===");
        player.sendMessage("§e/garden create §7- Создать участок");
        player.sendMessage("§e/garden expand §7- Расширить участок");
        player.sendMessage("§e/garden shop §7- Магазин семян");
        player.sendMessage("§e/garden sell §7- Продажа урожая");
        player.sendMessage("§e/garden buy <семя> <количество> §7- Купить семена");
        player.sendMessage("§e/garden sell <урожай> <количество> §7- Продать урожай");
        player.sendMessage("§e/garden balance §7- Проверить баланс");
        player.sendMessage("§e/garden give <семя> <количество> §7- Выдать тестовые семена");
        player.sendMessage("§e/garden plant <семя> §7- Тестовая посадка");
        player.sendMessage("§e/garden till §7- Вспахать землю на участке");
        player.sendMessage("§e/garden test §7- Тестовая команда");
        player.sendMessage("§e/garden debug §7- Отладка системы");
        player.sendMessage("§e/garden help §7- Эта справка");
        player.sendMessage("§7");
        player.sendMessage("§6=== Игровой процесс ===");
        player.sendMessage("§e1. Создайте участок: §6/garden create");
        player.sendMessage("§e2. Купите семена: §6/garden shop");
        player.sendMessage("§e3. Посадите семена на участке");
        player.sendMessage("§e4. Дождитесь созревания");
        player.sendMessage("§e5. Соберите урожай правым кликом");
        player.sendMessage("§e6. Продайте урожай: §6/garden sell");
    }
    
    private void sellCrops(Player player, String cropName, String amountStr) {
        // Проверяем количество
        int amount;
        try {
            amount = Integer.parseInt(amountStr);
            if (amount <= 0) {
                player.sendMessage("§cКоличество должно быть больше 0!");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§cНеверное количество!");
            return;
        }
        
        // Определяем тип урожая
        Material cropType = getCropMaterial(cropName);
        if (cropType == null) {
            player.sendMessage("§cНеизвестный урожай: " + cropName);
            player.sendMessage("§eДоступные урожаи: wheat, carrot, potato, beetroot, pumpkin, melon");
            return;
        }
        
        // Проверяем, есть ли у игрока нужное количество
        int playerAmount = getPlayerCropAmount(player, cropType);
        if (playerAmount < amount) {
            player.sendMessage("§cУ вас недостаточно урожая! Есть: §e" + playerAmount + "x " + getCropDisplayName(cropName));
            return;
        }
        
        // Получаем цену
        int price = plugin.getConfigManager().getCropPrice(cropName);
        int totalEarnings = price * amount;
        
        // Убираем урожай из инвентаря
        removeCropsFromInventory(player, cropType, amount);
        
        // Даем деньги
        plugin.getEconomyManager().addMoney(player, totalEarnings);
        
        player.sendMessage("§aПродажа успешна!");
        player.sendMessage("§eПродано: §7" + amount + "x " + getCropDisplayName(cropName));
        player.sendMessage("§eЗаработано: §7" + totalEarnings + " рублей");
        
        // Обновляем баланс
        int newBalance = plugin.getEconomyManager().getBalance(player);
        player.sendMessage("§aНовый баланс: §e" + newBalance + " §aрублей");
    }
    
    private void sellAllCrops(Player player) {
        int totalEarnings = 0;
        int totalSold = 0;
        
        // Проверяем все типы урожая
        Material[] cropTypes = {
            Material.WHEAT, Material.CARROT, Material.POTATO, 
            Material.BEETROOT, Material.PUMPKIN, Material.MELON
        };
        
        for (Material cropType : cropTypes) {
            int playerAmount = getPlayerCropAmount(player, cropType);
            if (playerAmount > 0) {
                String cropName = getCropName(cropType);
                int price = plugin.getConfigManager().getCropPrice(cropName);
                int earnings = price * playerAmount;
                
                totalEarnings += earnings;
                totalSold += playerAmount;
                
                // Убираем урожай из инвентаря
                removeCropsFromInventory(player, cropType, playerAmount);
                
                player.sendMessage("§eПродано §7" + playerAmount + "x " + getCropDisplayName(cropName) + " §eза §7" + earnings + " §eрублей");
            }
        }
        
        if (totalSold > 0) {
            // Даем деньги
            plugin.getEconomyManager().addMoney(player, totalEarnings);
            
            player.sendMessage("§aПродажа завершена!");
            player.sendMessage("§eВсего продано: §7" + totalSold + " §eпредметов");
            player.sendMessage("§eОбщий заработок: §7" + totalEarnings + " §eрублей");
            
            // Обновляем баланс
            int newBalance = plugin.getEconomyManager().getBalance(player);
            player.sendMessage("§aНовый баланс: §e" + newBalance + " §aрублей");
        } else {
            player.sendMessage("§cУ вас нет урожая для продажи!");
        }
    }
    
    private Material getCropMaterial(String cropName) {
        switch (cropName.toLowerCase()) {
            case "wheat": return Material.WHEAT;
            case "carrot": return Material.CARROT;
            case "potato": return Material.POTATO;
            case "beetroot": return Material.BEETROOT;
            case "pumpkin": return Material.PUMPKIN;
            case "melon": return Material.MELON;
            default: return null;
        }
    }
    
    private String getCropName(Material cropType) {
        switch (cropType) {
            case WHEAT: return "wheat";
            case CARROT: return "carrot";
            case POTATO: return "potato";
            case BEETROOT: return "beetroot";
            case PUMPKIN: return "pumpkin";
            case MELON: return "melon";
            default: return cropType.name().toLowerCase();
        }
    }
    
    private String getCropDisplayName(String cropName) {
        switch (cropName.toLowerCase()) {
            case "wheat": return "Пшеница";
            case "carrot": return "Морковь";
            case "potato": return "Картофель";
            case "beetroot": return "Свекла";
            case "pumpkin": return "Тыква";
            case "melon": return "Арбуз";
            default: return cropName;
        }
    }
    
    private int getPlayerCropAmount(Player player, Material cropType) {
        int total = 0;
        for (org.bukkit.inventory.ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == cropType) {
                total += item.getAmount();
            }
        }
        return total;
    }
    
    private void removeCropsFromInventory(Player player, Material cropType, int amount) {
        int remaining = amount;
        
        for (int i = 0; i < player.getInventory().getSize() && remaining > 0; i++) {
            org.bukkit.inventory.ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == cropType) {
                if (item.getAmount() <= remaining) {
                    remaining -= item.getAmount();
                    player.getInventory().setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - remaining);
                    remaining = 0;
                }
            }
        }
    }

    private void giveHarvestToPlayer(Player player, Material cropType) {
        // Даем кастомный урожай
        ItemStack customCrop = plugin.getCustomItemManager().getCustomCrop(cropType);
        player.getInventory().addItem(customCrop);
        
        String cropName = getCropName(cropType);
        int price = plugin.getConfigManager().getCropPrice(cropName);
        
        player.sendMessage("§aСобран урожай: §e" + getCropDisplayName(cropName));
        player.sendMessage("§eЦена продажи: §7" + price + " рублей");
    }

    private void testCustomSeeds(Player player) {
        player.sendMessage("§aТестовая команда для отладки кастомных семян.");
        player.sendMessage("§eВы можете попробовать купить кастомные семена.");
        player.sendMessage("§eИспользуйте: §6/garden buy <семя> <количество>");
        player.sendMessage("§eПример: §6/garden buy custom_wheat 10");
    }

    private void testPlanting(Player player, String seedName) {
        Material seedType = getSeedMaterial(seedName);
        if (seedType == null) {
            player.sendMessage("§cНеизвестное семя для посадки: " + seedName);
            player.sendMessage("§eДоступные семена: wheat, carrot, potato, beetroot, pumpkin, melon");
            return;
        }

        ItemStack customSeed = plugin.getCustomItemManager().getCustomSeed(seedType);
        if (customSeed == null) {
            player.sendMessage("§cОшибка: кастомное семя не найдено для типа " + seedType.name());
            return;
        }

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
            return;
        }

        Location plantLocation = plot.getPlantLocation(world);
        if (plantLocation == null) {
            player.sendMessage("§cОшибка: место для посадки не найдено!");
            return;
        }

        if (world.getBlockAt(plantLocation).getType().isSolid()) {
            player.sendMessage("§cМесто для посадки занято!");
            return;
        }

        if (!isLocationSafe(world, plantLocation)) {
            player.sendMessage("§cМесто для посадки небезопасно!");
            return;
        }

        // Удаляем семена из инвентаря
        player.getInventory().removeItem(customSeed);

        // Посадка
        plugin.getPlotManager().plantCrop(player.getUniqueId(), seedType, plantLocation);
        player.sendMessage("§aСемена посажены!");
        player.sendMessage("§eКоординаты посадки: §7" + plantLocation.getBlockX() + ", " + plantLocation.getBlockY() + ", " + plantLocation.getBlockZ());
    }

    private void giveTestSeeds(Player player, String seedName, String amountStr) {
        int amount;
        try {
            amount = Integer.parseInt(amountStr);
            if (amount <= 0 || amount > 64) {
                player.sendMessage("§cКоличество должно быть от 1 до 64!");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§cНеверное количество!");
            return;
        }

        Material seedType = getSeedMaterial(seedName);
        if (seedType == null) {
            player.sendMessage("§cНеизвестное семя: " + seedName);
            player.sendMessage("§eДоступные семена: wheat, carrot, potato, beetroot, pumpkin, melon");
            return;
        }

        ItemStack customSeed = plugin.getCustomItemManager().getCustomSeed(seedType);
        customSeed.setAmount(amount);
        player.getInventory().addItem(customSeed);

        player.sendMessage("§aВыдано тестовых семян!");
        player.sendMessage("§eПолучено: §7" + amount + "x " + getSeedDisplayName(seedName));
    }
    
    private void debugSystem(Player player) {
        player.sendMessage("§6=== Отладка системы ===");
        
        // Проверяем участок
        if (plugin.getPlotManager().hasPlot(player.getUniqueId())) {
            PlotManager.PlotData plot = plugin.getPlotManager().getPlot(player.getUniqueId());
            player.sendMessage("§a✓ Участок найден: " + plot.getPlotInfo());
            
            // Проверяем место посадки
            String worldName = plugin.getConfigManager().getWorldName();
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                Location plantLoc = plot.getPlantLocation(world);
                Location tillLoc = plot.getTillLocation(world);
                
                player.sendMessage("§eМесто посадки: " + plantLoc.getBlockX() + ", " + plantLoc.getBlockY() + ", " + plantLoc.getBlockZ());
                player.sendMessage("§eМесто вспашки: " + tillLoc.getBlockX() + ", " + tillLoc.getBlockY() + ", " + tillLoc.getBlockZ());
                
                // Проверяем блоки
                Block plantBlock = world.getBlockAt(plantLoc);
                Block tillBlock = world.getBlockAt(tillLoc);
                Block belowPlantBlock = world.getBlockAt(plantLoc.clone().subtract(0, 1, 0));
                
                player.sendMessage("§eБлок посадки: " + plantBlock.getType());
                player.sendMessage("§eБлок вспашки: " + tillBlock.getType());
                player.sendMessage("§eБлок под посадкой: " + belowPlantBlock.getType());
            }
        } else {
            player.sendMessage("§c✗ Участок не найден");
        }
        
        // Проверяем баланс
        int balance = plugin.getEconomyManager().getBalance(player);
        player.sendMessage("§eБаланс: " + balance + " рублей");
        
        // Проверяем кастомные семена
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand != null && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName()) {
            String displayName = itemInHand.getItemMeta().getDisplayName();
            boolean isCustom = plugin.getCustomItemManager().isCustomSeed(itemInHand);
            player.sendMessage("§eПредмет в руке: " + itemInHand.getType());
            player.sendMessage("§eНазвание: " + displayName);
            player.sendMessage("§eКастомное семя: " + (isCustom ? "✓ Да" : "✗ Нет"));
        } else {
            player.sendMessage("§eПредмет в руке: " + (itemInHand != null ? itemInHand.getType() : "пусто"));
        }
        
        player.sendMessage("§6=== Конец отладки ===");
    }

    private void tillPlot(Player player) {
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
            return;
        }

        Location tillLocation = plot.getTillLocation(world);
        if (tillLocation == null) {
            player.sendMessage("§cОшибка: место для вспашки не найдено!");
            return;
        }

        if (world.getBlockAt(tillLocation).getType().isSolid()) {
            player.sendMessage("§cМесто для вспашки занято!");
            return;
        }

        if (!isLocationSafe(world, tillLocation)) {
            player.sendMessage("§cМесто для вспашки небезопасно!");
            return;
        }

        // Вспашка
        plugin.getPlotManager().tillPlot(player.getUniqueId(), tillLocation);
        player.sendMessage("§aУчасток вспахан!");
        player.sendMessage("§eКоординаты вспаханного участка: §7" + tillLocation.getBlockX() + ", " + tillLocation.getBlockY() + ", " + tillLocation.getBlockZ());
    }
} 