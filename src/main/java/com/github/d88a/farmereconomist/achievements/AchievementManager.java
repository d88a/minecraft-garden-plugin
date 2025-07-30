package com.github.d88a.farmereconomist.achievements;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.data.DataManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AchievementManager {

    private final FarmerEconomist plugin;
    private final DataManager dataManager;
    private final Map<UUID, Set<String>> playerAchievements = new HashMap<>();
    private final Map<String, Achievement> achievements = new HashMap<>();

    public AchievementManager(FarmerEconomist plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        loadAchievements();
        loadPlayerAchievements();
    }

    private void loadAchievements() {
        // Достижения за посадку растений
        achievements.put("first_plant", new Achievement("first_plant", "Первые шаги", 
            "Посадите свое первое растение", 50, "WHEAT_SEEDS"));
        
        achievements.put("plant_master", new Achievement("plant_master", "Мастер посадки", 
            "Посадите 100 растений", 500, "WHEAT"));
        
        achievements.put("crop_collector", new Achievement("crop_collector", "Коллекционер", 
            "Вырастите все виды базовых культур", 1000, "BOOK"));
        
        achievements.put("exotic_farmer", new Achievement("exotic_farmer", "Экзотический фермер", 
            "Вырастите все виды экзотических культур", 2000, "NETHER_STAR"));
        
        achievements.put("harvest_king", new Achievement("harvest_king", "Король урожая", 
            "Соберите 1000 растений", 1500, "GOLDEN_HOE"));
        
        achievements.put("water_master", new Achievement("water_master", "Мастер полива", 
            "Полейте 500 растений", 300, "WATER_BUCKET"));
        
        achievements.put("fertilizer_expert", new Achievement("fertilizer_expert", "Эксперт по удобрениям", 
            "Удобрите 200 растений", 400, "BONE_MEAL"));
        
        achievements.put("plot_expander", new Achievement("plot_expander", "Расширитель участков", 
            "Расширьте свой участок до максимального размера", 800, "GRASS_BLOCK"));
        
        achievements.put("rich_farmer", new Achievement("rich_farmer", "Богатый фермер", 
            "Накопите 10,000 монет", 1000, "GOLD_INGOT"));
        
        achievements.put("millionaire", new Achievement("millionaire", "Миллионер", 
            "Накопите 100,000 монет", 5000, "DIAMOND"));
        
        achievements.put("seasonal_farmer", new Achievement("seasonal_farmer", "Сезонный фермер", 
            "Вырастите культуры во всех сезонах", 1200, "CLOCK"));
        
        achievements.put("weather_master", new Achievement("weather_master", "Повелитель погоды", 
            "Вырастите культуры при всех типах погоды", 800, "LIGHTNING_ROD"));
        
        achievements.put("speed_farmer", new Achievement("speed_farmer", "Скоростной фермер", 
            "Соберите 50 растений за один день", 600, "CLOCK"));
        
        achievements.put("lucky_farmer", new Achievement("lucky_farmer", "Удачливый фермер", 
            "Получите двойной урожай 10 раз", 400, "LUCK_POTION"));
        
        achievements.put("perfect_farmer", new Achievement("perfect_farmer", "Идеальный фермер", 
            "Вырастите растение с максимальным уходом (полив + удобрение)", 300, "EMERALD"));
        
        achievements.put("social_farmer", new Achievement("social_farmer", "Общительный фермер", 
            "Поделитесь урожаем с другим игроком", 200, "PLAYER_HEAD"));
        
        achievements.put("night_farmer", new Achievement("night_farmer", "Ночной фермер", 
            "Вырастите светящиеся культуры", 350, "GLOWSTONE"));
        
        achievements.put("desert_farmer", new Achievement("desert_farmer", "Пустынный фермер", 
            "Вырастите культуры в пустынных условиях", 450, "SAND"));
        
        achievements.put("mountain_farmer", new Achievement("mountain_farmer", "Горный фермер", 
            "Вырастите культуры на большой высоте", 400, "STONE"));
        
        achievements.put("ocean_farmer", new Achievement("ocean_farmer", "Океанский фермер", 
            "Вырастите культуры рядом с водой", 300, "WATER_BUCKET"));
    }

    public void unlockAchievement(Player player, String achievementId) {
        UUID playerId = player.getUniqueId();
        
        if (!playerAchievements.containsKey(playerId)) {
            playerAchievements.put(playerId, new HashSet<>());
        }
        
        Set<String> playerAchs = playerAchievements.get(playerId);
        
        if (!playerAchs.contains(achievementId)) {
            Achievement achievement = achievements.get(achievementId);
            if (achievement != null) {
                playerAchs.add(achievementId);
                
                // Выдаем награду
                plugin.getEconomyManager().addBalance(player, achievement.getReward());
                
                // Отправляем сообщение
                player.sendMessage("§a§l🏆 ДОСТИЖЕНИЕ РАЗБЛОКИРОВАНО! §a" + achievement.getName());
                player.sendMessage("§7" + achievement.getDescription());
                player.sendMessage("§6Награда: " + achievement.getReward() + " монет");
                
                // Звуковой эффект
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                
                // Частицы
                player.getWorld().spawnParticle(org.bukkit.Particle.FIREWORK, player.getLocation(), 20);
                
                savePlayerAchievements();
            }
        }
    }

    public boolean hasAchievement(Player player, String achievementId) {
        UUID playerId = player.getUniqueId();
        return playerAchievements.containsKey(playerId) && 
               playerAchievements.get(playerId).contains(achievementId);
    }

    public Set<String> getPlayerAchievements(Player player) {
        UUID playerId = player.getUniqueId();
        return playerAchievements.getOrDefault(playerId, new HashSet<>());
    }

    public Map<String, Achievement> getAllAchievements() {
        return new HashMap<>(achievements);
    }

    public int getAchievementProgress(Player player) {
        return getPlayerAchievements(player).size();
    }

    public int getTotalAchievements() {
        return achievements.size();
    }

    public double getCompletionPercentage(Player player) {
        if (achievements.isEmpty()) return 0.0;
        return (double) getAchievementProgress(player) / achievements.size() * 100.0;
    }

    private void loadPlayerAchievements() {
        File file = new File(plugin.getDataFolder(), "achievements.yml");
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection section = config.getConfigurationSection("players");
            if (section != null) {
                for (String playerId : section.getKeys(false)) {
                    UUID uuid = UUID.fromString(playerId);
                    List<String> achievementList = section.getStringList(playerId);
                    playerAchievements.put(uuid, new HashSet<>(achievementList));
                }
            }
        }
    }

    public void saveAchievements() {
        savePlayerAchievements();
    }

    private void savePlayerAchievements() {
        File file = new File(plugin.getDataFolder(), "achievements.yml");
        FileConfiguration config = new YamlConfiguration();
        
        for (Map.Entry<UUID, Set<String>> entry : playerAchievements.entrySet()) {
            config.set("players." + entry.getKey().toString(), new ArrayList<>(entry.getValue()));
        }
        
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить достижения: " + e.getMessage());
        }
    }
} 