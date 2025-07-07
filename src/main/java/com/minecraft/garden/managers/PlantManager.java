package com.minecraft.garden.managers;

import com.minecraft.garden.GardenPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlantManager {
    
    private final GardenPlugin plugin;
    private final Map<Location, PlantData> plantedCrops;
    
    // Маппинг семян на растения
    private final Map<Material, Material> seedToCrop = new HashMap<>();
    private final Map<Material, Integer> cropGrowthTimes = new HashMap<>();
    
    public PlantManager(GardenPlugin plugin) {
        this.plugin = plugin;
        this.plantedCrops = new HashMap<>();
        
        initializeCropMappings();
        loadPlantedCrops();
    }
    
    private void initializeCropMappings() {
        // Маппинг семян на растения
        seedToCrop.put(Material.WHEAT_SEEDS, Material.WHEAT);
        seedToCrop.put(Material.CARROT, Material.CARROTS);
        seedToCrop.put(Material.POTATO, Material.POTATOES);
        seedToCrop.put(Material.BEETROOT_SEEDS, Material.BEETROOTS);
        seedToCrop.put(Material.PUMPKIN_SEEDS, Material.PUMPKIN);
        seedToCrop.put(Material.MELON_SEEDS, Material.MELON);
        
        // Время роста из конфигурации (в секундах)
        cropGrowthTimes.put(Material.WHEAT, plugin.getConfigManager().getCropGrowthTime("wheat"));
        cropGrowthTimes.put(Material.CARROTS, plugin.getConfigManager().getCropGrowthTime("carrot"));
        cropGrowthTimes.put(Material.POTATOES, plugin.getConfigManager().getCropGrowthTime("potato"));
        cropGrowthTimes.put(Material.BEETROOTS, plugin.getConfigManager().getCropGrowthTime("beetroot"));
        cropGrowthTimes.put(Material.PUMPKIN, plugin.getConfigManager().getCropGrowthTime("pumpkin"));
        cropGrowthTimes.put(Material.MELON, plugin.getConfigManager().getCropGrowthTime("melon"));
    }
    
    private void loadPlantedCrops() {
        // Загрузка посаженных растений из данных
        ConfigurationSection cropsSection = plugin.getDataManager().getData().getConfigurationSection("planted_crops");
        if (cropsSection != null) {
            for (String locationKey : cropsSection.getKeys(false)) {
                ConfigurationSection cropSection = cropsSection.getConfigurationSection(locationKey);
                if (cropSection != null) {
                    try {
                        String[] coords = locationKey.split(",");
                        int x = Integer.parseInt(coords[0]);
                        int y = Integer.parseInt(coords[1]);
                        int z = Integer.parseInt(coords[2]);
                        String worldName = coords[3];
                        
                        World world = plugin.getServer().getWorld(worldName);
                        if (world != null) {
                            Location location = new Location(world, x, y, z);
                            
                            PlantData plantData = new PlantData();
                            plantData.cropType = Material.valueOf(cropSection.getString("crop_type"));
                            plantData.plantedTime = cropSection.getLong("planted_time");
                            plantData.ownerUuid = UUID.fromString(cropSection.getString("owner"));
                            plantData.plotId = cropSection.getInt("plot_id");
                            
                            plantedCrops.put(location, plantData);
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("Ошибка загрузки растения: " + locationKey + " - " + e.getMessage());
                    }
                }
            }
        }
    }
    
    public void savePlantedCrops() {
        // Сохранение посаженных растений в данные
        ConfigurationSection cropsSection = plugin.getDataManager().getData().createSection("planted_crops");
        
        for (Map.Entry<Location, PlantData> entry : plantedCrops.entrySet()) {
            Location location = entry.getKey();
            PlantData plantData = entry.getValue();
            
            String locationKey = location.getBlockX() + "," + location.getBlockY() + "," + 
                               location.getBlockZ() + "," + location.getWorld().getName();
            
            ConfigurationSection cropSection = cropsSection.createSection(locationKey);
            cropSection.set("crop_type", plantData.cropType.name());
            cropSection.set("planted_time", plantData.plantedTime);
            cropSection.set("owner", plantData.ownerUuid.toString());
            cropSection.set("plot_id", plantData.plotId);
        }
        
        plugin.getDataManager().saveData();
    }
    
    /**
     * Пытается посадить семя на участке игрока
     */
    public boolean plantSeed(Player player, Material seedType, Location location) {
        // Проверяем, что у игрока есть участок
        if (!plugin.getPlotManager().hasPlot(player.getUniqueId())) {
            player.sendMessage("§cУ вас нет участка для посадки!");
            return false;
        }
        
        // Проверяем, что семя можно посадить
        if (!seedToCrop.containsKey(seedType)) {
            player.sendMessage("§cЭтот предмет нельзя посадить!");
            return false;
        }
        
        // Проверяем, что место находится на участке игрока
        PlotManager.PlotData plot = plugin.getPlotManager().getPlot(player.getUniqueId());
        if (!plot.isInPlot(location)) {
            player.sendMessage("§cВы можете сажать только на своем участке!");
            return false;
        }
        
        // Проверяем, что место подходит для посадки
        if (!isValidPlantingLocation(location)) {
            player.sendMessage("§cЗдесь нельзя посадить растение!");
            return false;
        }
        
        // Проверяем, что место не занято
        if (plantedCrops.containsKey(location)) {
            player.sendMessage("§cЗдесь уже растет растение!");
            return false;
        }
        
        // Сажаем растение
        Material cropType = seedToCrop.get(seedType);
        Block block = location.getBlock();
        
        // Устанавливаем блок растения
        block.setType(cropType);
        
        // Создаем данные о растении
        PlantData plantData = new PlantData();
        plantData.cropType = cropType;
        plantData.plantedTime = System.currentTimeMillis();
        plantData.ownerUuid = player.getUniqueId();
        plantData.plotId = plot.id;
        
        plantedCrops.put(location, plantData);
        savePlantedCrops();
        
        // Запускаем процесс роста
        startGrowthProcess(location, plantData);
        
        player.sendMessage("§a" + getCropDisplayName(cropType) + " посажено!");
        return true;
    }
    
    /**
     * Проверяет, подходит ли место для посадки
     */
    private boolean isValidPlantingLocation(Location location) {
        Block block = location.getBlock();
        Block belowBlock = location.clone().subtract(0, 1, 0).getBlock();
        
        // Проверяем, что блок под растением подходящий
        Material belowType = belowBlock.getType();
        boolean validSoil = belowType == Material.FARMLAND || 
                           belowType == Material.GRASS_BLOCK || 
                           belowType == Material.DIRT;
        
        // Проверяем, что место для посадки свободно
        boolean spaceFree = block.getType().isAir();
        
        return validSoil && spaceFree;
    }
    
    /**
     * Запускает процесс роста растения
     */
    private void startGrowthProcess(Location location, PlantData plantData) {
        int growthTime = cropGrowthTimes.getOrDefault(plantData.cropType, 60); // 60 секунд по умолчанию
        int maxAge = getMaxAge(plantData.cropType);
        
        new BukkitRunnable() {
            int currentAge = 0;
            
            @Override
            public void run() {
                // Проверяем, что растение все еще существует
                if (!plantedCrops.containsKey(location)) {
                    this.cancel();
                    return;
                }
                
                Block block = location.getBlock();
                if (block.getType() != plantData.cropType) {
                    // Растение было уничтожено
                    plantedCrops.remove(location);
                    savePlantedCrops();
                    this.cancel();
                    return;
                }
                
                // Увеличиваем возраст растения
                if (currentAge < maxAge) {
                    currentAge++;
                    
                    // Обновляем блок растения
                    BlockData blockData = block.getBlockData();
                    if (blockData instanceof Ageable) {
                        Ageable ageable = (Ageable) blockData;
                        ageable.setAge(currentAge);
                        block.setBlockData(ageable);
                    }
                    
                    // Уведомляем владельца о готовности урожая
                    if (currentAge >= maxAge) {
                        Player owner = plugin.getServer().getPlayer(plantData.ownerUuid);
                        if (owner != null && owner.isOnline()) {
                            owner.sendMessage("§a" + getCropDisplayName(plantData.cropType) + " готов к сбору!");
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, growthTime * 20L, growthTime * 20L); // Конвертируем секунды в тики
    }
    
    /**
     * Получает максимальный возраст для типа растения
     */
    private int getMaxAge(Material cropType) {
        switch (cropType) {
            case WHEAT:
            case CARROTS:
            case POTATOES:
                return 7;
            case BEETROOTS:
                return 3;
            case PUMPKIN:
            case MELON:
                return 0; // Эти растения не имеют стадий роста
            default:
                return 7;
        }
    }
    
    /**
     * Получает отображаемое имя растения
     */
    private String getCropDisplayName(Material cropType) {
        switch (cropType) {
            case WHEAT: return "Пшеница";
            case CARROTS: return "Морковь";
            case POTATOES: return "Картофель";
            case BEETROOTS: return "Свекла";
            case PUMPKIN: return "Тыква";
            case MELON: return "Арбуз";
            default: return cropType.name();
        }
    }
    
    /**
     * Собирает урожай с растения
     */
    public boolean harvestCrop(Player player, Location location) {
        PlantData plantData = plantedCrops.get(location);
        if (plantData == null) {
            return false;
        }
        
        // Проверяем, что растение принадлежит игроку
        if (!plantData.ownerUuid.equals(player.getUniqueId())) {
            player.sendMessage("§cЭто не ваш урожай!");
            return false;
        }
        
        // Проверяем, что растение созрело
        Block block = location.getBlock();
        if (block.getType() != plantData.cropType) {
            return false;
        }
        
        boolean isMature = false;
        if (block.getBlockData() instanceof Ageable) {
            Ageable ageable = (Ageable) block.getBlockData();
            isMature = ageable.getAge() >= getMaxAge(plantData.cropType);
        } else {
            // Для тыквы и арбуза считаем зрелыми всегда
            isMature = true;
        }
        
        if (!isMature) {
            player.sendMessage("§eРастение еще не созрело!");
            return false;
        }
        
        // Удаляем растение и даем урожай
        block.setType(Material.AIR);
        plantedCrops.remove(location);
        savePlantedCrops();
        
        // Даем игроку урожай
        giveHarvestToPlayer(player, plantData.cropType);
        
        player.sendMessage("§aУрожай собран!");
        return true;
    }
    
    /**
     * Дает игроку собранный урожай
     */
    private void giveHarvestToPlayer(Player player, Material cropType) {
        Material harvestItem = getHarvestItem(cropType);
        int amount = getHarvestAmount(cropType);
        
        // Добавляем предметы в инвентарь игрока
        player.getInventory().addItem(new org.bukkit.inventory.ItemStack(harvestItem, amount));
        
        player.sendMessage("§aПолучено: §e" + amount + "x " + getCropDisplayName(harvestItem));
    }
    
    /**
     * Получает предмет урожая для типа растения
     */
    private Material getHarvestItem(Material cropType) {
        switch (cropType) {
            case WHEAT: return Material.WHEAT;
            case CARROTS: return Material.CARROT;
            case POTATOES: return Material.POTATO;
            case BEETROOTS: return Material.BEETROOT;
            case PUMPKIN: return Material.PUMPKIN;
            case MELON: return Material.MELON;
            default: return cropType;
        }
    }
    
    /**
     * Получает количество урожая с одного растения
     */
    private int getHarvestAmount(Material cropType) {
        switch (cropType) {
            case WHEAT: return 1;
            case CARROTS: return 1;
            case POTATOES: return 1;
            case BEETROOTS: return 1;
            case PUMPKIN: return 1;
            case MELON: return 1;
            default: return 1;
        }
    }
    
    /**
     * Проверяет, есть ли посаженное растение на локации
     */
    public boolean hasPlantedCrop(Location location) {
        return plantedCrops.containsKey(location);
    }
    
    /**
     * Получает данные о растении на локации
     */
    public PlantData getPlantedCrop(Location location) {
        return plantedCrops.get(location);
    }
    
    /**
     * Получает все посаженные растения игрока
     */
    public Map<Location, PlantData> getPlayerCrops(UUID playerUuid) {
        Map<Location, PlantData> playerCrops = new HashMap<>();
        for (Map.Entry<Location, PlantData> entry : plantedCrops.entrySet()) {
            if (entry.getValue().ownerUuid.equals(playerUuid)) {
                playerCrops.put(entry.getKey(), entry.getValue());
            }
        }
        return playerCrops;
    }
    
    /**
     * Класс для хранения данных о растении
     */
    public static class PlantData {
        public Material cropType;
        public long plantedTime;
        public UUID ownerUuid;
        public int plotId;
    }
} 