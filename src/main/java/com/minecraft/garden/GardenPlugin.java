package com.minecraft.garden;

import com.minecraft.garden.commands.GardenCommand;
import com.minecraft.garden.listeners.PlayerListener;
import com.minecraft.garden.managers.ConfigManager;
import com.minecraft.garden.managers.DataManager;
import com.minecraft.garden.managers.EconomyManager;
import com.minecraft.garden.managers.PlotManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GardenPlugin extends JavaPlugin {
    
    private static GardenPlugin instance;
    private ConfigManager configManager;
    private DataManager dataManager;
    private EconomyManager economyManager;
    private PlotManager plotManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        getLogger().info("=== Загрузка плагина 'Вырасти сад' ===");
        
        try {
            // Инициализация менеджеров
            getLogger().info("Инициализация менеджеров...");
            configManager = new ConfigManager(this);
            dataManager = new DataManager(this);
            economyManager = new EconomyManager(this);
            plotManager = new PlotManager(this);
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
            getLogger().info("✓ Слушатели событий зарегистрированы");
            
            getLogger().info("=== Плагин 'Вырасти сад' успешно загружен! ===");
            getLogger().info("Доступные команды:");
            getLogger().info("  /garden - главное меню");
            getLogger().info("  /garden create - создать участок");
            getLogger().info("  /garden tp - телепорт на участок");
            getLogger().info("  /garden help - справка");
            
        } catch (Exception e) {
            getLogger().severe("✗ КРИТИЧЕСКАЯ ОШИБКА при загрузке плагина: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void onDisable() {
        // Сохранение данных при выключении
        if (dataManager != null) {
            dataManager.saveAllData();
        }
        
        getLogger().info("Плагин 'Вырасти сад' выключен!");
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
} 