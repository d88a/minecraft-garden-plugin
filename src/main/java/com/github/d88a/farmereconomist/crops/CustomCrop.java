package com.github.d88a.farmereconomist.crops;

import org.bukkit.Location;

public class CustomCrop {

    public enum CropType {
        TOMATO("TOMATO", false, 1, 2, org.bukkit.Material.OAK_FENCE), // 2 стадии (0 и 1)
        GLOWSHROOM("GLOWSHROOM_DUST", false, 1, 2, org.bukkit.Material.BIRCH_FENCE), // 2 стадии
        STRAWBERRY("STRAWBERRY", true, 3, 2, org.bukkit.Material.JUNGLE_FENCE), // 2 стадии
        RADISH("RADISH", false, 1, 2, org.bukkit.Material.OAK_FENCE),
        WATERMELON("WATERMELON", true, 2, 2, org.bukkit.Material.MELON),
        LUNAR_BERRY("LUNAR_BERRY", true, 3, 3, org.bukkit.Material.END_ROD), // 3 стадии
        RAINBOW_MUSHROOM("RAINBOW_MUSHROOM", false, 1, 2, org.bukkit.Material.WARPED_FENCE),
        CRYSTAL_CACTUS("CRYSTAL_CACTUS", true, 2, 3, org.bukkit.Material.CACTUS),
        FLAME_PEPPER("FLAME_PEPPER", false, 1, 2, org.bukkit.Material.NETHER_BRICK_FENCE),
        MYSTIC_ROOT("MYSTIC_ROOT", true, 2, 3, org.bukkit.Material.WARPED_FENCE),
        STAR_FRUIT("STAR_FRUIT", true, 2, 2, org.bukkit.Material.BAMBOO),
        PREDATOR_FLOWER("PREDATOR_FLOWER", false, 1, 3, org.bukkit.Material.DARK_OAK_FENCE),
        ELECTRO_PUMPKIN("ELECTRO_PUMPKIN", true, 2, 2, org.bukkit.Material.LIGHTNING_ROD),
        MANDRAKE_LEAF("MANDRAKE_LEAF", false, 1, 2, org.bukkit.Material.AZALEA_LEAVES),
        FLYING_FRUIT("FLYING_FRUIT", true, 2, 2, org.bukkit.Material.BIRCH_FENCE),
        SNOW_MINT("SNOW_MINT", false, 1, 2, org.bukkit.Material.SNOW_BLOCK),
        SUN_PINEAPPLE("SUN_PINEAPPLE", true, 2, 3, org.bukkit.Material.BAMBOO),
        FOG_BERRY("FOG_BERRY", false, 1, 2, org.bukkit.Material.COBWEB),
        SAND_MELON("SAND_MELON", true, 2, 2, org.bukkit.Material.SANDSTONE),
        WITCH_MUSHROOM("WITCH_MUSHROOM", false, 1, 2, org.bukkit.Material.MANGROVE_FENCE);

        private final String dropItemId;
        private final boolean multiHarvest;
        private final int defaultHarvests;
        private final int maxStages;
        private final org.bukkit.Material stemMaterial;

        CropType(String dropItemId, boolean multiHarvest, int defaultHarvests, int maxStages, org.bukkit.Material stemMaterial) {
            this.dropItemId = dropItemId;
            this.multiHarvest = multiHarvest;
            this.defaultHarvests = defaultHarvests;
            this.maxStages = maxStages;
            this.stemMaterial = stemMaterial;
        }

        public String getDropItemId() { return dropItemId; }
        public boolean isMultiHarvest() { return multiHarvest; }
        public int getDefaultHarvests() { return defaultHarvests; }
        public int getMaxStages() { return maxStages; }
        public org.bukkit.Material getStemMaterial() { return stemMaterial; }
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