package com.minecraft.garden.managers;

import com.minecraft.garden.GardenPlugin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CustomItemManager {
    
    private final GardenPlugin plugin;
    private final Map<Material, ItemStack> customSeeds;
    private final Map<Material, ItemStack> customCrops;
    
    public CustomItemManager(GardenPlugin plugin) {
        this.plugin = plugin;
        this.customSeeds = new HashMap<>();
        this.customCrops = new HashMap<>();
        
        plugin.getLogger().info("    Начало инициализации CustomItemManager...");
        try {
            initializeCustomItems();
            plugin.getLogger().info("    ✓ CustomItemManager инициализирован успешно");
        } catch (Exception e) {
            plugin.getLogger().severe("    ✗ ОШИБКА в CustomItemManager: " + e.getMessage());
            throw e;
        }
    }
    
    private void initializeCustomItems() {
        plugin.getLogger().info("      Инициализация кастомных семян...");
        
        // Семена пшеницы
        try {
            customSeeds.put(Material.WHEAT_SEEDS, createSeedItem(
                Material.WHEAT_SEEDS, 
                "🍞 Семена пшеницы (Сад)",
                "§7Золотые семена пшеницы",
                "§7Время роста: §e30 секунд",
                "§7Цена: §e" + plugin.getConfigManager().getSeedPrice("wheat") + " рублей"
            ));
            plugin.getLogger().info("        ✓ Семена пшеницы созданы");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания семян пшеницы: " + e.getMessage());
            throw e;
        }
        
        // Семена моркови
        try {
            customSeeds.put(Material.CARROT, createSeedItem(
                Material.CARROT,
                "🥕 Семена моркови (Сад)",
                "§7Свежие семена моркови",
                "§7Время роста: §e45 секунд",
                "§7Цена: §e" + plugin.getConfigManager().getSeedPrice("carrot") + " рублей"
            ));
            plugin.getLogger().info("        ✓ Семена моркови созданы");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания семян моркови: " + e.getMessage());
            throw e;
        }
        
        // Семена картофеля
        try {
            customSeeds.put(Material.POTATO, createSeedItem(
                Material.POTATO,
                "🥔 Семена картофеля (Сад)",
                "§7Отборные семена картофеля",
                "§7Время роста: §e45 секунд",
                "§7Цена: §e" + plugin.getConfigManager().getSeedPrice("potato") + " рублей"
            ));
            plugin.getLogger().info("        ✓ Семена картофеля созданы");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания семян картофеля: " + e.getMessage());
            throw e;
        }
        
        // Семена свёклы
        try {
            customSeeds.put(Material.BEETROOT_SEEDS, createSeedItem(
                Material.BEETROOT_SEEDS,
                "🔴 Семена свёклы (Сад)",
                "§7Красные семена свёклы",
                "§7Время роста: §e35 секунд",
                "§7Цена: §e" + plugin.getConfigManager().getSeedPrice("beetroot") + " рублей"
            ));
            plugin.getLogger().info("        ✓ Семена свёклы созданы");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания семян свёклы: " + e.getMessage());
            throw e;
        }
        
        // Семена тыквы
        try {
            customSeeds.put(Material.PUMPKIN_SEEDS, createSeedItem(
                Material.PUMPKIN_SEEDS,
                "🎃 Семена тыквы (Сад)",
                "§7Большие семена тыквы",
                "§7Время роста: §e2 минуты",
                "§7Цена: §e" + plugin.getConfigManager().getSeedPrice("pumpkin") + " рублей"
            ));
            plugin.getLogger().info("        ✓ Семена тыквы созданы");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания семян тыквы: " + e.getMessage());
            throw e;
        }
        
        // Семена арбуза
        try {
            customSeeds.put(Material.MELON_SEEDS, createSeedItem(
                Material.MELON_SEEDS,
                "🍉 Семена арбуза (Сад)",
                "§7Сладкие семена арбуза",
                "§7Время роста: §e2 минуты",
                "§7Цена: §e" + plugin.getConfigManager().getSeedPrice("melon") + " рублей"
            ));
            plugin.getLogger().info("        ✓ Семена арбуза созданы");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания семян арбуза: " + e.getMessage());
            throw e;
        }
        
        plugin.getLogger().info("      Инициализация кастомного урожая...");
        
        // Урожай пшеницы
        try {
            customCrops.put(Material.WHEAT, createCropItem(
                Material.WHEAT,
                "🍞 Пшеница (Сад)",
                "§7Золотая пшеница с вашего участка",
                "§7Цена продажи: §e" + plugin.getConfigManager().getCropPrice("wheat") + " рублей"
            ));
            plugin.getLogger().info("        ✓ Урожай пшеницы создан");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания урожая пшеницы: " + e.getMessage());
            throw e;
        }
        
        // Урожай моркови
        try {
            customCrops.put(Material.CARROTS, createCropItem(
                Material.CARROT,  // Используем CARROT вместо CARROTS
                "🥕 Морковь (Сад)",
                "§7Свежая морковь с вашего участка",
                "§7Цена продажи: §e" + plugin.getConfigManager().getCropPrice("carrot") + " рублей"
            ));
            plugin.getLogger().info("        ✓ Урожай моркови создан");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания урожая моркови: " + e.getMessage());
            throw e;
        }
        
        // Урожай картофеля
        try {
            customCrops.put(Material.POTATOES, createCropItem(
                Material.POTATO,  // Используем POTATO вместо POTATOES
                "🥔 Картофель (Сад)",
                "§7Отборный картофель с вашего участка",
                "§7Цена продажи: §e" + plugin.getConfigManager().getCropPrice("potato") + " рублей"
            ));
            plugin.getLogger().info("        ✓ Урожай картофеля создан");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания урожая картофеля: " + e.getMessage());
            throw e;
        }
        
        // Урожай свёклы
        try {
            customCrops.put(Material.BEETROOTS, createCropItem(
                Material.BEETROOT,  // Используем BEETROOT вместо BEETROOTS
                "🔴 Свёкла (Сад)",
                "§7Красная свёкла с вашего участка",
                "§7Цена продажи: §e" + plugin.getConfigManager().getCropPrice("beetroot") + " рублей"
            ));
            plugin.getLogger().info("        ✓ Урожай свёклы создан");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания урожая свёклы: " + e.getMessage());
            throw e;
        }
        
        // Урожай тыквы
        try {
            customCrops.put(Material.PUMPKIN, createCropItem(
                Material.PUMPKIN,
                "🎃 Тыква (Сад)",
                "§7Большая тыква с вашего участка",
                "§7Цена продажи: §e" + plugin.getConfigManager().getCropPrice("pumpkin") + " рублей"
            ));
            plugin.getLogger().info("        ✓ Урожай тыквы создан");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания урожая тыквы: " + e.getMessage());
            throw e;
        }
        
        // Урожай арбуза
        try {
            customCrops.put(Material.MELON, createCropItem(
                Material.MELON,
                "🍉 Арбуз (Сад)",
                "§7Сладкий арбуз с вашего участка",
                "§7Цена продажи: §e" + plugin.getConfigManager().getCropPrice("melon") + " рублей"
            ));
            plugin.getLogger().info("        ✓ Урожай арбуза создан");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания урожая арбуза: " + e.getMessage());
            throw e;
        }
    }
    
    private ItemStack createSeedItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createCropItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Получает кастомное семя по материалу
     */
    public ItemStack getCustomSeed(Material material) {
        return customSeeds.getOrDefault(material, new ItemStack(material));
    }
    
    /**
     * Получает кастомный урожай по материалу
     */
    public ItemStack getCustomCrop(Material material) {
        return customCrops.getOrDefault(material, new ItemStack(material));
    }
    
    /**
     * Проверяет, является ли предмет кастомным семенем
     */
    public boolean isCustomSeed(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return false;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        return displayName.contains("(Сад)") && displayName.contains("Семена");
    }
    
    /**
     * Проверяет, является ли предмет кастомным урожаем
     */
    public boolean isCustomCrop(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return false;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        return displayName.contains("(Сад)") && !displayName.contains("Семена");
    }
    
    /**
     * Получает базовый материал из кастомного предмета
     */
    public Material getBaseMaterial(ItemStack item) {
        return item.getType();
    }
} 