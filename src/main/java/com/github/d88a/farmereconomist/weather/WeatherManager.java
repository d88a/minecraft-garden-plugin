package com.github.d88a.farmereconomist.weather;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WeatherManager {

    private final FarmerEconomist plugin;
    private final Map<WeatherType, Double> weatherMultipliers = new HashMap<>();
    private final Random random = new Random();
    private WeatherType currentWeather = WeatherType.SUNNY;
    private int weatherDuration = 0;
    private final int MAX_WEATHER_DURATION = 20 * 60 * 30; // 30 минут

    public enum WeatherType {
        SUNNY("Солнечно", "SUNNY", "§e", 1.0),
        CLOUDY("Облачно", "CLOUDY", "§7", 0.9),
        RAINY("Дождливо", "RAINY", "§b", 1.3),
        STORMY("Грозово", "STORMY", "§c", 1.5),
        FOGGY("Туманно", "FOGGY", "§8", 0.8),
        WINDY("Ветрено", "WINDY", "§f", 1.1),
        DROUGHT("Засуха", "DROUGHT", "§6", 0.6),
        PERFECT("Идеально", "PERFECT", "§a", 2.0);

        private final String displayName;
        private final String configName;
        private final String color;
        private final double growthMultiplier;

        WeatherType(String displayName, String configName, String color, double growthMultiplier) {
            this.displayName = displayName;
            this.configName = configName;
            this.color = color;
            this.growthMultiplier = growthMultiplier;
        }

        public String getDisplayName() { return displayName; }
        public String getConfigName() { return configName; }
        public String getColor() { return color; }
        public double getGrowthMultiplier() { return growthMultiplier; }
    }

    public WeatherManager(FarmerEconomist plugin) {
        this.plugin = plugin;
        setupWeatherMultipliers();
        startWeatherCycle();
    }

    private void setupWeatherMultipliers() {
        for (WeatherType type : WeatherType.values()) {
            weatherMultipliers.put(type, type.getGrowthMultiplier());
        }
    }

    private void startWeatherCycle() {
        new BukkitRunnable() {
            @Override
            public void run() {
                weatherDuration++;
                
                if (weatherDuration >= MAX_WEATHER_DURATION) {
                    changeWeather();
                    weatherDuration = 0;
                }
                
                // Каждые 5 минут показываем информацию о погоде
                if (weatherDuration % (20 * 60 * 5) == 0) {
                    announceWeather();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Каждую секунду
    }

    private void changeWeather() {
        WeatherType[] types = WeatherType.values();
        WeatherType newWeather;
        
        // 70% шанс обычной погоды, 30% шанс особой погоды
        if (random.nextDouble() < 0.7) {
            WeatherType[] normalWeather = {WeatherType.SUNNY, WeatherType.CLOUDY, WeatherType.RAINY, WeatherType.WINDY};
            newWeather = normalWeather[random.nextInt(normalWeather.length)];
        } else {
            WeatherType[] specialWeather = {WeatherType.STORMY, WeatherType.FOGGY, WeatherType.DROUGHT, WeatherType.PERFECT};
            newWeather = specialWeather[random.nextInt(specialWeather.length)];
        }
        
        if (newWeather != currentWeather) {
            WeatherType oldWeather = currentWeather;
            currentWeather = newWeather;
            announceWeatherChange(oldWeather, newWeather);
        }
    }

    private void announceWeather() {
        String message = currentWeather.getColor() + "🌤 Погода: " + currentWeather.getDisplayName();
        if (currentWeather.getGrowthMultiplier() > 1.0) {
            message += " §a(Растения растут быстрее!)";
        } else if (currentWeather.getGrowthMultiplier() < 1.0) {
            message += " §c(Растения растут медленнее)";
        }
        
        // Отправляем только игрокам, которые находятся на своих участках
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            if (plugin.getPlotManager().hasPlot(player)) {
                player.sendMessage(message);
            }
        }
    }

    private void announceWeatherChange(WeatherType oldWeather, WeatherType newWeather) {
        String message = newWeather.getColor() + "🌤 Погода изменилась: " + 
                        oldWeather.getDisplayName() + " → " + newWeather.getDisplayName();
        
        Bukkit.broadcastMessage(message);
        
        // Специальные эффекты для особой погоды
        if (newWeather == WeatherType.STORMY) {
            Bukkit.broadcastMessage("§c⚡ Гроза может повредить незащищенные растения!");
        } else if (newWeather == WeatherType.DROUGHT) {
            Bukkit.broadcastMessage("§6☀️ Засуха! Растения требуют больше воды!");
        } else if (newWeather == WeatherType.PERFECT) {
            Bukkit.broadcastMessage("§a✨ Идеальная погода! Растения растут в 2 раза быстрее!");
        }
        
        // Звуковые эффекты
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            switch (newWeather) {
                case STORMY:
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 1.0f);
                    break;
                case RAINY:
                    player.playSound(player.getLocation(), org.bukkit.Sound.WEATHER_RAIN, 0.3f, 1.0f);
                    break;
                case PERFECT:
                    player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                    break;
            }
        }
    }

    public WeatherType getCurrentWeather() {
        return currentWeather;
    }

    public double getWeatherMultiplier() {
        return currentWeather.getGrowthMultiplier();
    }

    public String getWeatherInfo() {
        return currentWeather.getColor() + "Погода: " + currentWeather.getDisplayName() + 
               " (x" + String.format("%.1f", currentWeather.getGrowthMultiplier()) + " рост)";
    }

    public int getWeatherDuration() {
        return weatherDuration;
    }

    public void setWeather(WeatherType weather) {
        WeatherType oldWeather = currentWeather;
        currentWeather = weather;
        weatherDuration = 0;
        announceWeatherChange(oldWeather, weather);
    }

    public boolean isSpecialWeather() {
        return currentWeather == WeatherType.STORMY || 
               currentWeather == WeatherType.FOGGY || 
               currentWeather == WeatherType.DROUGHT || 
               currentWeather == WeatherType.PERFECT;
    }

    public String getWeatherEffect() {
        switch (currentWeather) {
            case STORMY:
                return "§c⚡ Гроза может повредить незащищенные растения";
            case DROUGHT:
                return "§6☀️ Засуха! Растения требуют больше воды";
            case PERFECT:
                return "§a✨ Идеальная погода! Растения растут в 2 раза быстрее";
            case FOGGY:
                return "§8🌫 Туман замедляет рост растений";
            case RAINY:
                return "§b🌧 Дождь ускоряет рост растений";
            default:
                return "§7Обычная погода";
        }
    }
} 