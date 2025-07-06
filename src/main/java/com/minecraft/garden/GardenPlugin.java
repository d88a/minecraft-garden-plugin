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
        
        // Инициализация менеджеров
        configManager = new ConfigManager(this);
        dataManager = new DataManager(this);
        economyManager = new EconomyManager(this);
        plotManager = new PlotManager(this);
        
        // Регистрация команд
        getCommand("garden").setExecutor(new GardenCommand(this));
        
        // Регистрация слушателей событий
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        getLogger().info("Плагин 'Вырасти сад' успешно загружен!");
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