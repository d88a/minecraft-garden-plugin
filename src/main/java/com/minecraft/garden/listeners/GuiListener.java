package com.minecraft.garden.listeners;

import com.minecraft.garden.GardenPlugin;
import com.minecraft.garden.managers.GuiManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import com.minecraft.garden.managers.PlotManager;

public class GuiListener implements Listener {
    
    private final GardenPlugin plugin;
    
    public GuiListener(GardenPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        
        // Проверяем, что это наш GUI
        if (!plugin.getGuiManager().hasOpenGui(player)) {
            return;
        }
        
        // Отменяем стандартное взаимодействие
        event.setCancelled(true);
        
        // Получаем информацию о клике
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        
        // Обрабатываем клик в зависимости от типа GUI
        GuiManager.GuiType guiType = plugin.getGuiManager().getOpenGui(player);
        
        switch (guiType) {
            case MAIN_MENU:
                handleMainMenuClick(player, clickedItem);
                break;
            case SHOP:
                handleShopClick(player, clickedItem);
                break;
            case SELL_MENU:
                handleSellMenuClick(player, clickedItem);
                break;
            case EXPAND_MENU:
                handleExpandMenuClick(player, clickedItem);
                break;
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getPlayer();
        
        // Удаляем запись об открытом GUI
        if (plugin.getGuiManager().hasOpenGui(player)) {
            plugin.getGuiManager().closeGui(player);
        }
    }
    
    private void handleMainMenuClick(Player player, ItemStack clickedItem) {
        Material material = clickedItem.getType();
        
        switch (material) {
            case GRASS_BLOCK:
                if (plugin.getPlotManager().hasPlot(player.getUniqueId())) {
                    // Показываем информацию об участке
                    showPlotInfo(player);
                } else {
                    // Создаем участок
                    createPlot(player);
                }
                break;
                
            case ENDER_PEARL:
                if (plugin.getPlotManager().hasPlot(player.getUniqueId())) {
                    teleportToPlot(player);
                }
                break;
                
            case WOODEN_HOE:
                if (plugin.getPlotManager().hasPlot(player.getUniqueId())) {
                    tillPlot(player);
                } else {
                    player.sendMessage("§cУ вас нет участка для вспашки!");
                }
                break;
                
            case BARRIER:
                if (plugin.getPlotManager().hasPlot(player.getUniqueId())) {
                    deletePlot(player);
                } else {
                    player.sendMessage("§cУ вас нет участка для удаления!");
                }
                break;
                
            case WHEAT_SEEDS:
                plugin.getGuiManager().openShop(player);
                break;
                
            case GOLD_INGOT:
                plugin.getGuiManager().openSellMenu(player);
                break;
                
            case IRON_AXE:
                plugin.getGuiManager().openExpandMenu(player);
                break;
                
            case BOOK:
                showHelp(player);
                break;
                
            case EMERALD:
                // Обновляем баланс
                plugin.getGuiManager().openMainMenu(player);
                break;
        }
    }
    
    private void handleShopClick(Player player, ItemStack clickedItem) {
        Material material = clickedItem.getType();
        
        switch (material) {
            case WHEAT_SEEDS:
                buySeed(player, "wheat");
                break;
                
            case CARROT:
                buySeed(player, "carrot");
                break;
                
            case POTATO:
                buySeed(player, "potato");
                break;
                
            case BEETROOT_SEEDS:
                buySeed(player, "beetroot");
                break;
                
            case PUMPKIN_SEEDS:
                buySeed(player, "pumpkin");
                break;
                
            case MELON_SEEDS:
                buySeed(player, "melon");
                break;
                
            case ARROW:
                plugin.getGuiManager().openMainMenu(player);
                break;
                
            case EMERALD:
                // Обновляем баланс
                plugin.getGuiManager().openShop(player);
                break;
        }
    }
    
    private void handleSellMenuClick(Player player, ItemStack clickedItem) {
        Material material = clickedItem.getType();
        
        switch (material) {
            case WHEAT:
                sellCrops(player, "wheat", 1);
                break;
                
            case CARROT:
                sellCrops(player, "carrot", 1);
                break;
                
            case POTATO:
                sellCrops(player, "potato", 1);
                break;
                
            case BEETROOT:
                sellCrops(player, "beetroot", 1);
                break;
                
            case PUMPKIN:
                sellCrops(player, "pumpkin", 1);
                break;
                
            case MELON:
                sellCrops(player, "melon", 1);
                break;
                
            case HOPPER:
                sellAllCrops(player);
                break;
                
            case ARROW:
                plugin.getGuiManager().openMainMenu(player);
                break;
                
            case EMERALD:
                // Обновляем баланс
                plugin.getGuiManager().openSellMenu(player);
                break;
        }
    }
    
    private void handleExpandMenuClick(Player player, ItemStack clickedItem) {
        Material material = clickedItem.getType();
        
        switch (material) {
            case IRON_AXE:
                // Обрабатываем расширение участка
                handlePlotExpansion(player, clickedItem);
                break;
                
            case ARROW:
                plugin.getGuiManager().openMainMenu(player);
                break;
                
            case EMERALD:
                // Обновляем баланс
                plugin.getGuiManager().openExpandMenu(player);
                break;
        }
    }
    
    private void handlePlotExpansion(Player player, ItemStack clickedItem) {
        // Проверяем, есть ли у игрока участок
        if (!plugin.getPlotManager().hasPlot(player.getUniqueId())) {
            player.sendMessage("§cУ вас нет участка!");
            return;
        }
        
        PlotManager.PlotData plot = plugin.getPlotManager().getPlot(player.getUniqueId());
        int currentSize = plot.size;
        
        // Извлекаем новый размер из названия предмета
        String itemName = clickedItem.getItemMeta().getDisplayName();
        if (!itemName.contains("Расширить до")) {
            return;
        }
        
        // Парсим размер из названия (например, "Расширить до 10x10")
        String sizeStr = itemName.replace("§aРасширить до ", "").replace("x", "");
        int newSize;
        try {
            newSize = Integer.parseInt(sizeStr);
        } catch (NumberFormatException e) {
            player.sendMessage("§cОшибка при определении размера!");
            return;
        }
        
        // Проверяем, что новый размер больше текущего
        if (newSize <= currentSize) {
            player.sendMessage("§cНовый размер должен быть больше текущего!");
            return;
        }
        
        // Проверяем, что новый размер не превышает максимальный
        if (newSize > plot.maxSize) {
            player.sendMessage("§cНовый размер превышает максимальный!");
            return;
        }
        
        // Вычисляем стоимость
        int cost = (newSize - currentSize) * plugin.getConfigManager().getPlotExpansionCost();
        
        // Проверяем баланс
        if (!plugin.getEconomyManager().hasMoney(player, cost)) {
            player.sendMessage("§cНедостаточно денег! Нужно: §e" + cost + " §cрублей");
            return;
        }
        
        // Списываем деньги
        plugin.getEconomyManager().withdrawMoney(player, cost);
        
        // Расширяем участок
        boolean success = plugin.getPlotManager().expandPlot(player.getUniqueId(), newSize);
        
        if (success) {
            player.sendMessage("§aУчасток успешно расширен!");
            player.sendMessage("§eНовый размер: §7" + newSize + "x" + newSize);
            player.sendMessage("§eСтоимость: §7" + cost + " рублей");
            
            // Обновляем GUI
            plugin.getGuiManager().openExpandMenu(player);
        } else {
            // Возвращаем деньги, если расширение не удалось
            plugin.getEconomyManager().addMoney(player, cost);
            player.sendMessage("§cОшибка при расширении участка!");
        }
    }
    
    // Методы из GardenCommand (копируем для удобства)
    private void showPlotInfo(Player player) {
        if (!plugin.getPlotManager().hasPlot(player.getUniqueId())) {
            player.sendMessage("§cУ вас нет участка! Используйте §6/garden create §cдля получения участка.");
            return;
        }
        
        var plot = plugin.getPlotManager().getPlot(player.getUniqueId());
        player.sendMessage("§6=== Ваш участок ===");
        player.sendMessage("§e" + plot.getPlotInfo());
        player.sendMessage("§eКоординаты: §7" + plot.getCoordinates());
        player.sendMessage("§eТелепорт: §6/garden tp");
    }
    
    private void createPlot(Player player) {
        if (plugin.getPlotManager().hasPlot(player.getUniqueId())) {
            player.sendMessage("§cУ вас уже есть участок!");
            return;
        }
        
        var plot = plugin.getPlotManager().createPlot(player.getUniqueId());
        if (plot != null) {
            player.sendMessage("§aУчасток успешно создан!");
            player.sendMessage("§e" + plot.getPlotInfo());
            player.sendMessage("§eКоординаты: §7" + plot.getCoordinates());
            player.sendMessage("§eТелепорт: §6/garden tp");
        } else {
            player.sendMessage("§cОшибка при создании участка!");
        }
    }
    
    private void teleportToPlot(Player player) {
        if (!plugin.getPlotManager().hasPlot(player.getUniqueId())) {
            player.sendMessage("§cУ вас нет участка! Используйте §6/garden create §cдля получения участка.");
            return;
        }
        
        var plot = plugin.getPlotManager().getPlot(player.getUniqueId());
        if (plot == null) {
            player.sendMessage("§cОшибка: данные участка не найдены!");
            return;
        }
        
        String worldName = plugin.getConfigManager().getWorldName();
        var world = plugin.getServer().getWorld(worldName);
        
        if (world == null) {
            player.sendMessage("§cОшибка: мир '" + worldName + "' не найден!");
            return;
        }
        
        var teleportLocation = plot.getTeleportLocation(world);
        
        try {
            player.teleport(teleportLocation);
            player.sendMessage("§aТелепорт на участок выполнен!");
            player.sendMessage("§eКоординаты участка: §7" + plot.getCoordinates());
        } catch (Exception e) {
            player.sendMessage("§cОшибка при телепортации: " + e.getMessage());
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
            player.sendMessage("§eРазмер участка был: §7" + plot.size + "x" + plot.size);
            player.sendMessage("§eКоординаты: §7" + plot.getCoordinates());
        } else {
            player.sendMessage("§cОшибка при удалении участка!");
        }
        
        // Обновляем GUI
        plugin.getGuiManager().openMainMenu(player);
    }
    
    private void tillPlot(Player player) {
        if (!plugin.getPlotManager().hasPlot(player.getUniqueId())) {
            player.sendMessage("§cУ вас нет участка для вспашки!");
            return;
        }
        
        PlotManager.PlotData plot = plugin.getPlotManager().getPlot(player.getUniqueId());
        if (plot == null) {
            player.sendMessage("§cОшибка: данные участка не найдены!");
            return;
        }
        
        String worldName = plugin.getConfigManager().getWorldName();
        org.bukkit.World world = plugin.getServer().getWorld(worldName);
        if (world == null) {
            player.sendMessage("§cОшибка: мир '" + worldName + "' не найден!");
            return;
        }
        
        org.bukkit.Location tillLocation = plot.getTillLocation(world);
        if (tillLocation == null) {
            player.sendMessage("§cОшибка: место для вспашки не найдено!");
            return;
        }
        
        if (world.getBlockAt(tillLocation).getType().isSolid()) {
            player.sendMessage("§cМесто для вспашки занято!");
            return;
        }
        
        // Вспашка
        boolean success = plugin.getPlotManager().tillPlot(player.getUniqueId(), tillLocation);
        if (success) {
            player.sendMessage("§aУчасток вспахан!");
            player.sendMessage("§eКоординаты вспаханного участка: §7" + tillLocation.getBlockX() + ", " + tillLocation.getBlockY() + ", " + tillLocation.getBlockZ());
        } else {
            player.sendMessage("§cОшибка при вспашке участка!");
        }
        
        // Обновляем GUI
        plugin.getGuiManager().openMainMenu(player);
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
        player.sendMessage("§e/garden help §7- Эта справка");
    }
    
    private void buySeed(Player player, String seedName) {
        // Проверяем баланс
        int price = plugin.getConfigManager().getSeedPrice(seedName);
        if (!plugin.getEconomyManager().hasMoney(player, price)) {
            player.sendMessage("§cНедостаточно денег! Нужно: §e" + price + " §cрублей");
            return;
        }
        
        // Списываем деньги
        plugin.getEconomyManager().withdrawMoney(player, price);
        
        // Определяем тип семени
        Material seedType = getSeedMaterial(seedName);
        if (seedType == null) {
            player.sendMessage("§cНеизвестное семя: " + seedName);
            return;
        }
        
        // Даем кастомное семя
        ItemStack customSeed = plugin.getCustomItemManager().getCustomSeed(seedType);
        player.getInventory().addItem(customSeed);
        
        player.sendMessage("§aПокупка успешна!");
        player.sendMessage("§eПолучено: §7" + getSeedDisplayName(seedName));
        player.sendMessage("§eСтоимость: §7" + price + " рублей");
        
        // Обновляем баланс
        int newBalance = plugin.getEconomyManager().getBalance(player);
        player.sendMessage("§aНовый баланс: §e" + newBalance + " §aрублей");
    }
    
    private void sellCrops(Player player, String cropName, int amount) {
        Material cropType = getCropMaterial(cropName);
        if (cropType == null) {
            player.sendMessage("§cНеизвестный урожай: " + cropName);
            return;
        }
        
        int playerAmount = getPlayerCropAmount(player, cropType);
        if (playerAmount < amount) {
            player.sendMessage("§cУ вас недостаточно урожая! Есть: §e" + playerAmount + "x " + getCropDisplayName(cropName));
            return;
        }
        
        int price = plugin.getConfigManager().getCropPrice(cropName);
        int totalEarnings = price * amount;
        
        removeCropsFromInventory(player, cropType, amount);
        plugin.getEconomyManager().addMoney(player, totalEarnings);
        
        player.sendMessage("§aПродажа успешна! Продано: §e" + amount + "x " + getCropDisplayName(cropName));
        
        // Обновляем GUI
        plugin.getGuiManager().openSellMenu(player);
    }
    
    private void sellAllCrops(Player player) {
        int totalEarnings = 0;
        int totalSold = 0;
        
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
                
                removeCropsFromInventory(player, cropType, playerAmount);
                player.sendMessage("§eПродано §7" + playerAmount + "x " + getCropDisplayName(cropName) + " §eза §7" + earnings + " §eрублей");
            }
        }
        
        if (totalSold > 0) {
            plugin.getEconomyManager().addMoney(player, totalEarnings);
            player.sendMessage("§aПродажа завершена! Общий заработок: §7" + totalEarnings + " §eрублей");
        } else {
            player.sendMessage("§cУ вас нет урожая для продажи!");
        }
        
        // Обновляем GUI
        plugin.getGuiManager().openSellMenu(player);
    }
    
    // Вспомогательные методы
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
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == cropType) {
                total += item.getAmount();
            }
        }
        return total;
    }
    
    private void removeCropsFromInventory(Player player, Material cropType, int amount) {
        int remaining = amount;
        
        for (int i = 0; i < player.getInventory().getSize() && remaining > 0; i++) {
            ItemStack item = player.getInventory().getItem(i);
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
} 