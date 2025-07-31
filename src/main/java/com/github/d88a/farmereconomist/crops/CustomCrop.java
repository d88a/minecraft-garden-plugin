package com.github.d88a.farmereconomist.crops;

import org.bukkit.Location;
import org.bukkit.Material;
import java.util.Arrays;
import java.util.List;

public class CustomCrop {

    public enum CropType {
        TOMATO("TOMATO", false, 1, 2, Material.OAK_FENCE, Material.FARMLAND),
        GLOWSHROOM("GLOWSHROOM_DUST", false, 1, 2, Material.BIRCH_FENCE, Material.MYCELIUM, Material.PODZOL),
        STRAWBERRY("STRAWBERRY", true, 3, 2, Material.JUNGLE_FENCE, Material.FARMLAND),
        RADISH("RADISH", false, 1, 2, Material.OAK_FENCE, Material.FARMLAND),
        WATERMELON("WATERMELON", true, 2, 2, Material.MELON, Material.FARMLAND),
        LUNAR_BERRY("LUNAR_BERRY", true, 3, 3, Material.END_ROD, Material.FARMLAND),
        RAINBOW_MUSHROOM("RAINBOW_MUSHROOM", false, 1, 2, Material.WARPED_FENCE, Material.MYCELIUM, Material.PODZOL),
        CRYSTAL_CACTUS("CRYSTAL_CACTUS", true, 2, 3, Material.CACTUS, Material.SAND),
        FLAME_PEPPER("FLAME_PEPPER", false, 1, 2, Material.NETHER_BRICK_FENCE, Material.NETHERRACK),
        MYSTIC_ROOT("MYSTIC_ROOT", true, 2, 3, Material.WARPED_FENCE, Material.FARMLAND),
        STAR_FRUIT("STAR_FRUIT", true, 2, 2, Material.BAMBOO, Material.FARMLAND),
        PREDATOR_FLOWER("PREDATOR_FLOWER", false, 1, 3, Material.DARK_OAK_FENCE, Material.FARMLAND),
        ELECTRO_PUMPKIN("ELECTRO_PUMPKIN", true, 2, 2, Material.LIGHTNING_ROD, Material.FARMLAND),
        MANDRAKE_LEAF("MANDRAKE_LEAF", false, 1, 2, Material.AZALEA_LEAVES, Material.FARMLAND),
        FLYING_FRUIT("FLYING_FRUIT", true, 2, 2, Material.BIRCH_FENCE, Material.FARMLAND),
        SNOW_MINT("SNOW_MINT", false, 1, 2, Material.SNOW_BLOCK, Material.SNOW_BLOCK),
        SUN_PINEAPPLE("SUN_PINEAPPLE", true, 2, 3, Material.BAMBOO, Material.FARMLAND),
        FOG_BERRY("FOG_BERRY", false, 1, 2, Material.COBWEB, Material.FARMLAND),
        SAND_MELON("SAND_MELON", true, 2, 2, Material.SANDSTONE, Material.SAND),
        WITCH_MUSHROOM("WITCH_MUSHROOM", false, 1, 2, Material.MANGROVE_FENCE, Material.NETHERRACK);

        private final String dropItemId;
        private final boolean multiHarvest;
        private final int defaultHarvests;
        private final int maxStages;
        private final Material stemMaterial;
        private final List<Material> plantableOn;

        CropType(String dropItemId, boolean multiHarvest, int defaultHarvests, int maxStages, Material stemMaterial, Material... plantableOn) {
            this.dropItemId = dropItemId;
            this.multiHarvest = multiHarvest;
            this.defaultHarvests = defaultHarvests;
            this.maxStages = maxStages;
            this.stemMaterial = stemMaterial;
            this.plantableOn = Arrays.asList(plantableOn);
        }

        public String getDropItemId() { return dropItemId; }
        public boolean isMultiHarvest() { return multiHarvest; }
        public int getDefaultHarvests() { return defaultHarvests; }
        public int getMaxStages() { return maxStages; }
        public Material getStemMaterial() { return stemMaterial; }
        public boolean canPlantOn(Material material) {
            return this.plantableOn.contains(material);
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