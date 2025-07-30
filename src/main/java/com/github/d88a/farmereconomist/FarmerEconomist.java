package com.github.d88a.farmereconomist;

import com.github.d88a.farmereconomist.commands.BalanceCommand;
import com.github.d88a.farmereconomist.commands.EcoCommand;
import com.github.d88a.farmereconomist.commands.FermerCommand;
import com.github.d88a.farmereconomist.commands.OgorodCommand;
import com.github.d88a.farmereconomist.config.ConfigManager;
import com.github.d88a.farmereconomist.crops.CropManager;
import com.github.d88a.farmereconomist.data.DataManager;
import com.github.d88a.farmereconomist.economy.EconomyManager;
import com.github.d88a.farmereconomist.listeners.CropListener;
import com.github.d88a.farmereconomist.listeners.OgorodListener;
import com.github.d88a.farmereconomist.listeners.PlotProtectionListener;
import com.github.d88a.farmereconomist.listeners.ShopListener;
import com.github.d88a.farmereconomist.npc.NpcManager;
import com.github.d88a.farmereconomist.plots.OgorodGUI;
import com.github.d88a.farmereconomist.plots.PlotManager;
import com.github.d88a.farmereconomist.sound.SoundManager;
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

    @Override
    public void onEnable() {
        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.dataManager = new DataManager(this);
        this.economyManager = new EconomyManager(dataManager);
        this.plotManager = new PlotManager(this, dataManager);
        this.npcManager = new NpcManager(this);
        this.cropManager = new CropManager(this, dataManager);
        this.soundManager = new SoundManager(this);
        this.ogorodGUI = new OgorodGUI(plotManager);


        // Register commands
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("eco").setExecutor(new EcoCommand(this));
        getCommand("ogorod").setExecutor(new OgorodCommand(this));
        getCommand("fermer").setExecutor(new FermerCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlotProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new ShopListener(this), this);
        getServer().getPluginManager().registerEvents(new OgorodListener(this), this);
        getServer().getPluginManager().registerEvents(new CropListener(this), this);

        getLogger().info("FarmerEconomist has been enabled!");
        
    }

    @Override
    public void onDisable() {
        // Save all plots to file
        plotManager.savePlots();
        cropManager.saveCrops();

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
} 