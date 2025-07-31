package com.github.d88a.farmereconomist.plots;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.github.d88a.farmereconomist.data.DataManager;

public class PlotManager {

    private final FarmerEconomist plugin;
    private final DataManager dataManager;
    private final Map<UUID, Plot> plots = new HashMap<>();

    public PlotManager(FarmerEconomist plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        loadPlots();
    }

    public void savePlots() {
        ConfigurationSection plotsSection = dataManager.getPlotsConfig().createSection("plots");
        for (Map.Entry<UUID, Plot> entry : plots.entrySet()) {
            ConfigurationSection plotSection = plotsSection.createSection(entry.getKey().toString());
            plotSection.set("corner1", entry.getValue().getCorner1());
            plotSection.set("corner2", entry.getValue().getCorner2());
            plotSection.set("level", entry.getValue().getLevel());
            plotSection.set("creationTime", entry.getValue().getCreationTime());
        }
        dataManager.savePlotsConfig();
    }

    private void loadPlots() {
        ConfigurationSection plotsSection = dataManager.getPlotsConfig().getConfigurationSection("plots");
        if (plotsSection != null) {
            for (String uuidString : plotsSection.getKeys(false)) {
                UUID owner = UUID.fromString(uuidString);
                Location corner1 = plotsSection.getLocation(uuidString + ".corner1");
                Location corner2 = plotsSection.getLocation(uuidString + ".corner2");
                int level = plotsSection.getInt(uuidString + ".level", 1);
                long creationTime = plotsSection.getLong(uuidString + ".creationTime", System.currentTimeMillis());
                Plot plot = new Plot(owner, corner1, corner2, level, creationTime);
                plots.put(owner, plot);
            }
        }
    }

    public void addPlot(Player player, Plot plot) {
        plots.put(player.getUniqueId(), plot);
    }

    public Plot getPlot(Player player) {
        return plots.get(player.getUniqueId());
    }

    public boolean hasPlot(Player player) {
        return plots.containsKey(player.getUniqueId());
    }

    public Plot getPlotAt(org.bukkit.Location location) {
        for (Plot plot : plots.values()) {
            if (plot.isLocationInPlot(location)) {
                return plot;
            }
        }
        return null;
    }

    public Plot createNewPlotFor(Player player) {
        Location startLocation = plugin.getConfigManager().getGridStartLocation();
        if (startLocation == null) {
            return null; // Grid start not set
        }

        int plotSize = Plot.getSizeForLevel(1);
        // Максимальный размер участка 20x20.
        // Расширение от 8x8 до 20x20 происходит на (20-8)/2 = 6 блоков в каждую сторону.
        // Чтобы два соседних участка не столкнулись, нужно место для расширения каждого: 6 + 6 = 12 блоков.
        // Плюс дорожка в 3 блока и 2 блока на заборы: 12 + 3 + 2 = 17.
        // Таким образом, зазор между участками должен быть 17 блоков.
        int plotGap = 17;
        
        // Ищем первый свободный ID для участка
        int newPlotId = -1;
        for (int id = 0; id < 1000; id++) { // Ограничение, чтобы избежать вечного цикла
            Location[] corners = calculatePlotCorners(id, startLocation, plotSize, plotGap);
            Location corner1 = corners[0];
            Location corner2 = corners[1];
            if (isAreaFree(corner1, corner2)) {
                newPlotId = id;
                break;
            }
        }

        if (newPlotId == -1) {
            return null; // Не удалось найти свободное место
        }

        Location[] corners = calculatePlotCorners(newPlotId, startLocation, plotSize, plotGap);
        terraformAndBuild(corners[0], corners[1]);
        placePlotSign(corners[0], corners[1], player, 1);
        Plot newPlot = new Plot(player.getUniqueId(), corners[0], corners[1]);
        addPlot(player, newPlot);
        return newPlot;
    }

    private boolean isAreaFree(Location corner1, Location corner2) {
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        
        // Проверяем, не пересекается ли с существующими участками
        for (Plot existingPlot : plots.values()) {
            if (plotsOverlap(corner1, corner2, existingPlot.getCorner1(), existingPlot.getCorner2())) {
                return false;
            }
        }
        
        return true;
    }

