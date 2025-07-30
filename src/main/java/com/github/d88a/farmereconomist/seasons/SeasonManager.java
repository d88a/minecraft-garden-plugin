package com.github.d88a.farmereconomist.seasons;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SeasonManager {

    private final FarmerEconomist plugin;
    private Season currentSeason;
    private int seasonDay;
    private final int SEASON_LENGTH = 7; // 7 дней на сезон (можно настроить)
    private final Map<Season, List<String>> seasonalCrops = new HashMap<>();
    private final Map<Season, Double> growthMultipliers = new HashMap<>();

    public enum Season {
        SPRING("Весна", "SPRING", "§a"),
        SUMMER("Лето", "SUMMER", "§6"),
        AUTUMN("Осень", "AUTUMN", "§e"),
        WINTER("Зима", "WINTER", "§b");

        private final String displayName;
        private final String configName;
        private final String color;

        Season(String displayName, String configName, String color) {
            this.displayName = displayName;
            this.configName = configName;
            this.color = color;
        }

        public String getDisplayName() { return displayName; }
        public String getConfigName() { return configName; }
        public String getColor() { return color; }
    }

    public SeasonManager(FarmerEconomist plugin) {
        this.plugin = plugin;
        this.currentSeason = Season.SPRING;
        this.seasonDay = 1;
        setupSeasonalCrops();
        setupGrowthMultipliers();
        startSeasonTimer();
    }

    private void setupSeasonalCrops() {
        // Весенние культуры
        seasonalCrops.put(Season.SPRING, Arrays.asList(
            "TOMATO", "STRAWBERRY", "RADISH", "LUNAR_BERRY", "MANDRAKE_LEAF", "SNOW_MINT"
        ));

        // Летние культуры
        seasonalCrops.put(Season.SUMMER, Arrays.asList(
            "WATERMELON", "SUN_PINEAPPLE", "FLAME_PEPPER", "ELECTRO_PUMPKIN", "SAND_MELON"
        ));

        // Осенние культуры
        seasonalCrops.put(Season.AUTUMN, Arrays.asList(
            "RAINBOW_MUSHROOM", "MYSTIC_ROOT", "STAR_FRUIT", "FOG_BERRY", "WITCH_MUSHROOM"
        ));

        // Зимние культуры
        seasonalCrops.put(Season.WINTER, Arrays.asList(
            "GLOWSHROOM", "CRYSTAL_CACTUS", "PREDATOR_FLOWER", "FLYING_FRUIT"
        ));
    }

    private void setupGrowthMultipliers() {
        growthMultipliers.put(Season.SPRING, 1.2); // +20% скорость роста
        growthMultipliers.put(Season.SUMMER, 1.5); // +50% скорость роста
        growthMultipliers.put(Season.AUTUMN, 1.0); // Обычная скорость
        growthMultipliers.put(Season.WINTER, 0.7); // -30% скорость роста
    }

    private void startSeasonTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                seasonDay++;
                
                if (seasonDay > SEASON_LENGTH) {
                    seasonDay = 1;
                    changeSeason();
                }
                
                // Каждый день показываем информацию о сезоне
                if (seasonDay == 1) {
                    announceSeasonChange();
                }
            }
        }.runTaskTimer(plugin, 20L * 60 * 60 * 24, 20L * 60 * 60 * 24); // Каждые 24 часа
    }

    private void changeSeason() {
        Season[] seasons = Season.values();
        int currentIndex = Arrays.asList(seasons).indexOf(currentSeason);
        int nextIndex = (currentIndex + 1) % seasons.length;
        currentSeason = seasons[nextIndex];
        
        // Сохраняем текущий сезон
        saveSeasonData();
    }

    private void announceSeasonChange() {
        String message = currentSeason.getColor() + "§l🌱 НАСТУПИЛА " + currentSeason.getDisplayName().toUpperCase() + "!";
        Bukkit.broadcastMessage(message);
        Bukkit.broadcastMessage("§7Сезонные культуры растут быстрее!");
        
        // Звуковой эффект для всех игроков
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.5f);
        }
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    public int getSeasonDay() {
        return seasonDay;
    }

    public double getGrowthMultiplier() {
        return growthMultipliers.get(currentSeason);
    }

    public boolean isSeasonalCrop(String cropType) {
        List<String> seasonalCropList = seasonalCrops.get(currentSeason);
        return seasonalCropList != null && seasonalCropList.contains(cropType);
    }

    public List<String> getCurrentSeasonCrops() {
        return seasonalCrops.get(currentSeason);
    }

    public String getSeasonInfo() {
        return currentSeason.getColor() + "Текущий сезон: " + currentSeason.getDisplayName() + 
               " (День " + seasonDay + "/" + SEASON_LENGTH + ")";
    }

    public String getSeasonalCropInfo() {
        List<String> crops = getCurrentSeasonCrops();
        if (crops.isEmpty()) {
            return "§7В этом сезоне нет сезонных культур";
        }
        
        StringBuilder info = new StringBuilder("§aСезонные культуры: ");
        for (int i = 0; i < crops.size(); i++) {
            info.append("§e").append(crops.get(i));
            if (i < crops.size() - 1) {
                info.append("§7, ");
            }
        }
        return info.toString();
    }

    public void setSeason(Season season) {
        this.currentSeason = season;
        this.seasonDay = 1;
        saveSeasonData();
        announceSeasonChange();
    }

    public void setSeasonDay(int day) {
        this.seasonDay = Math.max(1, Math.min(day, SEASON_LENGTH));
        saveSeasonData();
    }

    private void saveSeasonData() {
        plugin.getConfig().set("seasons.current", currentSeason.name());
        plugin.getConfig().set("seasons.day", seasonDay);
        plugin.saveConfig();
    }

    public void loadSeasonData() {
        String seasonName = plugin.getConfig().getString("seasons.current", "SPRING");
        try {
            currentSeason = Season.valueOf(seasonName);
        } catch (IllegalArgumentException e) {
            currentSeason = Season.SPRING;
        }
        
        seasonDay = plugin.getConfig().getInt("seasons.day", 1);
        if (seasonDay < 1 || seasonDay > SEASON_LENGTH) {
            seasonDay = 1;
        }
    }
} 