package com.minecraft.garden.managers;

import com.minecraft.garden.GardenPlugin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CustomPlantManager {
    
    private final GardenPlugin plugin;
    private final Map<String, CustomPlant> customPlants;
    
    public CustomPlantManager(GardenPlugin plugin) {
        this.plugin = plugin;
        this.customPlants = new HashMap<>();
        
        plugin.getLogger().info("    Начало инициализации CustomPlantManager...");
        try {
            initializeCustomPlants();
            plugin.getLogger().info("    ✓ CustomPlantManager инициализирован успешно");
        } catch (Exception e) {
            plugin.getLogger().severe("    ✗ ОШИБКА в CustomPlantManager: " + e.getMessage());
            throw e;
        }
    }
    
    private void initializeCustomPlants() {
        plugin.getLogger().info("      Инициализация кастомных растений...");
        
        // Золотое дерево
        try {
            customPlants.put("golden_tree", new CustomPlant(
                "golden_tree",
                "🌳 Золотое дерево",
                Material.GOLD_INGOT,
                Material.GOLDEN_APPLE,
                "Золотые семена дерева",
                "§7Семена мистического золотого дерева",
                "§7Время роста: §e5 минут",
                "§7Цена: §e50 рублей",
                "§7Урожай: §eЗолотые яблоки (3x)",
                "§7Особенность: §eДает золотые яблоки",
                300, // 5 минут
                50,  // цена семени
                150  // цена урожая
            ));
            plugin.getLogger().info("        ✓ Золотое дерево создано");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания золотого дерева: " + e.getMessage());
            throw e;
        }
        
        // Кристальная роза
        try {
            customPlants.put("crystal_rose", new CustomPlant(
                "crystal_rose",
                "💎 Кристальная роза",
                Material.AMETHYST_SHARD,
                Material.AMETHYST_SHARD,
                "Кристальные семена розы",
                "§7Семена сияющей кристальной розы",
                "§7Время роста: §e3 минуты",
                "§7Цена: §e35 рублей",
                "§7Урожай: §eКристальные лепестки (5x)",
                "§7Особенность: §eСветится в темноте",
                180, // 3 минуты
                35,  // цена семени
                100  // цена урожая
            ));
            plugin.getLogger().info("        ✓ Кристальная роза создана");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания кристальной розы: " + e.getMessage());
            throw e;
        }
        
        // Огненная тыква
        try {
            customPlants.put("fire_pumpkin", new CustomPlant(
                "fire_pumpkin",
                "🔥 Огненная тыква",
                Material.BLAZE_POWDER,
                Material.PUMPKIN,
                "Огненные семена тыквы",
                "§7Семена пылающей тыквы",
                "§7Время роста: §e4 минуты",
                "§7Цена: §e40 рублей",
                "§7Урожай: §eОгненные тыквы (2x)",
                "§7Особенность: §eПоджигает врагов",
                240, // 4 минуты
                40,  // цена семени
                120  // цена урожая
            ));
            plugin.getLogger().info("        ✓ Огненная тыква создана");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания огненной тыквы: " + e.getMessage());
            throw e;
        }
        
        // Ледяная ягода
        try {
            customPlants.put("ice_berry", new CustomPlant(
                "ice_berry",
                "❄️ Ледяная ягода",
                Material.ICE,
                Material.SWEET_BERRIES,
                "Ледяные семена ягоды",
                "§7Семена замороженной ягоды",
                "§7Время роста: §e2 минуты",
                "§7Цена: §e25 рублей",
                "§7Урожай: §eЛедяные ягоды (4x)",
                "§7Особенность: §eЗамораживает врагов",
                120, // 2 минуты
                25,  // цена семени
                80   // цена урожая
            ));
            plugin.getLogger().info("        ✓ Ледяная ягода создана");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания ледяной ягоды: " + e.getMessage());
            throw e;
        }
        
        // Электрическая пшеница
        try {
            customPlants.put("electric_wheat", new CustomPlant(
                "electric_wheat",
                "⚡ Электрическая пшеница",
                Material.REDSTONE,
                Material.WHEAT,
                "Электрические семена пшеницы",
                "§7Семена заряженной пшеницы",
                "§7Время роста: §e90 секунд",
                "§7Цена: §e30 рублей",
                "§7Урожай: §eЭлектрическая пшеница (6x)",
                "§7Особенность: §eУдаряет током",
                90,  // 90 секунд
                30,  // цена семени
                90   // цена урожая
            ));
            plugin.getLogger().info("        ✓ Электрическая пшеница создана");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания электрической пшеницы: " + e.getMessage());
            throw e;
        }
        
        // Радужный цветок
        try {
            customPlants.put("rainbow_flower", new CustomPlant(
                "rainbow_flower",
                "🌈 Радужный цветок",
                Material.ORANGE_DYE,
                Material.ORANGE_TULIP,
                "Радужные семена цветка",
                "§7Семена разноцветного цветка",
                "§7Время роста: §e6 минут",
                "§7Цена: §e60 рублей",
                "§7Урожай: §eРадужные лепестки (8x)",
                "§7Особенность: §eМеняет цвет",
                360, // 6 минут
                60,  // цена семени
                200  // цена урожая
            ));
            plugin.getLogger().info("        ✓ Радужный цветок создан");
        } catch (Exception e) {
            plugin.getLogger().severe("        ✗ Ошибка создания радужного цветка: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Получает кастомное растение по ID
     */
    public CustomPlant getCustomPlant(String plantId) {
        return customPlants.get(plantId);
    }
    
    /**
     * Получает все кастомные растения
     */
    public Map<String, CustomPlant> getAllCustomPlants() {
        return customPlants;
    }
    
    /**
     * Создает семя кастомного растения
     */
    public ItemStack createCustomSeed(String plantId) {
        CustomPlant plant = getCustomPlant(plantId);
        if (plant == null) {
            return null;
        }
        
        ItemStack item = new ItemStack(plant.seedMaterial);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(plant.seedName);
        meta.setLore(Arrays.asList(plant.seedDescription, plant.growthTime, plant.seedPrice));
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Создает урожай кастомного растения
     */
    public ItemStack createCustomCrop(String plantId) {
        CustomPlant plant = getCustomPlant(plantId);
        if (plant == null) {
            return null;
        }
        
        ItemStack item = new ItemStack(plant.cropMaterial);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(plant.displayName + " (Сад)");
        meta.setLore(Arrays.asList(plant.cropDescription, plant.cropPrice));
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Проверяет, является ли предмет семенем кастомного растения
     */
    public boolean isCustomPlantSeed(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return false;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        for (CustomPlant plant : customPlants.values()) {
            if (displayName.equals(plant.seedName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Получает ID растения по семени
     */
    public String getPlantIdFromSeed(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return null;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        for (Map.Entry<String, CustomPlant> entry : customPlants.entrySet()) {
            if (displayName.equals(entry.getValue().seedName)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    /**
     * Класс для хранения данных о кастомном растении
     */
    public static class CustomPlant {
        public final String id;
        public final String displayName;
        public final Material seedMaterial;
        public final Material cropMaterial;
        public final String seedName;
        public final String seedDescription;
        public final String growthTime;
        public final String seedPrice;
        public final String cropDescription;
        public final String cropPrice;
        public final int growthTimeSeconds;
        public final int seedPriceValue;
        public final int cropPriceValue;
        
        public CustomPlant(String id, String displayName, Material seedMaterial, Material cropMaterial,
                          String seedName, String seedDescription, String growthTime, String seedPrice,
                          String cropDescription, String cropPrice, int growthTimeSeconds, 
                          int seedPriceValue, int cropPriceValue) {
            this.id = id;
            this.displayName = displayName;
            this.seedMaterial = seedMaterial;
            this.cropMaterial = cropMaterial;
            this.seedName = seedName;
            this.seedDescription = seedDescription;
            this.growthTime = growthTime;
            this.seedPrice = seedPrice;
            this.cropDescription = cropDescription;
            this.cropPrice = cropPrice;
            this.growthTimeSeconds = growthTimeSeconds;
            this.seedPriceValue = seedPriceValue;
            this.cropPriceValue = cropPriceValue;
        }
    }
} 