    private boolean plotsOverlap(Location c1, Location c2, Location existingC1, Location existingC2) {
        int minX1 = Math.min(c1.getBlockX(), c2.getBlockX());
        int maxX1 = Math.max(c1.getBlockX(), c2.getBlockX());
        int minZ1 = Math.min(c1.getBlockZ(), c2.getBlockZ());
        int maxZ1 = Math.max(c1.getBlockZ(), c2.getBlockZ());
        
        int minX2 = Math.min(existingC1.getBlockX(), existingC2.getBlockX());
        int maxX2 = Math.max(existingC1.getBlockX(), existingC2.getBlockX());
        int minZ2 = Math.min(existingC1.getBlockZ(), existingC2.getBlockZ());
        int maxZ2 = Math.max(existingC1.getBlockZ(), existingC2.getBlockZ());
        
        return !(maxX1 < minX2 || maxX2 < minX1 || maxZ1 < minZ2 || maxZ2 < minZ1);
    }

    private void placePlotSign(Location corner1, Location corner2, Player player, int level) {
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int y = corner1.getBlockY();
        int signX = minX + (maxX - minX) / 2;
        int signZ = minZ - 2;
        World world = corner1.getWorld();
        org.bukkit.block.Block signBlock = world.getBlockAt(signX, y + 1, signZ);
        signBlock.setType(org.bukkit.Material.OAK_SIGN);
        if (signBlock.getState() instanceof org.bukkit.block.Sign) {
            org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signBlock.getState();
            sign.setLine(0, "§aОгород");
            sign.setLine(1, player.getName());
            double balance = plugin.getEconomyManager().getBalance(player);
            sign.setLine(2, "Баланс: " + balance);
            sign.setLine(3, "Уровень: " + level);
            sign.update();
        }
    }

    private boolean isExpansionAreaFree(Location corner1, Location corner2, UUID expandingPlayerId) {
        for (Plot existingPlot : plots.values()) {
            // Не проверять наложение с самим собой
            if (existingPlot.getOwner().equals(expandingPlayerId)) {
                continue;
            }
            if (plotsOverlap(corner1, corner2, existingPlot.getCorner1(), existingPlot.getCorner2())) {
                return false;
            }
        }
        return true;
    }

    // Метод для расширения участка
    public boolean expandPlot(Player player) {
        Plot plot = getPlot(player);
        if (plot == null) {
            plugin.getConfigManager().sendMessage(player, "plot_expand_no_plot");
            return false;
        }

        if (!plot.canExpand()) {
            plugin.getConfigManager().sendMessage(player, "plot_expand_max_level");
            return false;
        }

        if (!plot.hasMinimumTime()) {
            plugin.getConfigManager().sendMessage(player, "plot_expand_time_required");
            return false;
        }

        int cost = Plot.getExpansionCost(plot.getLevel());
        if (plugin.getEconomyManager().getBalance(player) < cost) {
            plugin.getConfigManager().sendMessage(player, "plot_expand_no_money");
            return false;
        }

        // Проверяем, что расширенная область свободна
        Location oldCorner1 = plot.getCorner1().clone(); // Важно клонировать!
        Location oldCorner2 = plot.getCorner2().clone();

        // Создаем временный "призрачный" участок, чтобы проверить, куда он расширится
        Plot ghostPlot = new Plot(plot.getOwner(), oldCorner1, oldCorner2, plot.getLevel(), plot.getCreationTime());
        ghostPlot.expandPlot();
        Location newCorner1 = ghostPlot.getCorner1();
        Location newCorner2 = ghostPlot.getCorner2();

        if (!isExpansionAreaFree(newCorner1, newCorner2, player.getUniqueId())) {
            plugin.getConfigManager().sendMessage(player, "plot_expand_area_occupied");
            return false;
        }

        // Снимаем деньги
        plugin.getEconomyManager().takeBalance(player, cost);

        // Расширяем участок (теперь уже по-настоящему)
        plot.expandPlot();

        // Перестраиваем участок: убираем старый забор, готовим землю, строим новый забор
        remodelPlot(plot, oldCorner1, oldCorner2);

        // Обновляем табличку
        updatePlotSign(player);

        plugin.getConfigManager().sendMessage(player, "plot_expand_success");
        plugin.getSoundManager().playSound(player, "plot_expand");

        return true;
    }

