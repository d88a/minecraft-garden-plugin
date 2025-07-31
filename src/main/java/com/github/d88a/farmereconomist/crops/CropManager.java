package com.github.d88a.farmereconomist.crops;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.data.DataManager;
import com.github.d88a.farmereconomist.items.ItemManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import org.bukkit.inventory.meta.SkullMeta;

public class CropManager {

    private final FarmerEconomist plugin;
    private final DataManager dataManager;
    private final Map<Location, CustomCrop> crops = new ConcurrentHashMap<>();
    private static final long GROWTH_INTERVAL = 1 * 60 * 1000; // 1 minute in milliseconds

    public CropManager(FarmerEconomist plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        startGrowthTask();
        loadCrops();
    }

    public void plantCrop(Location location, CustomCrop.CropType type) {
        if (crops.containsKey(location)) return;

        CustomCrop crop = new CustomCrop(location, type);
        crops.put(location, crop);

        // Place the first stage of the crop
        updateCropBlock(crop);
    }

    public void harvestCrop(Location location) {
        CustomCrop crop = crops.get(location);
        if (crop != null) {
            // Определяем предмет для дропа
            ItemStack drop = null;
            switch (crop.getType()) {
                case TOMATO:
                    drop = ItemManager.createTomato(); break;
                case GLOWSHROOM:
                    drop = ItemManager.createGlowshroomDust(); break;
                case LUNAR_BERRY:
                    drop = ItemManager.createLunarBerry(); break;
                case RAINBOW_MUSHROOM:
                    drop = ItemManager.createRainbowMushroom(); break;
                case CRYSTAL_CACTUS:
                    drop = ItemManager.createCrystalCactus(); break;
                case FLAME_PEPPER:
                    drop = ItemManager.createFlamePepper(); break;
                case MYSTIC_ROOT:
                    drop = ItemManager.createMysticRoot(); break;
                case STAR_FRUIT:
                    drop = ItemManager.createStarFruit(); break;
                case PREDATOR_FLOWER:
                    drop = ItemManager.createPredatorFlower(); break;
                case ELECTRO_PUMPKIN:
                    drop = ItemManager.createElectroPumpkin(); break;
                case MANDRAKE_LEAF:
                    drop = ItemManager.createMandrakeLeaf(); break;
                case FLYING_FRUIT:
                    drop = ItemManager.createFlyingFruit(); break;
                case SNOW_MINT:
                    drop = ItemManager.createSnowMint(); break;
                case SUN_PINEAPPLE:
                    drop = ItemManager.createSunPineapple(); break;
                case FOG_BERRY:
                    drop = ItemManager.createFogBerry(); break;
                case SAND_MELON:
                    drop = ItemManager.createSandMelon(); break;
                case WITCH_MUSHROOM:
                    drop = ItemManager.createWitchMushroom(); break;
                default: break;
            }
            
            // Дроп предмета
            if (drop != null) {
                int amount = 1;
                
                // Базовые бонусы
                if (crop.isFertilized()) { amount++; } // +1 предмет, если удобрено
                if (crop.isWatered()) { amount++; } // +1 предмет, если полито
                
                // Бонусы от событий
                double harvestMultiplier = plugin.getFarmingEventManager().getEventMultiplier(
                    com.github.d88a.farmereconomist.events.EventType.HARVEST_BOOST
                );
                amount = (int) (amount * harvestMultiplier);
                
                // Бонусы от сезонов для сезонных культур
                String cropTypeName = crop.getType().name();
                if (plugin.getSeasonManager().isSeasonalCrop(cropTypeName)) {
                    amount += 1; // +1 предмет для сезонных культур
                }
                
                // Бонусы от погоды
                if (plugin.getWeatherManager().getCurrentWeather() == 
                    com.github.d88a.farmereconomist.weather.WeatherManager.WeatherType.PERFECT) {
                    amount += 1; // +1 предмет при идеальной погоде
                }
                
                drop.setAmount(amount);
                location.getWorld().dropItemNaturally(location, drop);
                
                // Сбрасываем состояния после сбора урожая
                crop.setFertilized(false);
                crop.setWatered(false);
            }
            
            // Эффекты для некоторых культур
            switch (crop.getType()) {
                case LUNAR_BERRY:
                    location.getWorld().spawnParticle(org.bukkit.Particle.ENCHANT, location.clone().add(0.5, crop.getStage() + 0.5, 0.5), 20);
                    location.getWorld().playSound(location, org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
                    break;
                case RAINBOW_MUSHROOM:
                    location.getWorld().spawnParticle(org.bukkit.Particle.WITCH, location.clone().add(0.5, crop.getStage() + 0.5, 0.5), 20);
                    location.getWorld().playSound(location, org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                    break;
                case FLAME_PEPPER:
                    location.getWorld().spawnParticle(org.bukkit.Particle.FLAME, location.clone().add(0.5, crop.getStage() + 0.5, 0.5), 20);
                    location.getWorld().playSound(location, org.bukkit.Sound.ITEM_FIRECHARGE_USE, 1, 1);
                    break;
                case STAR_FRUIT:
                    location.getWorld().spawnParticle(org.bukkit.Particle.FIREWORK, location.clone().add(0.5, crop.getStage() + 0.5, 0.5), 20);
                    location.getWorld().playSound(location, org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
                    break;
                case FOG_BERRY:
                    location.getWorld().spawnParticle(org.bukkit.Particle.CLOUD, location.clone().add(0.5, crop.getStage() + 0.5, 0.5), 20);
                    break;
                case STRAWBERRY:
                    location.getWorld().spawnParticle(org.bukkit.Particle.HEART, location.clone().add(0.5, crop.getStage() + 0.5, 0.5), 10);
                    location.getWorld().playSound(location, org.bukkit.Sound.ENTITY_FOX_EAT, 1, 1);
                    break;
                case RADISH:
                    location.getWorld().spawnParticle(org.bukkit.Particle.CRIT, location.clone().add(0.5, crop.getStage() + 0.5, 0.5), 10);
                    location.getWorld().playSound(location, org.bukkit.Sound.ENTITY_RABBIT_HURT, 1, 1);
                    break;
                case WATERMELON:
                    location.getWorld().spawnParticle(org.bukkit.Particle.ITEM_SLIME, location.clone().add(0.5, crop.getStage() + 0.5, 0.5), 10);
                    location.getWorld().playSound(location, org.bukkit.Sound.ENTITY_PIG_AMBIENT, 1, 1);
                    break;
                case PREDATOR_FLOWER:
                    location.getWorld().spawnParticle(org.bukkit.Particle.ANGRY_VILLAGER, location.clone().add(0.5, crop.getStage() + 0.5, 0.5), 10);
                    location.getWorld().playSound(location, org.bukkit.Sound.ENTITY_RAVAGER_ROAR, 1, 1);
                    break;
                case MANDRAKE_LEAF:
                    location.getWorld().spawnParticle(org.bukkit.Particle.NOTE, location.clone().add(0.5, crop.getStage() + 0.5, 0.5), 10);
                    location.getWorld().playSound(location, org.bukkit.Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
                    break;
                case FLYING_FRUIT:
                    location.getWorld().spawnParticle(org.bukkit.Particle.PORTAL, location.clone().add(0.5, crop.getStage() + 0.5, 0.5), 10);
                    location.getWorld().playSound(location, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    break;
                case SNOW_MINT:
                    location.getWorld().spawnParticle(org.bukkit.Particle.SNOWFLAKE, location.clone().add(0.5, crop.getStage() + 0.5, 0.5), 10);
                    break;
                default: break;
            }
            
            // Удаляем растение из списка
            crops.remove(location);
            
            // Очищаем блоки
            clearCropColumn(location, crop.getType().getMaxStages());
        }
    }

    public CustomCrop getCropAt(Location location) {
        return crops.get(location);
    }

    private void startGrowthTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                for (CustomCrop crop : crops.values()) {
                    long effectiveGrowthInterval = GROWTH_INTERVAL;
                    
                    // Базовые модификаторы
                    double growthMultiplier = 1.0;
                    
                    // Ускорение роста от удобрения (например, 20% ускорение на 5 минут)
                    if (crop.isFertilized() && (currentTime - crop.getFertilizerTime() < 5 * 60 * 1000)) {
                        growthMultiplier *= 0.8; // Ускоряем на 20%
                    } else if (crop.isFertilized()) { // Если время удобрения истекло
                        crop.setFertilized(false);
                    }
                    
                    // Ускорение роста от полива (например, 20% ускорение на 5 минут)
                    if (crop.isWatered() && (currentTime - crop.getWateredTime() < 5 * 60 * 1000)) {
                        growthMultiplier *= 0.8; // Ускоряем на 20%
                    } else if (crop.isWatered()) { // Если время полива истекло
                        crop.setWatered(false);
                    }
                    
                    // Модификатор сезона
                    growthMultiplier *= plugin.getSeasonManager().getGrowthMultiplier();
                    
                    // Модификатор погоды
                    growthMultiplier *= plugin.getWeatherManager().getWeatherMultiplier();
                    
                    // Модификатор событий
                    growthMultiplier *= plugin.getFarmingEventManager().getEventMultiplier(
                        com.github.d88a.farmereconomist.events.EventType.GROWTH_BOOST
                    );
                    
                    // Специальные модификаторы для определенных культур
                    String cropTypeName = crop.getType().name();
                    if (plugin.getSeasonManager().isSeasonalCrop(cropTypeName)) {
                        growthMultiplier *= 1.5; // Сезонные культуры растут быстрее
                    }
                    
                    // Применяем общий модификатор
                    effectiveGrowthInterval = (long) (effectiveGrowthInterval * growthMultiplier);

                    if (currentTime - crop.getLastGrowthTime() > effectiveGrowthInterval) {
                        growCrop(crop);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L * 60); // Check every minute
    }

    private void growCrop(CustomCrop crop) {
        int maxStage = crop.getType().getMaxStages() - 1;
        if (crop.getStage() < maxStage) {
                crop.setStage(crop.getStage() + 1);
                crop.setLastGrowthTime(System.currentTimeMillis());
                updateCropBlock(crop);
            // Уникальные звуки при росте
            switch (crop.getType()) {
                case WITCH_MUSHROOM:
                    crop.getLocation().getWorld().playSound(crop.getLocation(), org.bukkit.Sound.BLOCK_PORTAL_AMBIENT, 1, 1);
                    break;
                case ELECTRO_PUMPKIN:
                    crop.getLocation().getWorld().playSound(crop.getLocation(), org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
                    break;
                case MYSTIC_ROOT:
                    crop.getLocation().getWorld().playSound(crop.getLocation(), org.bukkit.Sound.BLOCK_SOUL_SAND_BREAK, 1, 1);
                    break;
                case CRYSTAL_CACTUS:
                    crop.getLocation().getWorld().playSound(crop.getLocation(), org.bukkit.Sound.BLOCK_GLASS_BREAK, 1, 1);
                    break;
                case RAINBOW_MUSHROOM:
                    crop.getLocation().getWorld().playSound(crop.getLocation(), org.bukkit.Sound.BLOCK_AMETHYST_CLUSTER_BREAK, 1, 1);
                    break;
                case FLAME_PEPPER:
                    crop.getLocation().getWorld().playSound(crop.getLocation(), org.bukkit.Sound.BLOCK_FIRE_AMBIENT, 1, 1);
                    break;
                case STAR_FRUIT:
                    crop.getLocation().getWorld().playSound(crop.getLocation(), org.bukkit.Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1, 1);
                    break;
                case PREDATOR_FLOWER:
                    crop.getLocation().getWorld().playSound(crop.getLocation(), org.bukkit.Sound.ENTITY_STRIDER_DEATH, 1, 1);
                    break;
                case MANDRAKE_LEAF:
                    crop.getLocation().getWorld().playSound(crop.getLocation(), org.bukkit.Sound.BLOCK_ROOTED_DIRT_BREAK, 1, 1);
                    break;
                case FLYING_FRUIT:
                    crop.getLocation().getWorld().playSound(crop.getLocation(), org.bukkit.Sound.ENTITY_ALLAY_AMBIENT_WITH_ITEM, 1, 1);
                    break;
                case SNOW_MINT:
                    crop.getLocation().getWorld().playSound(crop.getLocation(), org.bukkit.Sound.BLOCK_POWDER_SNOW_STEP, 1, 1);
                    break;
                case SAND_MELON:
                    crop.getLocation().getWorld().playSound(crop.getLocation(), org.bukkit.Sound.BLOCK_GRAVEL_PLACE, 1, 1);
                    break;
                default: break;
            }
        }
    }

    private void updateCropBlock(CustomCrop crop) {
        Location loc = crop.getLocation();
        Block block = loc.getBlock();

        // Получаем голову для текущей стадии
        ItemStack head = ItemManager.getPlantStageHead(crop.getType(), crop.getStage());

        if (head != null) {
            block.setType(Material.PLAYER_HEAD, false); // Устанавливаем блок головы
            if (block.getState() instanceof Skull) {
                Skull skullState = (Skull) block.getState();
                SkullMeta headMeta = (SkullMeta) head.getItemMeta();
                if (headMeta != null && headMeta.hasOwnerProfile()) {
                    // Применяем профиль (текстуру) к блоку в мире
                    skullState.setPlayerProfile(headMeta.getPlayerProfile());
                }
                skullState.update();
            }
        }

        // Сюда можно добавить партиклы для определенных стадий/растений
        // Например: loc.getWorld().spawnParticle(...)
    }

    // Очищает столб растения (стебель + голову)
    private void clearCropColumn(Location base, int height) {
        // Теперь мы просто очищаем один блок
        base.getBlock().setType(Material.AIR);
    }
    
    public void saveCrops() {
        ConfigurationSection cropsSection = dataManager.getCropsConfig().createSection("crops");
        int i = 0;
        for (CustomCrop crop : crops.values()) {
            ConfigurationSection cropSection = cropsSection.createSection(String.valueOf(i++));
            cropSection.set("location", crop.getLocation());
            cropSection.set("type", crop.getType().name());
            cropSection.set("stage", crop.getStage());
            cropSection.set("last-growth", crop.getLastGrowthTime());
            cropSection.set("is-fertilized", crop.isFertilized());
            cropSection.set("fertilizer-time", crop.getFertilizerTime());
            cropSection.set("is-watered", crop.isWatered());
            cropSection.set("watered-time", crop.getWateredTime());
        }
        dataManager.saveCropsConfig();
    }

    private void loadCrops() {
        ConfigurationSection cropsSection = dataManager.getCropsConfig().getConfigurationSection("crops");
        if (cropsSection != null) {
            for (String key : cropsSection.getKeys(false)) {
                Location loc = cropsSection.getLocation(key + ".location");
                CustomCrop.CropType type = CustomCrop.CropType.valueOf(cropsSection.getString(key + ".type"));
                CustomCrop crop = new CustomCrop(loc, type);
                crop.setStage(cropsSection.getInt(key + ".stage"));
                crop.setLastGrowthTime(cropsSection.getLong(key + ".last-growth"));
                crop.setFertilized(cropsSection.getBoolean(key + ".is-fertilized"));
                crop.setFertilizerTime(cropsSection.getLong(key + ".fertilizer-time"));
                crop.setWatered(cropsSection.getBoolean(key + ".is-watered"));
                crop.setWateredTime(cropsSection.getLong(key + ".watered-time"));
                crops.put(loc, crop);
            }
        }
    }
} 