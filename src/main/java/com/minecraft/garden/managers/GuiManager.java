package com.minecraft.garden.managers;

import com.minecraft.garden.GardenPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GuiManager {
    
    private final GardenPlugin plugin;
    private final Map<Player, GuiType> openGuis;
    
    public enum GuiType {
        MAIN_MENU,
        SHOP,
        SELL_MENU,
        EXPAND_MENU
    }
    
    public GuiManager(GardenPlugin plugin) {
        this.plugin = plugin;
        this.openGuis = new HashMap<>();
    }
    
    /**
     * Открывает главное меню
     */
    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§6=== Меню сада ===");
        
        // Информация об участке
        if (plugin.getPlotManager().hasPlot(player.getUniqueId())) {
            inv.setItem(10, createItem(Material.GRASS_BLOCK, "§aИнформация об участке", 
                "§7Нажмите, чтобы посмотреть", "§7информацию о вашем участке"));
            
            inv.setItem(11, createItem(Material.ENDER_PEARL, "§bТелепорт на участок", 
                "§7Нажмите, чтобы телепортироваться", "§7на свой участок"));
            
            inv.setItem(12, createItem(Material.BARRIER, "§cУдалить участок", 
                "§7Нажмите, чтобы удалить", "§7свой участок"));
        } else {
            inv.setItem(10, createItem(Material.GRASS_BLOCK, "§aПолучить участок", 
                "§7Нажмите, чтобы получить", "§7свой участок"));
        }
        
        // Магазин
        inv.setItem(13, createItem(Material.WHEAT_SEEDS, "§eМагазин семян", 
            "§7Нажмите, чтобы открыть", "§7магазин семян"));
        
        // Продажа
        inv.setItem(14, createItem(Material.GOLD_INGOT, "§6Продажа урожая", 
            "§7Нажмите, чтобы продать", "§7свой урожай"));
        
        // Расширение
        inv.setItem(15, createItem(Material.IRON_AXE, "§dРасширить участок", 
            "§7Нажмите, чтобы расширить", "§7свой участок"));
        
        // Справка
        inv.setItem(16, createItem(Material.BOOK, "§fСправка", 
            "§7Нажмите, чтобы посмотреть", "§7справку по командам"));
        
        // Баланс
        int balance = plugin.getEconomyManager().getBalance(player);
        inv.setItem(22, createItem(Material.EMERALD, "§aВаш баланс: §e" + balance + " §aрублей", 
            "§7Нажмите, чтобы обновить"));
        
        player.openInventory(inv);
        openGuis.put(player, GuiType.MAIN_MENU);
    }
    
    /**
     * Открывает магазин семян
     */
    public void openShop(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, "§6=== Магазин семян ===");
        
        // Семена - используем кастомные предметы
        inv.setItem(10, plugin.getCustomItemManager().getCustomSeed(Material.WHEAT_SEEDS));
        inv.setItem(11, plugin.getCustomItemManager().getCustomSeed(Material.CARROT));
        inv.setItem(12, plugin.getCustomItemManager().getCustomSeed(Material.POTATO));
        inv.setItem(13, plugin.getCustomItemManager().getCustomSeed(Material.BEETROOT_SEEDS));
        inv.setItem(14, plugin.getCustomItemManager().getCustomSeed(Material.PUMPKIN_SEEDS));
        inv.setItem(15, plugin.getCustomItemManager().getCustomSeed(Material.MELON_SEEDS));
        
        // Кнопка "Назад"
        inv.setItem(31, createItem(Material.ARROW, "§cНазад", 
            "§7Нажмите, чтобы вернуться", "§7в главное меню"));
        
        // Баланс
        int balance = plugin.getEconomyManager().getBalance(player);
        inv.setItem(32, createItem(Material.EMERALD, "§aБаланс: §e" + balance + " §aрублей"));
        
        player.openInventory(inv);
        openGuis.put(player, GuiType.SHOP);
    }
    
    /**
     * Открывает меню продажи
     */
    public void openSellMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, "§6=== Продажа урожая ===");
        
        // Урожай - используем кастомные предметы
        inv.setItem(10, plugin.getCustomItemManager().getCustomCrop(Material.WHEAT));
        inv.setItem(11, plugin.getCustomItemManager().getCustomCrop(Material.CARROTS));
        inv.setItem(12, plugin.getCustomItemManager().getCustomCrop(Material.POTATOES));
        inv.setItem(13, plugin.getCustomItemManager().getCustomCrop(Material.BEETROOTS));
        inv.setItem(14, plugin.getCustomItemManager().getCustomCrop(Material.PUMPKIN));
        inv.setItem(15, plugin.getCustomItemManager().getCustomCrop(Material.MELON));
        
        // Продать все
        inv.setItem(21, createItem(Material.HOPPER, "§aПродать весь урожай", 
            "§7Нажмите, чтобы продать", "§7весь урожай из инвентаря"));
        
        // Кнопка "Назад"
        inv.setItem(31, createItem(Material.ARROW, "§cНазад", 
            "§7Нажмите, чтобы вернуться", "§7в главное меню"));
        
        // Баланс
        int balance = plugin.getEconomyManager().getBalance(player);
        inv.setItem(32, createItem(Material.EMERALD, "§aБаланс: §e" + balance + " §aрублей"));
        
        player.openInventory(inv);
        openGuis.put(player, GuiType.SELL_MENU);
    }
    
    /**
     * Открывает меню расширения участка
     */
    public void openExpandMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§6=== Расширение участка ===");
        
        // Информация о расширении
        inv.setItem(13, createItem(Material.IRON_AXE, "§dРасширение участка", 
            "§7Функция расширения", "§7пока не реализована", 
            "§7Следите за обновлениями!"));
        
        // Кнопка "Назад"
        inv.setItem(22, createItem(Material.ARROW, "§cНазад", 
            "§7Нажмите, чтобы вернуться", "§7в главное меню"));
        
        player.openInventory(inv);
        openGuis.put(player, GuiType.EXPAND_MENU);
    }
    
    /**
     * Создает предмет для интерфейса
     */
    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Создает предмет для магазина
     */
    private ItemStack createShopItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Создает предмет для продажи
     */
    private ItemStack createSellItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Получает тип открытого GUI для игрока
     */
    public GuiType getOpenGui(Player player) {
        return openGuis.get(player);
    }
    
    /**
     * Удаляет запись об открытом GUI
     */
    public void closeGui(Player player) {
        openGuis.remove(player);
    }
    
    /**
     * Проверяет, открыт ли GUI у игрока
     */
    public boolean hasOpenGui(Player player) {
        return openGuis.containsKey(player);
    }
} 