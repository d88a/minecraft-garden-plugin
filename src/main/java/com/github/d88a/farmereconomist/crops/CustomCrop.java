package com.github.d88a.farmereconomist.crops;

import org.bukkit.Location;
import org.bukkit.Material;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomCrop {

    public enum CropType {
        TOMATO("Рубиновый Томат", "TOMATO", false, 1, 2, Material.OAK_FENCE, Arrays.asList(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjVlZDRiYjM0ZDRlY2YyYjBlY2MwYmYxY2RkYjYxYjI3OWY0NzYyYjM5YjQ4ZGY0ZGNkY2I0YjYyYjlkY2YxIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjYyYmM3YjM5Y2E5YzA0NDc0N2QxYjI0Y2I5MDUxODcxYjY4YjY3ZTk0ZGY2Y2E0MGMxZmU5ZDUxM2U2M2UifX19"
        ), Material.FARMLAND),
        GLOWSHROOM("Светящийся Гриб", "GLOWSHROOM_DUST", false, 1, 2, Material.BIRCH_FENCE, Collections.emptyList(), Material.MYCELIUM, Material.PODZOL),
        STRAWBERRY("Лучезарная Клубника", "STRAWBERRY", true, 3, 2, Material.JUNGLE_FENCE, Collections.emptyList(), Material.FARMLAND),
        RADISH("Хрустящий Редис", "RADISH", false, 1, 2, Material.OAK_FENCE, Collections.emptyList(), Material.FARMLAND),
        WATERMELON("Пустынный Арбуз", "WATERMELON", true, 2, 2, Material.MELON, Collections.emptyList(), Material.FARMLAND),
        LUNAR_BERRY("Лунная Ягода", "LUNAR_BERRY", true, 3, 3, Material.END_ROD, Collections.emptyList(), Material.FARMLAND),
        RAINBOW_MUSHROOM("Радужный Гриб", "RAINBOW_MUSHROOM", false, 1, 2, Material.WARPED_FENCE, Collections.emptyList(), Material.MYCELIUM, Material.PODZOL),
        CRYSTAL_CACTUS("Кристальный Кактус", "CRYSTAL_CACTUS", true, 2, 3, Material.CACTUS, Collections.emptyList(), Material.SAND),
        FLAME_PEPPER("Пылающий Перец", "FLAME_PEPPER", false, 1, 2, Material.NETHER_BRICK_FENCE, Collections.emptyList(), Material.NETHERRACK),
        MYSTIC_ROOT("Мистический Корень", "MYSTIC_ROOT", true, 2, 3, Material.WARPED_FENCE, Collections.emptyList(), Material.FARMLAND),
        STAR_FRUIT("Звёздный Плод", "STAR_FRUIT", true, 2, 2, Material.BAMBOO, Collections.emptyList(), Material.FARMLAND),
        PREDATOR_FLOWER("Цветок-Хищник", "PREDATOR_FLOWER", false, 1, 3, Material.DARK_OAK_FENCE, Collections.emptyList(), Material.FARMLAND),
        ELECTRO_PUMPKIN("Электро-Тыква", "ELECTRO_PUMPKIN", true, 2, 2, Material.LIGHTNING_ROD, Arrays.asList(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmE2MDQ2YjE5YjI3YjQxYTQyODQxNDFhYjQyZDNkY2YxMTk5ZGU3Mjk2YjY0YjE2ZWIzY2YxYjY0Zjc4YjNmIn19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Y5YjE4Y2Y2MTQzNmQ3YjE4YTgwYjA4MjY3ODg2ZDIxYjQ1YjU3YjE1Y2YyYjQ1Y2E0YjYyYjlkY2YxIn19fQ=="
        ), Material.FARMLAND),
        MANDRAKE_LEAF("Листья Мандрагоры", "MANDRAKE_LEAF", false, 1, 2, Material.AZALEA_LEAVES, Collections.emptyList(), Material.FARMLAND),
        FLYING_FRUIT("Летающий Плод", "FLYING_FRUIT", true, 2, 2, Material.BIRCH_FENCE, Collections.emptyList(), Material.FARMLAND),
        SNOW_MINT("Снежная Мята", "SNOW_MINT", false, 1, 2, Material.SNOW_BLOCK, Collections.emptyList(), Material.SNOW_BLOCK),
        SUN_PINEAPPLE("Солнечный Ананас", "SUN_PINEAPPLE", true, 2, 3, Material.BAMBOO, Collections.emptyList(), Material.FARMLAND),
        FOG_BERRY("Туманная Ягода", "FOG_BERRY", false, 1, 2, Material.COBWEB, Collections.emptyList(), Material.FARMLAND),
        SAND_MELON("Песчаный Арбуз", "SAND_MELON", true, 2, 2, Material.SANDSTONE, Collections.emptyList(), Material.SAND),
        WITCH_MUSHROOM("Ведьмин Гриб", "WITCH_MUSHROOM", false, 1, 2, Material.MANGROVE_FENCE, Collections.emptyList(), Material.NETHERRACK);

        private final String displayName;
        private final String dropItemId;
        private final boolean multiHarvest;
        private final int defaultHarvests;
        private final int maxStages;
        private final Material stemMaterial;
        private final List<String> stageTextures;
        private final List<Material> plantableOn;

        CropType(String displayName, String dropItemId, boolean multiHarvest, int defaultHarvests, int maxStages, Material stemMaterial, List<String> stageTextures, Material... plantableOn) {
            this.displayName = displayName;
            this.dropItemId = dropItemId;
            this.multiHarvest = multiHarvest;
            this.defaultHarvests = defaultHarvests;
            this.maxStages = maxStages;
            this.stemMaterial = stemMaterial;
            this.stageTextures = stageTextures;
            this.plantableOn = Arrays.asList(plantableOn);
        }

        public String getDisplayName() { return displayName; }
        public String getDropItemId() { return dropItemId; }
        public boolean isMultiHarvest() { return multiHarvest; }
        public int getDefaultHarvests() { return defaultHarvests; }
        public int getMaxStages() { return maxStages; }
        public Material getStemMaterial() { return stemMaterial; }
        public boolean canPlantOn(Material material) {
            return this.plantableOn.contains(material);
        }
        public String getTextureForStage(int stage) {
            if (stageTextures != null && !stageTextures.isEmpty() && stage < stageTextures.size()) {
                return stageTextures.get(stage);
            }
            return null;
        }
    }

    private final Location location;
    private final CropType type;
    private int stage;
    private long lastGrowthTime;
    private int harvestsLeft;
    private boolean isFertilized;
    private long fertilizerTime;
    private boolean isWatered;
    private long wateredTime;

    public CustomCrop(Location location, CropType type) {
        this.location = location;
        this.type = type;
        this.stage = 0;
        this.lastGrowthTime = System.currentTimeMillis();
        this.harvestsLeft = type.getDefaultHarvests();
        this.isFertilized = false;
        this.fertilizerTime = 0;
        this.isWatered = false;
        this.wateredTime = 0;
    }

    public Location getLocation() {
        return location;
    }

    public CropType getType() {
        return type;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public long getLastGrowthTime() {
        return lastGrowthTime;
    }

    public void setLastGrowthTime(long lastGrowthTime) {
        this.lastGrowthTime = lastGrowthTime;
    }

    public boolean isFertilized() { return isFertilized; }
    public void setFertilized(boolean fertilized) { this.isFertilized = fertilized; }
    public long getFertilizerTime() { return fertilizerTime; }
    public void setFertilizerTime(long fertilizerTime) { this.fertilizerTime = fertilizerTime; }

    public boolean isWatered() { return isWatered; }
    public void setWatered(boolean watered) { this.isWatered = watered; }
    public long getWateredTime() { return wateredTime; }
    public void setWateredTime(long wateredTime) { this.wateredTime = wateredTime; }

    public int getHarvestsLeft() { return harvestsLeft; }
    public void setHarvestsLeft(int left) { this.harvestsLeft = left; }
    public void decrementHarvests() { this.harvestsLeft--; }
    public boolean isMultiHarvest() { return type.isMultiHarvest(); }
} 