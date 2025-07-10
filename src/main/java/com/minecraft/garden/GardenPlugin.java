package com.minecraft.garden;

import com.minecraft.garden.commands.GardenCommand;
import com.minecraft.garden.listeners.PlayerListener;
import com.minecraft.garden.listeners.GuiListener;
import com.minecraft.garden.managers.ConfigManager;
import com.minecraft.garden.managers.DataManager;
import com.minecraft.garden.managers.EconomyManager;
import com.minecraft.garden.managers.PlotManager;
import com.minecraft.garden.managers.PlantManager;
import com.minecraft.garden.managers.GuiManager;
import com.minecraft.garden.managers.CustomItemManager;
import com.minecraft.garden.managers.CustomPlantManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GardenPlugin extends JavaPlugin {
    
    private static GardenPlugin instance;
    private ConfigManager configManager;
    private DataManager dataManager;
    private EconomyManager economyManager;
    private PlotManager plotManager;
    private PlantManager plantManager;
    private GuiManager guiManager;
    private CustomItemManager customItemManager;
    private CustomPlantManager customPlantManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        getLogger().info("=== Загрузка плагина 'Вырасти сад' ===");
        
        try {
            // Инициализация менеджеров
            getLogger().info("Инициализация менеджеров...");
            
            getLogger().info("  - Инициализация ConfigManager...");
            configManager = new ConfigManager(this);
            getLogger().info("  ✓ ConfigManager инициализирован");
            
            getLogger().info("  - Инициализация DataManager...");
            dataManager = new DataManager(this);
            getLogger().info("  ✓ DataManager инициализирован");
            
            getLogger().info("  - Инициализация EconomyManager...");
            economyManager = new EconomyManager(this);
            getLogger().info("  ✓ EconomyManager инициализирован");
            
            getLogger().info("  - Инициализация PlotManager...");
            plotManager = new PlotManager(this);
            getLogger().info("  ✓ PlotManager инициализирован");
            
            getLogger().info("  - Инициализация PlantManager...");
            plantManager = new PlantManager(this);
            getLogger().info("  ✓ PlantManager инициализирован");
            
            getLogger().info("  - Инициализация GuiManager...");
            guiManager = new GuiManager(this);
            getLogger().info("  ✓ GuiManager инициализирован");
            
            getLogger().info("  - Инициализация CustomItemManager...");
            customItemManager = new CustomItemManager(this);
            getLogger().info("  ✓ CustomItemManager инициализирован");
            
            getLogger().info("  - Инициализация CustomPlantManager...");
            customPlantManager = new CustomPlantManager(this);
            getLogger().info("  ✓ CustomPlantManager инициализирован");
            
            getLogger().info("✓ Менеджеры инициализированы");
            
            // Регистрация команд
            getLogger().info("Регистрация команд...");
            if (getCommand("garden") != null) {
                getCommand("garden").setExecutor(new GardenCommand(this));
                getLogger().info("✓ Команда /garden зарегистрирована");
                getLogger().info("✓ Алиас /g зарегистрирован");
            } else {
                getLogger().severe("✗ ОШИБКА: Команда 'garden' не найдена в plugin.yml!");
            }
            
            // Регистрация слушателей событий
            getLogger().info("Регистрация слушателей событий...");
            getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
            getServer().getPluginManager().registerEvents(new GuiListener(this), this);
            getLogger().info("✓ Слушатели событий зарегистрированы");
            
            getLogger().info("=== Плагин 'Вырасти сад' успешно загружен! ===");
            getLogger().info("Доступные команды:");
            getLogger().info("  /garden - главное меню");
            getLogger().info("  /garden create - создать участок");
            getLogger().info("  /garden tp - телепорт на участок");
            getLogger().info("  /garden help - справка");
            
        } catch (Exception e) {
            getLogger().severe("✗ КРИТИЧЕСКАЯ ОШИБКА при загрузке плагина: " + e.getMessage());
            getLogger().severe("✗ Стек вызовов:");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        getLogger().info("=== Выгрузка плагина 'Вырасти сад' ===");
        
        // Сохраняем данные
        if (dataManager != null) {
            dataManager.saveData();
            getLogger().info("✓ Данные сохранены");
        }
        
        if (plotManager != null) {
            plotManager.savePlots();
            getLogger().info("✓ Участки сохранены");
        }
        
        if (plantManager != null) {
            plantManager.savePlantedCrops();
            getLogger().info("✓ Растения сохранены");
        }
        
        if (economyManager != null) {
            economyManager.saveBalances();
            getLogger().info("✓ Балансы сохранены");
        }
        
        getLogger().info("=== Плагин 'Вырасти сад' выгружен! ===");
    }
    
    // Геттеры для менеджеров
    public static GardenPlugin getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
    
    public EconomyManager getEconomyManager() {
        return economyManager;
    }
    
    public PlotManager getPlotManager() {
        return plotManager;
    }
    
    public PlantManager getPlantManager() {
        return plantManager;
    }
    
    public GuiManager getGuiManager() {
        return guiManager;
    }
    
    public CustomItemManager getCustomItemManager() {
        return customItemManager;
    }

    public CustomPlantManager getCustomPlantManager() {
        return customPlantManager;
    }
} 