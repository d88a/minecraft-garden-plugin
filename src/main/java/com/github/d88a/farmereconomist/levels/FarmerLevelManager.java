package com.github.d88a.farmereconomist.levels;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.data.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FarmerLevelManager {

    private final FarmerEconomist plugin;
    private final DataManager dataManager;
    private final Map<UUID, FarmerLevel> playerLevels = new HashMap<>();
    private final Map<Integer, LevelReward> levelRewards = new HashMap<>();

    public FarmerLevelManager(FarmerEconomist plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        setupLevelRewards();
        loadPlayerLevels();
    }

    private void setupLevelRewards() {
        // Награды за уровни
        levelRewards.put(5, new LevelReward(5, 1000, "§aДоступ к расширенным семенам"));
        levelRewards.put(10, new LevelReward(10, 2500, "§6Скидка 10% в магазине"));
        levelRewards.put(15, new LevelReward(15, 5000, "§bДоступ к экзотическим культурам"));
        levelRewards.put(20, new LevelReward(20, 10000, "§dСкидка 20% в магазине"));
        levelRewards.put(25, new LevelReward(25, 15000, "§eДоступ к легендарным культурам"));
        levelRewards.put(30, new LevelReward(30, 25000, "§cСкидка 30% в магазине"));
        levelRewards.put(40, new LevelReward(40, 50000, "§6Золотая лейка (увеличивает урожай)"));
        levelRewards.put(50, new LevelReward(50, 100000, "§dАлмазная лейка (максимальный урожай)"));
    }

    public void addExperience(Player player, int experience) {
        UUID playerId = player.getUniqueId();
        FarmerLevel level = playerLevels.get(playerId);
        
        if (level == null) {
            level = new FarmerLevel(playerId);
            playerLevels.put(playerId, level);
        }
        
        int oldLevel = level.getLevel();
        level.addExperience(experience);
        int newLevel = level.getLevel();
        
        // Проверяем, повысился ли уровень
        if (newLevel > oldLevel) {
            levelUp(player, oldLevel, newLevel);
        }
        
        savePlayerLevels();
    }

    private void levelUp(Player player, int oldLevel, int newLevel) {
        // Отправляем сообщение о повышении уровня
        player.sendMessage("§a§l🎉 ПОЗДРАВЛЯЕМ! Вы достигли уровня " + newLevel + "!");
        player.sendMessage("§7Опыт: " + getCurrentExperience(player) + "/" + getExperienceForNextLevel(player));
        
        // Звуковой эффект
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        
        // Частицы
        player.getWorld().spawnParticle(org.bukkit.Particle.FIREWORK, player.getLocation(), 30);
        
        // Проверяем награды за уровень
        LevelReward reward = levelRewards.get(newLevel);
        if (reward != null) {
            player.sendMessage("§6🏆 НОВАЯ НАГРАДА: " + reward.getDescription());
            player.sendMessage("§6Монеты: +" + reward.getMoneyReward());
            
            // Выдаем награду
            plugin.getEconomyManager().addMoney(player, reward.getMoneyReward());
        }
        
        // Уведомляем всех игроков о достижении
        Bukkit.broadcastMessage("§a🎉 " + player.getName() + " достиг уровня " + newLevel + " в фермерстве!");
    }

    public int getLevel(Player player) {
        FarmerLevel level = playerLevels.get(player.getUniqueId());
        return level != null ? level.getLevel() : 1;
    }

    public int getExperience(Player player) {
        FarmerLevel level = playerLevels.get(player.getUniqueId());
        return level != null ? level.getExperience() : 0;
    }

    public int getCurrentExperience(Player player) {
        FarmerLevel level = playerLevels.get(player.getUniqueId());
        if (level == null) return 0;
        
        int levelExp = getExperienceForLevel(level.getLevel());
        return level.getExperience() - levelExp;
    }

    public int getExperienceForNextLevel(Player player) {
        int currentLevel = getLevel(player);
        int nextLevelExp = getExperienceForLevel(currentLevel + 1);
        int currentLevelExp = getExperienceForLevel(currentLevel);
        return nextLevelExp - currentLevelExp;
    }

    public int getExperienceForLevel(int level) {
        // Формула: 100 * level^1.5
        return (int) (100 * Math.pow(level, 1.5));
    }

    public double getLevelProgress(Player player) {
        int currentExp = getCurrentExperience(player);
        int nextLevelExp = getExperienceForNextLevel(player);
        return (double) currentExp / nextLevelExp * 100.0;
    }

    public String getLevelInfo(Player player) {
        int level = getLevel(player);
        int currentExp = getCurrentExperience(player);
        int nextLevelExp = getExperienceForNextLevel(player);
        double progress = getLevelProgress(player);
        
        return String.format("§aУровень: %d §7(%.1f%%) §eОпыт: %d/%d", 
                           level, progress, currentExp, nextLevelExp);
    }

    public boolean hasLevel(Player player, int requiredLevel) {
        return getLevel(player) >= requiredLevel;
    }

    public LevelReward getLevelReward(int level) {
        return levelRewards.get(level);
    }

    private void loadPlayerLevels() {
        File file = new File(plugin.getDataFolder(), "levels.yml");
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection section = config.getConfigurationSection("players");
            if (section != null) {
                for (String playerId : section.getKeys(false)) {
                    UUID uuid = UUID.fromString(playerId);
                    int level = section.getInt(playerId + ".level", 1);
                    int experience = section.getInt(playerId + ".experience", 0);
                    playerLevels.put(uuid, new FarmerLevel(uuid, level, experience));
                }
            }
        }
    }

    public void saveLevels() {
        savePlayerLevels();
    }

    private void savePlayerLevels() {
        File file = new File(plugin.getDataFolder(), "levels.yml");
        FileConfiguration config = new YamlConfiguration();
        
        for (Map.Entry<UUID, FarmerLevel> entry : playerLevels.entrySet()) {
            String playerId = entry.getKey().toString();
            FarmerLevel level = entry.getValue();
            config.set("players." + playerId + ".level", level.getLevel());
            config.set("players." + playerId + ".experience", level.getExperience());
        }
        
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить уровни: " + e.getMessage());
        }
    }
} 