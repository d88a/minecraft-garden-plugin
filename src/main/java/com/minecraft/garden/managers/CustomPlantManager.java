package com.minecraft.garden.managers;

import com.minecraft.garden.GardenPlugin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomPlantManager {
    
    private final GardenPlugin plugin;
    private final Map<String, CustomPlant> customPlants;
    private final Map<Location, GrowingPlant> growingPlants;
    
    public CustomPlantManager(GardenPlugin plugin) {
        this.plugin = plugin;
        this.customPlants = new HashMap<>();
        this.growingPlants = new HashMap<>();
        
        initializeCustomPlants();
    }
    
    private void initializeCustomPlants() {
        plugin.getLogger().info("Инициализация кастомных растений...");
        
        // Золотое дерево
        registerCustomPlant(new CustomPlant(
            "golden_tree",
            "🌳 Золотое дерево",
            Material.OAK_SAPLING,
            Material.GOLD_INGOT,
            "Золотые семена дерева",
            new String[]{
                "§7Семена мистического золотого дерева",
                "§7Время роста: §e5 минут",
                "§7Цена: §e50 рублей",
                "§7Урожай: §eЗолотые яблоки",
                "§7Особенность: §eДает золотые яблоки"
            },
            300, // 5 минут
            Material.GOLDEN_APPLE,
            3 // количество урожая
        ));
        
        // Кристальная роза
        registerCustomPlant(new CustomPlant(
            "crystal_rose",
            "💎 Кристальная роза",
            Material.ROSE_BUSH,
            Material.AMETHYST_SHARD,
            "Кристальные семена розы",
            new String[]{
                "§7Семена сияющей кристальной розы",
                "§7Время роста: §e3 минуты",
                "§7Цена: §e35 рублей",
                "§7Урожай: §eКристальные лепестки",
                "§7Особенность: §eСветится в темноте"
            },
            180, // 3 минуты
            Material.AMETHYST_SHARD,
            5 // количество урожая
        ));
        
        // Огненная тыква
        registerCustomPlant(new CustomPlant(
            "fire_pumpkin",
            "🔥 Огненная тыква",
            Material.PUMPKIN,
            Material.BLAZE_POWDER,
            "Огненные семена тыквы",
            new String[]{
                "§7Семена пылающей тыквы",
                "§7Время роста: §e4 минуты",
                "§7Цена: §e40 рублей",
                "§7Урожай: §eОгненные тыквы",
                "§7Особенность: §eПоджигает врагов"
            },
            240, // 4 минуты
            Material.BLAZE_POWDER,
            2 // количество урожая
        ));
        
        // Ледяная ягода
        registerCustomPlant(new CustomPlant(
            "ice_berry",
            "❄️ Ледяная ягода",
            Material.SWEET_BERRIES,
            Material.ICE,
            "Ледяные семена ягоды",
            new String[]{
                "§7Семена замороженной ягоды",
                "§7Время роста: §e2 минуты",
                "§7Цена: §e25 рублей",
                "§7Урожай: §eЛедяные ягоды",
                "§7Особенность: §eЗамораживает врагов"
            },
            120, // 2 минуты
            Material.ICE,
            4 // количество урожая
        ));
        
        // Электрическая пшеница
        registerCustomPlant(new CustomPlant(
            "electric_wheat",
            "⚡ Электрическая пшеница",
            Material.WHEAT,
            Material.REDSTONE,
            "Электрические семена пшеницы",
            new String[]{
                "§7Семена заряженной пшеницы",
                "§7Время роста: §e90 секунд",
                "§7Цена: §e30 рублей",
                "§7Урожай: §eЭлектрическая пшеница",
                "§7Особенность: §eУдаряет током"
            },
            90, // 90 секунд
            Material.REDSTONE,
            6 // количество урожая
        ));
        
        // Радужный цветок
        registerCustomPlant(new CustomPlant(
            "rainbow_flower",
            "🌈 Радужный цветок",
            Material.SUNFLOWER,
            Material.ORANGE_DYE,
            "Радужные семена цветка",
            new String[]{
                "§7Семена разноцветного цветка",
                "§7Время роста: §e6 минут",
                "§7Цена: §e60 рублей",
                "§7Урожай: §eРадужные лепестки",
                "§7Особенность: §eМеняет цвет"
            },
            360, // 6 минут
            Material.ORANGE_DYE,
            8 // количество урожая
        ));
        
        plugin.getLogger().info("Зарегистрировано " + customPlants.size() + " кастомных растений");
    }
    
    private void registerCustomPlant(CustomPlant plant) {
        customPlants.put(plant.getId(), plant);
        plugin.getLogger().info("  ✓ " + plant.getName());
    }
    
    public CustomPlant getCustomPlant(String id) {
        return customPlants.get(id);
    }
    
    public Map<String, CustomPlant> getAllCustomPlants() {
        return customPlants;
    }
    
    public ItemStack createCustomSeed(String plantId) {
        CustomPlant plant = customPlants.get(plantId);
        if (plant == null) {
            return null;
        }
        
        ItemStack seed = new ItemStack(plant.getSeedMaterial());
        ItemMeta meta = seed.getItemMeta();
        meta.setDisplayName(plant.getSeedName());
        meta.setLore(Arrays.asList(plant.getSeedLore()));
        seed.setItemMeta(meta);
        
        return seed;
    }
    
    public boolean isCustomSeed(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return false;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        return customPlants.values().stream()
                .anyMatch(plant -> plant.getSeedName().equals(displayName));
    }
    
    public CustomPlant getPlantBySeed(ItemStack seed) {
        if (!isCustomSeed(seed)) {
            return null;
        }
        
        String displayName = seed.getItemMeta().getDisplayName();
        return customPlants.values().stream()
                .filter(plant -> plant.getSeedName().equals(displayName))
                .findFirst()
                .orElse(null);
    }
    
    public boolean plantCustomSeed(Player player, ItemStack seed, Location location) {
        CustomPlant plant = getPlantBySeed(seed);
        if (plant == null) {
            return false;
        }
        
        if (!isValidPlantingLocation(location)) {
            player.sendMessage("§cЗдесь нельзя посадить растение!");
            return false;
        }
        
        if (growingPlants.containsKey(location)) {
            player.sendMessage("§cЗдесь уже растет растение!");
            return false;
        }
        
        Block block = location.getBlock();
        block.setType(plant.getPlantMaterial());
        
        GrowingPlant growingPlant = new GrowingPlant();
        growingPlant.plantId = plant.getId();
        growingPlant.plantedTime = System.currentTimeMillis();
        growingPlant.ownerUuid = player.getUniqueId();
        growingPlant.currentStage = 0;
        growingPlant.maxStages = 3;
        
        growingPlants.put(location, growingPlant);
        
        startCustomGrowthProcess(location, growingPlant, plant);
        
        player.sendMessage("§a" + plant.getName() + " посажено!");
        player.sendMessage("§eВремя роста: §7" + (plant.getGrowthTime() / 60) + " минут");
        
        return true;
    }
    
    private boolean isValidPlantingLocation(Location location) {
        Block block = location.getBlock();
        Block belowBlock = location.clone().subtract(0, 1, 0).getBlock();
        
        Material belowType = belowBlock.getType();
        boolean validSoil = belowType == Material.FARMLAND || 
                           belowType == Material.DIRT ||
                           belowType == Material.GRASS_BLOCK ||
                           belowType == Material.SAND ||
                           belowType == Material.STONE;
        
        boolean spaceFree = block.getType().isAir();
        
        return validSoil && spaceFree;
    }
    
    private void startCustomGrowthProcess(Location location, GrowingPlant growingPlant, CustomPlant plant) {
        int stageTime = plant.getGrowthTime() / growingPlant.maxStages;
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!growingPlants.containsKey(location)) {
                    this.cancel();
                    return;
                }
                
                Block block = location.getBlock();
                if (block.getType() != plant.getPlantMaterial()) {
                    growingPlants.remove(location);
                    this.cancel();
                    return;
                }
                
                growingPlant.currentStage++;
                updatePlantAppearance(block, growingPlant, plant);
                
                Player owner = plugin.getServer().getPlayer(growingPlant.ownerUuid);
                if (owner != null && owner.isOnline()) {
                    int progress = (growingPlant.currentStage * 100) / growingPlant.maxStages;
                    owner.sendMessage("§e" + plant.getName() + " растет... §7" + progress + "%");
                    
                    if (growingPlant.currentStage >= growingPlant.maxStages) {
                        owner.sendMessage("§a" + plant.getName() + " готов к сбору!");
                    }
                }
                
                if (growingPlant.currentStage >= growingPlant.maxStages) {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, stageTime * 20L, stageTime * 20L);
    }
    
    private void updatePlantAppearance(Block block, GrowingPlant growingPlant, CustomPlant plant) {
        if (growingPlant.currentStage == 1) {
            switch (plant.getPlantMaterial()) {
                case OAK_SAPLING:
                    block.setType(Material.OAK_SAPLING);
                    break;
                case ROSE_BUSH:
                    block.setType(Material.POPPY);
                    break;
                case PUMPKIN:
                    block.setType(Material.PUMPKIN_STEM);
                    break;
                case SWEET_BERRIES:
                    block.setType(Material.SWEET_BERRIES);
                    break;
                case WHEAT:
                    block.setType(Material.WHEAT);
                    break;
                case SUNFLOWER:
                    block.setType(Material.DANDELION);
                    break;
            }
        } else if (growingPlant.currentStage >= growingPlant.maxStages) {
            block.setType(plant.getPlantMaterial());
        }
    }
    
    public boolean harvestCustomPlant(Player player, Location location) {
        GrowingPlant growingPlant = growingPlants.get(location);
        if (growingPlant == null) {
            return false;
        }
        
        if (!growingPlant.ownerUuid.equals(player.getUniqueId())) {
            player.sendMessage("§cЭто не ваш урожай!");
            return false;
        }
        
        if (growingPlant.currentStage < growingPlant.maxStages) {
            player.sendMessage("§eРастение еще не созрело!");
            return false;
        }
        
        CustomPlant plant = customPlants.get(growingPlant.plantId);
        if (plant == null) {
            return false;
        }
        
        location.getBlock().setType(Material.AIR);
        growingPlants.remove(location);
        
        ItemStack harvest = new ItemStack(plant.getHarvestMaterial(), plant.getHarvestAmount());
        player.getInventory().addItem(harvest);
        
        player.sendMessage("§aУрожай собран: §e" + plant.getName());
        player.sendMessage("§eПолучено: §7" + plant.getHarvestAmount() + "x " + plant.getHarvestMaterial().name());
        
        return true;
    }
    
    public static class CustomPlant {
        private final String id;
        private final String name;
        private final Material plantMaterial;
        private final Material seedMaterial;
        private final String seedName;
        private final String[] seedLore;
        private final int growthTime;
        private final Material harvestMaterial;
        private final int harvestAmount;
        
        public CustomPlant(String id, String name, Material plantMaterial, Material seedMaterial,
                          String seedName, String... seedLore) {
            this.id = id;
            this.name = name;
            this.plantMaterial = plantMaterial;
            this.seedMaterial = seedMaterial;
            this.seedName = seedName;
            this.seedLore = seedLore;
            this.growthTime = 300;
            this.harvestMaterial = seedMaterial;
            this.harvestAmount = 1;
        }
        
        public CustomPlant(String id, String name, Material plantMaterial, Material seedMaterial,
                          String seedName, String[] seedLore, int growthTime, Material harvestMaterial, int harvestAmount) {
            this.id = id;
            this.name = name;
            this.plantMaterial = plantMaterial;
            this.seedMaterial = seedMaterial;
            this.seedName = seedName;
            this.seedLore = seedLore;
            this.growthTime = growthTime;
            this.harvestMaterial = harvestMaterial;
            this.harvestAmount = harvestAmount;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public Material getPlantMaterial() { return plantMaterial; }
        public Material getSeedMaterial() { return seedMaterial; }
        public String getSeedName() { return seedName; }
        public String[] getSeedLore() { return seedLore; }
        public int getGrowthTime() { return growthTime; }
        public Material getHarvestMaterial() { return harvestMaterial; }
        public int getHarvestAmount() { return harvestAmount; }
    }
    
    public static class GrowingPlant {
        public String plantId;
        public long plantedTime;
        public UUID ownerUuid;
        public int currentStage;
        public int maxStages;
    }
} 