package com.github.d88a.farmereconomist.crops;

import com.github.d88a.farmereconomist.items.ItemManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class CustomCrop {

    private final Location location;
    private final CropType type;
    private int stage;
    private long lastGrowthTime;
    private boolean isFertilized;
    private long fertilizerTime;
    private boolean isWatered;
    private long wateredTime;
    private int harvestsLeft;

    public CustomCrop(Location location, CropType type) {
        this.location = location;
        this.type = type;
        this.stage = 0;
        this.lastGrowthTime = System.currentTimeMillis();
        this.harvestsLeft = type.getMaxHarvests();
    }

    // Getters
    public Location getLocation() { return location; }
    public CropType getType() { return type; }
    public int getStage() { return stage; }
    public long getLastGrowthTime() { return lastGrowthTime; }
    public boolean isFertilized() { return isFertilized; }
    public long getFertilizerTime() { return fertilizerTime; }
    public boolean isWatered() { return isWatered; }
    public long getWateredTime() { return wateredTime; }
    public int getHarvestsLeft() { return harvestsLeft; }
    public boolean isMultiHarvest() { return type.getMaxHarvests() > 1; }

    // Setters
    public void setStage(int stage) { this.stage = stage; }
    public void setLastGrowthTime(long lastGrowthTime) { this.lastGrowthTime = lastGrowthTime; }
    public void setFertilized(boolean fertilized) { this.isFertilized = fertilized; }
    public void setFertilizerTime(long fertilizerTime) { this.fertilizerTime = fertilizerTime; }
    public void setWatered(boolean watered) { this.isWatered = watered; }
    public void setWateredTime(long wateredTime) { this.wateredTime = wateredTime; }
    public void decrementHarvests() { this.harvestsLeft--; }

    public enum CropType {
        // --- Vanilla-like ---
        LETTUCE("Скромный Латук", 2, 1, "LETTUCE_SEEDS", Arrays.asList(Material.FARMLAND), () -> ItemManager.createLettuce(false),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19"),
        TOMATO("Рубиновый Томат", 2, 1, "TOMATO_SEEDS", Arrays.asList(Material.FARMLAND), ItemManager::createTomato,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjYyNjUxODc5M2I5YjU4YjI2ODk3MjY2YjM0MTc4M2U0YjJjN2Y3YjFlNWYxODRjNWIxNGViZTY4NzQzNmUifX19"),
        GLOWSHROOM("Светящийся Гриб", 2, 1, "GLOWSHROOM_SPORES", Arrays.asList(Material.MYCELIUM, Material.PODZOL), ItemManager::createGlowshroomDust,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjYyNjUxODc5M2I5YjU4YjI2ODk3MjY2YjM0MTc4M2U0YjJjN2Y3YjFlNWYxODRjNWIxNGViZTY4NzQzNmUifX19"),
        STRAWBERRY("Лучезарная Клубника", 2, 3, "STRAWBERRY_SEEDS", Arrays.asList(Material.FARMLAND), ItemManager::createStrawberry,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19"),
        RADISH("Хрустящий Редис", 2, 1, "RADISH_SEEDS", Arrays.asList(Material.FARMLAND), ItemManager::createRadish,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19"),
        WATERMELON("Пустынный Арбуз", 2, 1, "WATERMELON_SEEDS", Arrays.asList(Material.FARMLAND), ItemManager::createWatermelon,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjYyNjUxODc5M2I5YjU4YjI2ODk3MjY2YjM0MTc4M2U0YjJjN2Y3YjFlNWYxODRjNWIxNGViZTY4NzQzNmUifX19"),

        // --- Magical ---
        LUNAR_BERRY("Лунная Ягода", 3, 3, "LUNAR_BERRY_SEEDS", Arrays.asList(Material.FARMLAND), ItemManager::createLunarBerry,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjYyNjUxODc5M2I5YjU4YjI2ODk3MjY2YjM0MTc4M2U0YjJjN2Y3YjFlNWYxODRjNWIxNGViZTY4NzQzNmUifX19"),
        RAINBOW_MUSHROOM("Радужный Гриб", 2, 1, "RAINBOW_MUSHROOM_SEEDS", Arrays.asList(Material.MYCELIUM, Material.PODZOL), ItemManager::createRainbowMushroom,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19"),
        CRYSTAL_CACTUS("Кристальный Кактус", 3, 2, "CRYSTAL_CACTUS_SEEDS", Arrays.asList(Material.SAND), ItemManager::createCrystalCactus,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjYyNjUxODc5M2I5YjU4YjI2ODk3MjY2YjM0MTc4M2U0YjJjN2Y3YjFlNWYxODRjNWIxNGViZTY4NzQzNmUifX19"),
        FLAME_PEPPER("Пылающий Перец", 2, 1, "FLAME_PEPPER_SEEDS", Arrays.asList(Material.NETHERRACK), ItemManager::createFlamePepper,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19"),
        MYSTIC_ROOT("Мистический Корень", 3, 2, "MYSTIC_ROOT_SEEDS", Arrays.asList(Material.DIRT), ItemManager::createMysticRoot,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjYyNjUxODc5M2I5YjU4YjI2ODk3MjY2YjM0MTc4M2U0YjJjN2Y3YjFlNWYxODRjNWIxNGViZTY4NzQzNmUifX19"),
        STAR_FRUIT("Звёздный Плод", 2, 2, "STAR_FRUIT_SEEDS", Arrays.asList(Material.DIRT), ItemManager::createStarFruit,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19"),
        PREDATOR_FLOWER("Цветок-Хищник", 3, 1, "PREDATOR_FLOWER_SEEDS", Arrays.asList(Material.DIRT), ItemManager::createPredatorFlower,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjYyNjUxODc5M2I5YjU4YjI2ODk3MjY2YjM0MTc4M2U0YjJjN2Y3YjFlNWYxODRjNWIxNGViZTY4NzQzNmUifX19"),
        ELECTRO_PUMPKIN("Электро-Тыква", 2, 2, "ELECTRO_PUMPKIN_SEEDS", Arrays.asList(Material.DIRT), ItemManager::createElectroPumpkin,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19"),
        MANDRAKE_LEAF("Листья Мандрагоры", 2, 1, "MANDRAKE_LEAF_SEEDS", Arrays.asList(Material.DIRT), ItemManager::createMandrakeLeaf,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19"),
        FLYING_FRUIT("Летающий Плод", 2, 1, "FLYING_FRUIT_SEEDS", Arrays.asList(Material.DIRT), ItemManager::createFlyingFruit,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19"),
        SNOW_MINT("Снежная Мята", 2, 1, "SNOW_MINT_SEEDS", Arrays.asList(Material.SNOW_BLOCK), ItemManager::createSnowMint,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19"),
        SUN_PINEAPPLE("Солнечный Ананас", 3, 1, "SUN_PINEAPPLE_SEEDS", Arrays.asList(Material.DIRT), ItemManager::createSunPineapple,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjYyNjUxODc5M2I5YjU4YjI2ODk3MjY2YjM0MTc4M2U0YjJjN2Y3YjFlNWYxODRjNWIxNGViZTY4NzQzNmUifX19"),
        FOG_BERRY("Туманная Ягода", 2, 2, "FOG_BERRY_SEEDS", Arrays.asList(Material.DIRT), ItemManager::createFogBerry,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19"),
        SAND_MELON("Песчаный Арбуз", 2, 1, "SAND_MELON_SEEDS", Arrays.asList(Material.SAND), ItemManager::createSandMelon,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19"),
        WITCH_MUSHROOM("Ведьмин Гриб", 2, 1, "WITCH_MUSHROOM_SEEDS", Arrays.asList(Material.NETHERRACK), ItemManager::createWitchMushroom,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBkNzZlYjJjN2M5MjI1ZGIyZDE4M2Y5YjM5N2YyYjE3YjYyMDIxYjM3ZDE3OWY5YjgzM2QxYjYxYjVhIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5YjE4YzA3NmFkY2M0Y2YxODRmNzVjMmM1N2YxNDYxMGEyZTY0YjFjYjI3YjY5ZTM0ZDEyZjg1Y2Q0ZTcifX19");

        private final String displayName;
        private final int maxStages;
        private final int maxHarvests;
        private final String seedItemId;
        private final List<Material> plantableOn;
        private final Supplier<ItemStack> drop;
        private final String[] textures;

        CropType(String displayName, int maxStages, int maxHarvests, String seedItemId, List<Material> plantableOn, Supplier<ItemStack> drop, String... textures) {
            this.displayName = displayName;
            this.maxStages = maxStages;
            this.maxHarvests = maxHarvests;
            this.seedItemId = seedItemId;
            this.plantableOn = plantableOn;
            this.drop = drop;
            this.textures = textures;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getMaxStages() {
            return maxStages;
        }

        public int getMaxHarvests() {
            return maxHarvests;
        }

        public String getSeedItemId() {
            return seedItemId;
        }

        public boolean canPlantOn(Material material) {
            return plantableOn.contains(material);
        }

        public Supplier<ItemStack> getDrop() {
            return drop;
        }

        public String getTextureForStage(int stage) {
            if (stage >= 0 && stage < textures.length) {
                return textures[stage];
            }
            return null;
        }

        public static CropType fromSeedId(String seedId) {
            for (CropType type : values()) {
                if (type.getSeedItemId().equals(seedId)) {
                    return type;
                }
            }
            return null;
        }
    }
} 