    private void remodelPlot(Plot plot, Location oldCorner1, Location oldCorner2) {
        Location newCorner1 = plot.getCorner1();
        Location newCorner2 = plot.getCorner2();
        World world = newCorner1.getWorld();
        int y = newCorner1.getBlockY();

        int oldMinX = Math.min(oldCorner1.getBlockX(), oldCorner2.getBlockX());
        int oldMaxX = Math.max(oldCorner1.getBlockX(), oldCorner2.getBlockX());
        int oldMinZ = Math.min(oldCorner1.getBlockZ(), oldCorner2.getBlockZ());
        int oldMaxZ = Math.max(oldCorner1.getBlockZ(), oldCorner2.getBlockZ());

        int newMinX = Math.min(newCorner1.getBlockX(), newCorner2.getBlockX());
        int newMaxX = Math.max(newCorner1.getBlockX(), newCorner2.getBlockX());
        int newMinZ = Math.min(newCorner1.getBlockZ(), newCorner2.getBlockZ());
        int newMaxZ = Math.max(newCorner1.getBlockZ(), newCorner2.getBlockZ());

        // 1. Очищаем старый забор и землю под ним
        for (int x = oldMinX - 1; x <= oldMaxX + 1; x++) {
            for (int z = oldMinZ - 1; z <= oldMaxZ + 1; z++) {
                if (x == oldMinX - 1 || x == oldMaxX + 1 || z == oldMinZ - 1 || z == oldMaxZ + 1) {
                    // Убираем все до уровня y-1
                    for (int yClear = y; yClear < y + 10; yClear++) {
                        world.getBlockAt(x, yClear, z).setType(Material.AIR);
                    }
                }
            }
        }

        // 2. Терраформируем новую область (не трогая старую, чтобы сохранить посадки)
        for (int x = newMinX; x <= newMaxX; x++) {
            for (int z = newMinZ; z <= newMaxZ; z++) {
                boolean isOldPlot = (x >= oldMinX && x <= oldMaxX && z >= oldMinZ && z <= oldMaxZ);
                if (!isOldPlot) {
                    world.getBlockAt(x, y, z).setType(Material.DIRT);
                    for (int yClear = y + 1; yClear < y + 10; yClear++) {
                        world.getBlockAt(x, yClear, z).setType(Material.AIR);
                    }
                }
            }
        }

        // 3. Строим новый забор и ворота
        Material fence = plugin.getConfigManager().getFenceMaterial();
        Material gate = plugin.getConfigManager().getGateMaterial();

        for (int x = newMinX - 1; x <= newMaxX + 1; x++) {
            for (int z = newMinZ - 1; z <= newMaxZ + 1; z++) {
                if (x == newMinX - 1 || x == newMaxX + 1 || z == newMinZ - 1 || z == newMaxZ + 1) {
                    boolean isCorner = (x == newMinX - 1 || x == newMaxX + 1) && (z == newMinZ - 1 || z == newMaxZ + 1);

                    world.getBlockAt(x, y, z).setType(Material.DIRT); // Земля под забором
                    world.getBlockAt(x, y + 1, z).setType(fence); // Забор или опора

                    int startClearY;
                    if (isCorner) {
                        world.getBlockAt(x, y + 2, z).setType(Material.LANTERN);
                        startClearY = y + 3;
                    } else {
                        startClearY = y + 2;
                    }
                    // Очищаем воздух выше
                    for (int yClear = startClearY; yClear < y + 10; yClear++) {
                        world.getBlockAt(x, yClear, z).setType(Material.AIR);
                    }
                }
            }
        }

        world.getBlockAt(newMinX + (newMaxX - newMinX) / 2, y + 1, newMinZ - 1).setType(gate);
    }
    private void terraformAndBuild(Location corner1, Location corner2) {
        World world = corner1.getWorld();
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        int y = corner1.getBlockY();

        // 1. Подготовка земли на самом участке
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                world.getBlockAt(x, y, z).setType(Material.DIRT);
                // Очистка воздуха над участком
                for (int yClear = y + 1; yClear < y + 10; yClear++) {
                    world.getBlockAt(x, yClear, z).setType(Material.AIR);
                }
            }
        }

        // 2. Строительство забора и очистка воздуха над ним
        Material fence = plugin.getConfigManager().getFenceMaterial();
        Material gate = plugin.getConfigManager().getGateMaterial();

        // Более чистый цикл для всего периметра забора
        for (int x = minX - 1; x <= maxX + 1; x++) {
            for (int z = minZ - 1; z <= maxZ + 1; z++) {
                // Строим только по внешнему краю
                if (x == minX - 1 || x == maxX + 1 || z == minZ - 1 || z == maxZ + 1) {
                    boolean isCorner = (x == minX - 1 || x == maxX + 1) && (z == minZ - 1 || z == maxZ + 1);

                    world.getBlockAt(x, y, z).setType(Material.DIRT); // Земля под забором
                    world.getBlockAt(x, y + 1, z).setType(fence); // Забор или опора

                    int startClearY;
                    if (isCorner) {
                        world.getBlockAt(x, y + 2, z).setType(Material.LANTERN);
                        startClearY = y + 3;
                    } else {
                        startClearY = y + 2;
                    }
                    // Очищаем воздух выше
                    for (int yClear = startClearY; yClear < y + 10; yClear++) {
                        world.getBlockAt(x, yClear, z).setType(Material.AIR);
                    }
                }
            }
        }

        // 3. Установка ворот
        world.getBlockAt(minX + (maxX - minX) / 2, y + 1, minZ - 1).setType(gate);
    }

    public void deletePlot(Player player) {
        Plot plot = plots.remove(player.getUniqueId());
        if (plot != null) {
            // Remove from config
            dataManager.getPlotsConfig().set("plots." + player.getUniqueId().toString(), null);
            dataManager.savePlotsConfig();
            
            // Clean up the plot area
            clearPlotArea(plot);
        }
    }
    
    private void clearPlotArea(Plot plot) {
        Location corner1 = plot.getCorner1();
        Location corner2 = plot.getCorner2();
        World world = corner1.getWorld();

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        int y = corner1.getBlockY();

        // Remove fence and turn land to stone
        for (int x = minX - 1; x <= maxX + 1; x++) {
            for (int z = minZ - 1; z <= maxZ + 1; z++) {
                // Clear fence level
                world.getBlockAt(x, y + 1, z).setType(Material.AIR);
                // Clear lantern level
                world.getBlockAt(x, y + 2, z).setType(Material.AIR);
                // Turn plot land to stone
                if (x >= minX && x <= maxX && z >= minZ && z <= maxZ) {
                    world.getBlockAt(x, y, z).setType(Material.STONE);
                }
            }
        }
    }

    public void updatePlotSign(Player player) {
        Plot plot = getPlot(player);
        if (plot == null) return;
        Location corner1 = plot.getCorner1();
        Location corner2 = plot.getCorner2();
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int y = corner1.getBlockY();
        int signX = minX + (maxX - minX) / 2;
        int signZ = minZ - 2;
        World world = corner1.getWorld();
        org.bukkit.block.Block signBlock = world.getBlockAt(signX, y + 1, signZ);
        if (signBlock.getState() instanceof org.bukkit.block.Sign) {
            org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signBlock.getState();
            sign.setLine(2, "Баланс: " + plugin.getEconomyManager().getBalance(player));
            sign.setLine(3, "Уровень: " + plot.getLevel());
            sign.update();
        }
    }

    public FarmerEconomist getPlugin() {
        return plugin;
    }

    private Location[] calculatePlotCorners(int plotId, Location startLocation, int plotSize, int plotGap) {
        int totalSize = plotSize + plotGap;
        int plotsPerRow = 5; // Consider making this a configurable constant

        int row = plotId / plotsPerRow;
        int col = plotId % plotsPerRow;

        World world = startLocation.getWorld();
        int plotX = startLocation.getBlockX() + col * totalSize;
        int plotZ = startLocation.getBlockZ() + row * totalSize;
        int plotY = startLocation.getBlockY();

        Location corner1 = new Location(world, plotX, plotY, plotZ);
        Location corner2 = new Location(world, plotX + plotSize - 1, plotY, plotZ + plotSize - 1);

        return new Location[]{corner1, corner2};
    }
} 