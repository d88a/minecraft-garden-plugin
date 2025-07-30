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
    private int nextPlotId = 0; // Tracks the number of plots to determine the next location

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
        nextPlotId = plots.size();
    }

    public void addPlot(Player player, Plot plot) {
        plots.put(player.getUniqueId(), plot);
        nextPlotId++;
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

        // Используем фиксированный размер для базового участка
        int plotSize = Plot.getSizeForLevel(1); // 8x8
        int plotGap = 4; // Отступ между участками для будущего расширения
        int totalSize = plotSize + plotGap;

        // Простая сетка: участки размещаются по строкам и столбцам
        int plotsPerRow = 5; // 5 участков в ряду
        int row = nextPlotId / plotsPerRow;
        int col = nextPlotId % plotsPerRow;

        World world = startLocation.getWorld();
        int plotX = startLocation.getBlockX() + col * totalSize;
        int plotZ = startLocation.getBlockZ() + row * totalSize;
        int plotY = startLocation.getBlockY();

        Location corner1 = new Location(world, plotX, plotY, plotZ);
        Location corner2 = new Location(world, plotX + plotSize - 1, plotY, plotZ + plotSize - 1);

        // Проверяем, что место свободно
        if (!isAreaFree(corner1, corner2)) {
            // Если место занято, ищем следующее свободное
            return findNextFreePlot(player, startLocation, plotSize, plotGap);
        }

        terraformAndBuild(corner1, corner2);

        // Ставим табличку у входа
        placePlotSign(corner1, corner2, player, 1);

        Plot newPlot = new Plot(player.getUniqueId(), corner1, corner2);
        addPlot(player, newPlot);
        return newPlot;
    }

    private Plot findNextFreePlot(Player player, Location startLocation, int plotSize, int plotGap) {
        int totalSize = plotSize + plotGap;
        int plotsPerRow = 5;
        
        // Ищем свободное место, начиная с текущего nextPlotId
        for (int attempt = 0; attempt < 100; attempt++) { // Максимум 100 попыток
            int testId = nextPlotId + attempt;
            int row = testId / plotsPerRow;
            int col = testId % plotsPerRow;
            
            int plotX = startLocation.getBlockX() + col * totalSize;
            int plotZ = startLocation.getBlockZ() + row * totalSize;
            int plotY = startLocation.getBlockY();
            
            Location corner1 = new Location(startLocation.getWorld(), plotX, plotY, plotZ);
            Location corner2 = new Location(startLocation.getWorld(), plotX + plotSize - 1, plotY, plotZ + plotSize - 1);
            
            if (isAreaFree(corner1, corner2)) {
                terraformAndBuild(corner1, corner2);
                placePlotSign(corner1, corner2, player, 1);
                
                Plot newPlot = new Plot(player.getUniqueId(), corner1, corner2);
                addPlot(player, newPlot);
                nextPlotId = testId + 1; // Обновляем nextPlotId
                return newPlot;
            }
        }
        
        return null; // Не удалось найти свободное место
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
        Location oldCorner2 = plot.getCorner2();
        int newSize = Plot.getSizeForLevel(plot.getLevel() + 1);
        int currentSize = plot.getSize();
        int expansion = newSize - currentSize;
        
        Location newCorner2 = new Location(oldCorner2.getWorld(), 
            oldCorner2.getBlockX() + expansion, 
            oldCorner2.getBlockY(), 
            oldCorner2.getBlockZ() + expansion);

        if (!isAreaFree(oldCorner2, newCorner2)) {
            plugin.getConfigManager().sendMessage(player, "plot_expand_area_occupied");
            return false;
        }

        // Снимаем деньги
        plugin.getEconomyManager().takeBalance(player, cost);

        // Расширяем участок
        plot.expandPlot();

        // Перестраиваем забор и подготавливаем новую землю
        rebuildPlotFence(plot);
        prepareExpandedArea(oldCorner2, newCorner2);

        // Обновляем табличку
        updatePlotSign(player);

        plugin.getConfigManager().sendMessage(player, "plot_expand_success");
        plugin.getSoundManager().playSound(player, "plot_expand");
        
        return true;
    }

    private void rebuildPlotFence(Plot plot) {
        Location corner1 = plot.getCorner1();
        Location corner2 = plot.getCorner2();
        World world = corner1.getWorld();
        int y = corner1.getBlockY();

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        Material fence = plugin.getConfigManager().getFenceMaterial();
        Material gate = plugin.getConfigManager().getGateMaterial();

        // Убираем старый забор и ставим новый
        for (int x = minX - 1; x <= maxX + 1; x++) {
            for (int z = minZ - 1; z <= maxZ + 1; z++) {
                if (x == minX - 1 || x == maxX + 1 || z == minZ - 1 || z == maxZ + 1) {
                    world.getBlockAt(x, y + 1, z).setType(fence);
                    world.getBlockAt(x, y, z).setType(Material.DIRT);
                }
            }
        }

        // Добавляем ворота
        world.getBlockAt(minX + (maxX - minX) / 2, y + 1, minZ - 1).setType(gate);
    }

    private void prepareExpandedArea(Location oldCorner2, Location newCorner2) {
        World world = oldCorner2.getWorld();
        int y = oldCorner2.getBlockY();

        int minX = Math.min(oldCorner2.getBlockX() + 1, newCorner2.getBlockX());
        int maxX = Math.max(oldCorner2.getBlockX() + 1, newCorner2.getBlockX());
        int minZ = Math.min(oldCorner2.getBlockZ() + 1, newCorner2.getBlockZ());
        int maxZ = Math.max(oldCorner2.getBlockZ() + 1, newCorner2.getBlockZ());

        // Подготавливаем новую землю
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                world.getBlockAt(x, y, z).setType(Material.DIRT);
                // Очищаем пространство сверху
                for (int yClear = y + 1; yClear < y + 10; yClear++) {
                    world.getBlockAt(x, yClear, z).setType(Material.AIR);
                }
            }
        }
    }

    private void terraformAndBuild(Location corner1, Location corner2) {
        World world = corner1.getWorld();
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        int y = corner1.getBlockY();

        // Terraforming
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                world.getBlockAt(x, y, z).setType(Material.DIRT);
                // Clear space above
                for (int yClear = y + 1; yClear < y + 10; yClear++) {
                    world.getBlockAt(x, yClear, z).setType(Material.AIR);
                }
            }
        }

        // Build fence
        Material fence = plugin.getConfigManager().getFenceMaterial();
        Material gate = plugin.getConfigManager().getGateMaterial();

        for (int x = minX - 1; x <= maxX + 1; x++) {
            world.getBlockAt(x, y + 1, minZ - 1).setType(fence);
            // Новый код: слой земли под забором
            world.getBlockAt(x, y, minZ - 1).setType(Material.DIRT);
            world.getBlockAt(x, y + 1, maxZ + 1).setType(fence);
            world.getBlockAt(x, y, maxZ + 1).setType(Material.DIRT);
        }
        for (int z = minZ; z <= maxZ; z++) {
            world.getBlockAt(minX - 1, y + 1, z).setType(fence);
            world.getBlockAt(minX - 1, y, z).setType(Material.DIRT);
            world.getBlockAt(maxX + 1, y + 1, z).setType(fence);
            world.getBlockAt(maxX + 1, y, z).setType(Material.DIRT);
        }

        // Add gate
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
} 