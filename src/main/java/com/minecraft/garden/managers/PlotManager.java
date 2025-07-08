package com.minecraft.garden.managers;

import com.minecraft.garden.GardenPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

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
                        plotData.y = plotSection.getInt("y", 62);
                        plotData.size = plotSection.getInt("size", 15);
                        plotData.maxSize = plotSection.getInt("maxSize", 60);
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
            plotSection.set("maxSize", plot.maxSize);
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
        
        // Заданная область из конфигурации
        int areaStartX = plugin.getConfigManager().getAreaStartX();
        int areaEndX = plugin.getConfigManager().getAreaEndX();
        int areaStartZ = plugin.getConfigManager().getAreaStartZ();
        int areaEndZ = plugin.getConfigManager().getAreaEndZ();
        int areaHeight = plugin.getConfigManager().getAreaHeight();
        
        // Параметры участков из конфигурации
        int initialSize = plugin.getConfigManager().getInitialPlotSize();
        int maxSize = plugin.getConfigManager().getMaxPlotSize();
        int pathWidth = plugin.getConfigManager().getPathWidth();
        int fenceSpacing = plugin.getConfigManager().getFenceSpacing();
        
        // Вычисляем, сколько участков поместится в области
        int totalWidth = areaEndX - areaStartX;
        int totalDepth = areaEndZ - areaStartZ;
        
        // Учитываем заборы и тропинки
        int plotWithFence = maxSize + (fenceSpacing * 2);
        int plotWithPath = plotWithFence + pathWidth;
        
        int plotsPerRow = totalWidth / plotWithPath;
        int maxRows = totalDepth / plotWithPath;
        int maxPlots = plotsPerRow * maxRows;
        
        // Находим свободный ID для нового участка
        int newPlotId = findNextAvailablePlotId(maxPlots);
        
        // Находим позицию для нового участка
        int plotRow = newPlotId / plotsPerRow;
        int plotCol = newPlotId % plotsPerRow;
        
        // Проверяем, есть ли место
        if (plotRow >= maxRows) {
            plugin.getLogger().warning("Нет места для нового участка! Все места заняты.");
            plugin.getLogger().info("Текущий ID: " + newPlotId + ", Максимум участков: " + maxPlots);
            return null;
        }
        
        // Вычисляем координаты участка
        int plotX = areaStartX + (plotCol * plotWithPath) + fenceSpacing;
        int plotZ = areaStartZ + (plotRow * plotWithPath) + fenceSpacing;
        int plotY = areaHeight;
        
        PlotData plotData = new PlotData();
        plotData.id = newPlotId;
        plotData.x = plotX;
        plotData.z = plotZ;
        plotData.y = plotY;
        plotData.size = initialSize;
        plotData.maxSize = maxSize;
        
        playerPlots.put(playerUuid, plotData);
        savePlots();
        
        // Создаем забор и табличку
        createPlotFence(plotData, playerUuid);
        
        // Выдаем начальный баланс игроку
        Player player = plugin.getServer().getPlayer(playerUuid);
        if (player != null) {
            plugin.getEconomyManager().givePlotStartingBalance(player);
        }
        
        return plotData;
    }
    
    /**
     * Находит следующий доступный ID участка
     */
    private int findNextAvailablePlotId(int maxPlots) {
        // Создаем массив занятых ID
        boolean[] usedIds = new boolean[maxPlots];
        
        // Отмечаем занятые ID
        for (PlotData plot : playerPlots.values()) {
            if (plot.id < maxPlots) {
                usedIds[plot.id] = true;
            }
        }
        
        // Ищем первый свободный ID
        for (int i = 0; i < maxPlots; i++) {
            if (!usedIds[i]) {
                return i;
            }
        }
        
        // Если все заняты, возвращаем следующий после максимального
        return nextPlotId;
    }
    
    /**
     * Создает забор вокруг участка и табличку с именем игрока
     */
    private void createPlotFence(PlotData plot, UUID playerUuid) {
        World world = plugin.getServer().getWorld(plugin.getConfigManager().getWorldName());
        if (world == null) return;
        
        String playerName = plugin.getServer().getOfflinePlayer(playerUuid).getName();
        if (playerName == null) playerName = "Неизвестный";
        
        // Создаем забор вокруг текущего размера участка (не максимального)
        int fenceX = plot.x - 1;
        int fenceZ = plot.z - 1;
        int fenceSize = plot.size + 2; // +2 для забора вокруг текущего размера
        int fenceY = plot.y + 1; // Поднимаем забор на один блок выше
        
        // Создаем периметр забора
        for (int i = 0; i < fenceSize; i++) {
            // Нижняя сторона
            world.getBlockAt(fenceX + i, fenceY, fenceZ).setType(org.bukkit.Material.OAK_FENCE);
            // Верхняя сторона
            world.getBlockAt(fenceX + i, fenceY, fenceZ + fenceSize - 1).setType(org.bukkit.Material.OAK_FENCE);
            // Левая сторона
            world.getBlockAt(fenceX, fenceY, fenceZ + i).setType(org.bukkit.Material.OAK_FENCE);
            // Правая сторона
            world.getBlockAt(fenceX + fenceSize - 1, fenceY, fenceZ + i).setType(org.bukkit.Material.OAK_FENCE);
        }
        
        // Создаем табличку с именем игрока (в центре передней стороны)
        int signX = fenceX + (fenceSize / 2);
        int signZ = fenceZ - 1;
        int signY = fenceY; // Табличка на той же высоте, что и забор
        org.bukkit.block.Block signBlock = world.getBlockAt(signX, signY, signZ);
        signBlock.setType(org.bukkit.Material.OAK_SIGN);
        
        if (signBlock.getState() instanceof org.bukkit.block.Sign) {
            org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signBlock.getState();
            sign.setLine(0, "§6Участок игрока:");
            sign.setLine(1, "§e" + playerName);
            sign.setLine(2, "§7ID: " + plot.id);
            sign.setLine(3, "§7Размер: " + plot.size + "x" + plot.size);
            sign.update();
        }
        
        plugin.getLogger().info("Создан забор для участка " + plot.id + " игрока " + playerName + " размером " + plot.size + "x" + plot.size);
    }
    
    /**
     * Расширяет участок игрока
     */
    public boolean expandPlot(UUID playerUuid, int newSize) {
        if (!hasPlot(playerUuid)) {
            return false; // У игрока нет участка
        }
        
        PlotData plot = playerPlots.get(playerUuid);
        
        // Проверяем, что новый размер не превышает максимальный
        if (newSize > plot.maxSize) {
            return false;
        }
        
        // Проверяем, что новый размер больше текущего
        if (newSize <= plot.size) {
            return false;
        }
        
        // Обновляем размер участка
        int oldSize = plot.size;
        plot.size = newSize;
        
        // Обновляем забор
        updatePlotFence(plot, playerUuid);
        
        // Сохраняем изменения
        savePlots();
        
        plugin.getLogger().info("Участок " + plot.id + " расширен с " + oldSize + "x" + oldSize + " до " + newSize + "x" + newSize);
        return true;
    }
    
    /**
     * Обновляет забор участка при расширении
     */
    private void updatePlotFence(PlotData plot, UUID playerUuid) {
        World world = plugin.getServer().getWorld(plugin.getConfigManager().getWorldName());
        if (world == null) return;
        
        String playerName = plugin.getServer().getOfflinePlayer(playerUuid).getName();
        if (playerName == null) playerName = "Неизвестный";
        
        // Удаляем старый забор (максимальный размер)
        int oldFenceX = plot.x - 1;
        int oldFenceZ = plot.z - 1;
        int oldFenceSize = plot.maxSize + 2;
        int fenceY = plot.y + 1;
        
        // Удаляем периметр старого забора
        for (int i = 0; i < oldFenceSize; i++) {
            // Нижняя сторона
            world.getBlockAt(oldFenceX + i, fenceY, oldFenceZ).setType(org.bukkit.Material.AIR);
            // Верхняя сторона
            world.getBlockAt(oldFenceX + i, fenceY, oldFenceZ + oldFenceSize - 1).setType(org.bukkit.Material.AIR);
            // Левая сторона
            world.getBlockAt(oldFenceX, fenceY, oldFenceZ + i).setType(org.bukkit.Material.AIR);
            // Правая сторона
            world.getBlockAt(oldFenceX + oldFenceSize - 1, fenceY, oldFenceZ + i).setType(org.bukkit.Material.AIR);
        }
        
        // Создаем новый забор вокруг текущего размера
        int fenceX = plot.x - 1;
        int fenceZ = plot.z - 1;
        int fenceSize = plot.size + 2; // +2 для забора вокруг текущего размера
        
        // Создаем периметр нового забора
        for (int i = 0; i < fenceSize; i++) {
            // Нижняя сторона
            world.getBlockAt(fenceX + i, fenceY, fenceZ).setType(org.bukkit.Material.OAK_FENCE);
            // Верхняя сторона
            world.getBlockAt(fenceX + i, fenceY, fenceZ + fenceSize - 1).setType(org.bukkit.Material.OAK_FENCE);
            // Левая сторона
            world.getBlockAt(fenceX, fenceY, fenceZ + i).setType(org.bukkit.Material.OAK_FENCE);
            // Правая сторона
            world.getBlockAt(fenceX + fenceSize - 1, fenceY, fenceZ + i).setType(org.bukkit.Material.OAK_FENCE);
        }
        
        // Обновляем табличку
        int signX = fenceX + (fenceSize / 2);
        int signZ = fenceZ - 1;
        int signY = fenceY;
        org.bukkit.block.Block signBlock = world.getBlockAt(signX, signY, signZ);
        
        // Удаляем старую табличку
        signBlock.setType(org.bukkit.Material.AIR);
        
        // Создаем новую табличку
        signBlock.setType(org.bukkit.Material.OAK_SIGN);
        
        if (signBlock.getState() instanceof org.bukkit.block.Sign) {
            org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signBlock.getState();
            sign.setLine(0, "§6Участок игрока:");
            sign.setLine(1, "§e" + playerName);
            sign.setLine(2, "§7ID: " + plot.id);
            sign.setLine(3, "§7Размер: " + plot.size + "x" + plot.size);
            sign.update();
        }
        
        plugin.getLogger().info("Обновлен забор для участка " + plot.id + " игрока " + playerName + " до размера " + plot.size + "x" + plot.size);
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
    
    /**
     * Удаляет участок игрока
     */
    public boolean deletePlot(UUID playerUuid) {
        if (!hasPlot(playerUuid)) {
            return false; // У игрока нет участка
        }
        
        PlotData plot = playerPlots.get(playerUuid);
        plugin.getLogger().info("Удаление участка ID " + plot.id + " для игрока " + playerUuid);
        
        // Удаляем забор и табличку
        removePlotFence(plot);
        
        // Удаляем участок из памяти
        playerPlots.remove(playerUuid);
        
        // Сбрасываем nextPlotId, если это был последний участок
        if (playerPlots.isEmpty()) {
            nextPlotId = 0;
        } else {
            // Находим максимальный ID среди оставшихся участков
            int maxId = -1;
            for (PlotData remainingPlot : playerPlots.values()) {
                if (remainingPlot.id > maxId) {
                    maxId = remainingPlot.id;
                }
            }
            nextPlotId = maxId + 1;
        }
        
        // Сохраняем изменения
        savePlots();
        
        return true;
    }
    
    /**
     * Удаляет забор вокруг участка и табличку
     */
    private void removePlotFence(PlotData plot) {
        World world = plugin.getServer().getWorld(plugin.getConfigManager().getWorldName());
        if (world == null) return;
        
        // Удаляем забор вокруг участка (учитываем максимальный размер)
        int fenceX = plot.x - 1;
        int fenceZ = plot.z - 1;
        int fenceSize = plot.maxSize + 2; // +2 для забора
        int fenceY = plot.y + 1; // Поднимаем забор на один блок выше
        
        // Удаляем периметр забора
        for (int i = 0; i < fenceSize; i++) {
            // Нижняя сторона
            world.getBlockAt(fenceX + i, fenceY, fenceZ).setType(org.bukkit.Material.AIR);
            // Верхняя сторона
            world.getBlockAt(fenceX + i, fenceY, fenceZ + fenceSize - 1).setType(org.bukkit.Material.AIR);
            // Левая сторона
            world.getBlockAt(fenceX, fenceY, fenceZ + i).setType(org.bukkit.Material.AIR);
            // Правая сторона
            world.getBlockAt(fenceX + fenceSize - 1, fenceY, fenceZ + i).setType(org.bukkit.Material.AIR);
        }
        
        // Удаляем табличку с именем игрока (в центре передней стороны)
        int signX = fenceX + (fenceSize / 2);
        int signZ = fenceZ - 1;
        int signY = fenceY; // Табличка на той же высоте, что и забор
        org.bukkit.block.Block signBlock = world.getBlockAt(signX, signY, signZ);
        signBlock.setType(org.bukkit.Material.AIR);
        
        plugin.getLogger().info("Удален забор для участка " + plot.id);
    }
    
    /**
     * Получает информацию о сетке участков
     */
    public String getGridInfo() {
        int plotsPerRow = 5;
        int totalPlots = playerPlots.size();
        int currentRow = totalPlots / plotsPerRow;
        int currentCol = totalPlots % plotsPerRow;
        
        return String.format("Всего участков: %d, Текущая позиция: ряд %d, колонка %d", 
                           totalPlots, currentRow, currentCol);
    }
    
    public static class PlotData {
        public int id;
        public int x;
        public int z;
        public int y;
        public int size;
        public int maxSize;
        
        public Location getCenterLocation(World world) {
            return new Location(world, x + size/2, y + 1, z + size/2);
        }
        
        public Location getTeleportLocation(World world) {
            // Телепорт в центр участка на безопасную высоту
            int centerX = x + size/2;
            int centerZ = z + size/2;
            
            // Находим безопасную высоту для телепортации
            int safeY = world.getHighestBlockYAt(centerX, centerZ) + 2;
            
            return new Location(world, centerX, safeY, centerZ);
        }
        
        public boolean isInPlot(Location location) {
            return location.getBlockX() >= x && location.getBlockX() < x + size &&
                   location.getBlockZ() >= z && location.getBlockZ() < z + size &&
                   location.getBlockY() >= y - 5 && location.getBlockY() <= y + 10; // Проверяем высоту
        }
        
        public String getCoordinates() {
            return String.format("X: %d, Y: %d, Z: %d", x, y, z);
        }
        
        public String getPlotInfo() {
            return String.format("ID: %d, Размер: %dx%d (макс: %dx%d), Позиция: ряд %d, колонка %d", 
                               id, size, size, maxSize, maxSize, id / 3, id % 3);
        }
    }
} 