package com.minecraft.garden.managers;

import com.minecraft.garden.GardenPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlotManager {
    
    private final GardenPlugin plugin;
    private final Map<UUID, PlotData> playerPlots;
    private int nextPlotId = 0;
    
    public PlotManager(GardenPlugin plugin) {
        this.plugin = plugin;
        this.playerPlots = new HashMap<>();
        loadPlots();
    }
    
    private void loadPlots() {
        // Загрузка участков из данных
        ConfigurationSection plotsSection = plugin.getDataManager().getData().getConfigurationSection("plots");
        if (plotsSection != null) {
            for (String uuidString : plotsSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    ConfigurationSection plotSection = plotsSection.getConfigurationSection(uuidString);
                    if (plotSection != null) {
                        PlotData plotData = new PlotData();
                        plotData.id = plotSection.getInt("id");
                        plotData.x = plotSection.getInt("x");
                        plotData.z = plotSection.getInt("z");
                        plotData.y = plotSection.getInt("y", 64);
                        plotData.size = plotSection.getInt("size", plugin.getConfigManager().getMinPlotSize());
                        playerPlots.put(uuid, plotData);
                        
                        if (plotData.id >= nextPlotId) {
                            nextPlotId = plotData.id + 1;
                        }
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Неверный UUID в данных участка: " + uuidString);
                }
            }
        }
    }
    
    public void savePlots() {
        // Сохранение участков в данные
        ConfigurationSection plotsSection = plugin.getDataManager().getData().createSection("plots");
        for (Map.Entry<UUID, PlotData> entry : playerPlots.entrySet()) {
            ConfigurationSection plotSection = plotsSection.createSection(entry.getKey().toString());
            PlotData plot = entry.getValue();
            plotSection.set("id", plot.id);
            plotSection.set("x", plot.x);
            plotSection.set("z", plot.z);
            plotSection.set("y", plot.y);
            plotSection.set("size", plot.size);
        }
        plugin.getDataManager().saveData();
    }
    
    public boolean hasPlot(UUID playerUuid) {
        return playerPlots.containsKey(playerUuid);
    }
    
    public PlotData getPlot(UUID playerUuid) {
        return playerPlots.get(playerUuid);
    }
    
    public Map<UUID, PlotData> getAllPlots() {
        return new HashMap<>(playerPlots);
    }
    
    public PlotData createPlot(UUID playerUuid) {
        if (hasPlot(playerUuid)) {
            return null; // У игрока уже есть участок
        }
        
        // Вычисляем координаты для размещения участков в сетке
        int plotsPerRow = 5; // 5 участков в ряду
        int plotRow = nextPlotId / plotsPerRow;
        int plotCol = nextPlotId % plotsPerRow;
        
        int spacing = plugin.getConfigManager().getPlotSpacing();
        int plotSize = plugin.getConfigManager().getMinPlotSize();
        
        // Вычисляем координаты участка
        int plotX = plugin.getConfigManager().getStartX() + (plotCol * (plotSize + spacing));
        int plotZ = plugin.getConfigManager().getStartZ() + (plotRow * (plotSize + spacing));
        
        // Находим подходящую высоту для участка
        World world = plugin.getServer().getWorld(plugin.getConfigManager().getWorldName());
        int plotY = findSuitableHeight(world, plotX, plotZ, plotSize);
        
        PlotData plotData = new PlotData();
        plotData.id = nextPlotId++;
        plotData.x = plotX;
        plotData.z = plotZ;
        plotData.y = plotY;
        plotData.size = plotSize;
        
        playerPlots.put(playerUuid, plotData);
        savePlots();
        
        return plotData;
    }
    
    /**
     * Находит подходящую высоту для участка
     */
    private int findSuitableHeight(World world, int x, int z, int size) {
        // Проверяем центральную точку участка
        int centerX = x + size / 2;
        int centerZ = z + size / 2;
        
        // Начинаем с высоты 64 и ищем подходящую поверхность
        for (int y = 64; y < 128; y++) {
            if (isSuitableForPlot(world, centerX, y, centerZ, size)) {
                return y;
            }
        }
        
        // Если не нашли подходящую высоту, возвращаем 64
        return 64;
    }
    
    /**
     * Проверяет, подходит ли место для участка
     */
    private boolean isSuitableForPlot(World world, int x, int y, int z, int size) {
        // Проверяем центральную точку и углы участка
        int[] checkPoints = {
            x, z, // центр
            x - size/2, z - size/2, // левый верхний угол
            x + size/2, z - size/2, // правый верхний угол
            x - size/2, z + size/2, // левый нижний угол
            x + size/2, z + size/2  // правый нижний угол
        };
        
        for (int i = 0; i < checkPoints.length; i += 2) {
            int checkX = checkPoints[i];
            int checkZ = checkPoints[i + 1];
            
            // Проверяем, что блок под поверхностью твёрдый
            if (world.getBlockAt(checkX, y - 1, checkZ).getType().isAir()) {
                return false;
            }
            
            // Проверяем, что поверхность относительно ровная
            if (world.getBlockAt(checkX, y, checkZ).getType().isSolid()) {
                return false;
            }
        }
        
        return true;
    }
    
    public static class PlotData {
        public int id;
        public int x;
        public int z;
        public int y;
        public int size;
        
        public Location getCenterLocation(World world) {
            return new Location(world, x + size/2, y + 1, z + size/2);
        }
        
        public Location getTeleportLocation(World world) {
            // Телепорт на безопасное место рядом с участком
            return new Location(world, x + size/2, y + 2, z + size/2);
        }
        
        public boolean isInPlot(Location location) {
            return location.getBlockX() >= x && location.getBlockX() < x + size &&
                   location.getBlockZ() >= z && location.getBlockZ() < z + size &&
                   location.getBlockY() >= y - 5 && location.getBlockY() <= y + 10; // Проверяем высоту
        }
        
        public String getCoordinates() {
            return String.format("X: %d, Y: %d, Z: %d", x, y, z);
        }
    }
} 