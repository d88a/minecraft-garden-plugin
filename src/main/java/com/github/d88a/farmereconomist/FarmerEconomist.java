package com.github.d88a.farmereconomist;

import com.github.d88a.farmereconomist.commands.BalanceCommand;
import com.github.d88a.farmereconomist.commands.AdminCommand;
import com.github.d88a.farmereconomist.commands.EcoCommand;
import com.github.d88a.farmereconomist.commands.FermerCommand;
import com.github.d88a.farmereconomist.commands.OgorodCommand;
import com.github.d88a.farmereconomist.commands.StatsCommand;
import com.github.d88a.farmereconomist.commands.EventCommand;
import com.github.d88a.farmereconomist.config.ConfigManager;
import com.github.d88a.farmereconomist.crops.CropManager;
import com.github.d88a.farmereconomist.data.DataManager;
import com.github.d88a.farmereconomist.items.ItemManager;
import com.github.d88a.farmereconomist.economy.EconomyManager;
import com.github.d88a.farmereconomist.listeners.CropListener;
import com.github.d88a.farmereconomist.listeners.OgorodListener;
import com.github.d88a.farmereconomist.listeners.PlotProtectionListener;
import com.github.d88a.farmereconomist.listeners.ShopListener;
import com.github.d88a.farmereconomist.listeners.FarmingListener;
import com.github.d88a.farmereconomist.npc.NpcManager; // Already correct, but ensuring it's the new one
import com.github.d88a.farmereconomist.npc.NpcAnimator;
import com.github.d88a.farmereconomist.plots.OgorodGUI;
import com.github.d88a.farmereconomist.plots.PlotManager;
import com.github.d88a.farmereconomist.sound.SoundManager;
import com.github.d88a.farmereconomist.achievements.AchievementManager;
import com.github.d88a.farmereconomist.seasons.SeasonManager;
import com.github.d88a.farmereconomist.levels.FarmerLevelManager;
import com.github.d88a.farmereconomist.weather.WeatherManager;
import com.github.d88a.farmereconomist.events.FarmingEventManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class FarmerEconomist extends JavaPlugin {

    private DataManager dataManager;
    private EconomyManager economyManager;
    private PlotManager plotManager;
    private ConfigManager configManager;
    private NpcManager npcManager;
    private OgorodGUI ogorodGUI;
    private CropManager cropManager;
    private SoundManager soundManager;
    private AchievementManager achievementManager;
    private SeasonManager seasonManager;
    private FarmerLevelManager farmerLevelManager;
    private WeatherManager weatherManager;
    private FarmingEventManager farmingEventManager;

    @Override
    public void onEnable() {
        // Initialize managers
        ItemManager.init(this);
        this.configManager = new ConfigManager(this);
        this.dataManager = new DataManager(this);
        this.economyManager = new EconomyManager(dataManager);
        this.plotManager = new PlotManager(this, dataManager);
        this.npcManager = new NpcManager(this);
        this.cropManager = new CropManager(this, dataManager);
        this.soundManager = new SoundManager(this);
        this.ogorodGUI = new OgorodGUI(plotManager);
        
        // Новые менеджеры
        this.achievementManager = new AchievementManager(this, dataManager);
        this.seasonManager = new SeasonManager(this);
        this.farmerLevelManager = new FarmerLevelManager(this, dataManager);
        this.weatherManager = new WeatherManager(this);
        this.farmingEventManager = new FarmingEventManager(this);

        // Запуск анимации головы NPC
        new NpcAnimator(this, npcManager).start();

        // Register commands
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("eco").setExecutor(new EcoCommand(this));
        getCommand("ogorod").setExecutor(new OgorodCommand(this));
        getCommand("fermer").setExecutor(new FermerCommand(this));
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("event").setExecutor(new EventCommand(this));
        getCommand("feadmin").setExecutor(new AdminCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlotProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new ShopListener(this), this);
        getServer().getPluginManager().registerEvents(new OgorodListener(this), this);
        getServer().getPluginManager().registerEvents(new CropListener(this), this);
        getServer().getPluginManager().registerEvents(new FarmingListener(this), this);

        getLogger().info("FarmerEconomist has been enabled!");
        
    }

    @Override
    public void onDisable() {
        // Save all plots to file
        plotManager.savePlots();
        cropManager.saveCrops();
        achievementManager.saveAchievements();
        farmerLevelManager.saveLevels();

        getLogger().info("FarmerEconomist has been disabled!");
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

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public NpcManager getNpcManager() {
        return npcManager;
    }

    public OgorodGUI getOgorodGUI() {
        return ogorodGUI;
    }

    public CropManager getCropManager() {
        return cropManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public AchievementManager getAchievementManager() {
        return achievementManager;
    }

    public SeasonManager getSeasonManager() {
        return seasonManager;
    }

    public FarmerLevelManager getFarmerLevelManager() {
        return farmerLevelManager;
    }

    public WeatherManager getWeatherManager() {
        return weatherManager;
    }

    public FarmingEventManager getFarmingEventManager() {
        return farmingEventManager;
    }
} 