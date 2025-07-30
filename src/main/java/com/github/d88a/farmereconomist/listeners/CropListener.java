package com.github.d88a.farmereconomist.listeners;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.crops.CropManager;
import com.github.d88a.farmereconomist.crops.CustomCrop;
import com.github.d88a.farmereconomist.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CropListener implements Listener {

    private final FarmerEconomist plugin;
    private final CropManager cropManager;

    public CropListener(FarmerEconomist plugin) {
        this.plugin = plugin;
        this.cropManager = plugin.getCropManager();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
        
        if (itemInHand.isSimilar(ItemManager.createTomatoSeeds())) {
            handleTomatoPlacement(event);
        } else if (itemInHand.isSimilar(ItemManager.createGlowshroomSpores())) {
            handleGlowshroomPlacement(event);
        } else if (itemInHand.isSimilar(ItemManager.createStrawberrySeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.STRAWBERRY);
        } else if (itemInHand.isSimilar(ItemManager.createRadishSeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.RADISH);
        } else if (itemInHand.isSimilar(ItemManager.createWatermelonSeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.WATERMELON);
        } else if (itemInHand.isSimilar(ItemManager.createLunarBerrySeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.LUNAR_BERRY);
        } else if (itemInHand.isSimilar(ItemManager.createRainbowMushroomSeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.RAINBOW_MUSHROOM);
        } else if (itemInHand.isSimilar(ItemManager.createCrystalCactusSeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.CRYSTAL_CACTUS);
        } else if (itemInHand.isSimilar(ItemManager.createFlamePepperSeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.FLAME_PEPPER);
        } else if (itemInHand.isSimilar(ItemManager.createMysticRootSeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.MYSTIC_ROOT);
        } else if (itemInHand.isSimilar(ItemManager.createStarFruitSeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.STAR_FRUIT);
        } else if (itemInHand.isSimilar(ItemManager.createPredatorFlowerSeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.PREDATOR_FLOWER);
        } else if (itemInHand.isSimilar(ItemManager.createElectroPumpkinSeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.ELECTRO_PUMPKIN);
        } else if (itemInHand.isSimilar(ItemManager.createMandrakeLeafSeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.MANDRAKE_LEAF);
        } else if (itemInHand.isSimilar(ItemManager.createFlyingFruitSeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.FLYING_FRUIT);
        } else if (itemInHand.isSimilar(ItemManager.createSnowMintSeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.SNOW_MINT);
        } else if (itemInHand.isSimilar(ItemManager.createSunPineappleSeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.SUN_PINEAPPLE);
        } else if (itemInHand.isSimilar(ItemManager.createFogBerrySeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.FOG_BERRY);
        } else if (itemInHand.isSimilar(ItemManager.createSandMelonSeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.SAND_MELON);
        } else if (itemInHand.isSimilar(ItemManager.createWitchMushroomSeeds())) {
            handleCropPlacement(event, CustomCrop.CropType.WITCH_MUSHROOM);
        }
    }

    private void handleTomatoPlacement(BlockPlaceEvent event) {
        Block placedOn = event.getBlock().getRelative(0, -1, 0);
        if (placedOn.getType() == Material.FARMLAND) {
            event.setCancelled(true);
            cropManager.plantCrop(event.getBlock().getLocation(), CustomCrop.CropType.TOMATO);
            event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
        } else {
            plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_plant_fail_not_farmland");
            event.setCancelled(true);
        }
    }

    private void handleGlowshroomPlacement(BlockPlaceEvent event) {
        Block placedOn = event.getBlock().getRelative(0, -1, 0);
        if (placedOn.getType() == Material.MYCELIUM || placedOn.getType() == Material.PODZOL) {
            event.setCancelled(true);
            cropManager.plantCrop(event.getBlock().getLocation(), CustomCrop.CropType.GLOWSHROOM);
            event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
        } else {
            plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_plant_fail_not_mycelium");
            event.setCancelled(true);
        }
    }

    private void handleCropPlacement(BlockPlaceEvent event, CustomCrop.CropType cropType) {
        Block placedOn = event.getBlock().getRelative(0, -1, 0);
        
        // Проверяем подходящую почву для разных культур
        boolean validSoil = false;
        switch (cropType) {
            case CRYSTAL_CACTUS:
            case SAND_MELON:
                validSoil = placedOn.getType() == Material.SAND;
                break;
            case FLAME_PEPPER:
                validSoil = placedOn.getType() == Material.NETHERRACK;
                break;
            case WITCH_MUSHROOM:
                validSoil = placedOn.getType() == Material.NETHERRACK;
                break;
            case SNOW_MINT:
                validSoil = placedOn.getType() == Material.SNOW_BLOCK || placedOn.getType() == Material.ICE;
                break;
            default:
                validSoil = placedOn.getType() == Material.FARMLAND || placedOn.getType() == Material.DIRT || placedOn.getType() == Material.GRASS_BLOCK;
                break;
        }
        
        if (validSoil) {
            event.setCancelled(true);
            cropManager.plantCrop(event.getBlock().getLocation(), cropType);
            event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
        } else {
            plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_plant_fail_not_farmland");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        // Проверяем, есть ли растение на этом блоке
        CustomCrop crop = cropManager.getCropAt(clickedBlock.getLocation());
        
        // --- Обработка правой кнопки мыши (использование лейки/удобрения) ---
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
            
            // Использование лейки
            if (itemInHand.isSimilar(ItemManager.createWateringCan())) {
                if (crop != null && !crop.isWatered()) {
                    crop.setWatered(true);
                    crop.setWateredTime(System.currentTimeMillis());
                    plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_watered");
                    plugin.getSoundManager().playSound(event.getPlayer(), "water_crop");
                    event.setCancelled(true);
                } else if (crop != null && crop.isWatered()) {
                    plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_already_watered");
                    event.setCancelled(true);
                }
                return;
            }
            
            // Использование удобрения
            if (itemInHand.isSimilar(ItemManager.createFertilizer())) {
                if (crop != null && !crop.isFertilized()) {
                    crop.setFertilized(true);
                    crop.setFertilizerTime(System.currentTimeMillis());
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                    plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_fertilized");
                    plugin.getSoundManager().playSound(event.getPlayer(), "fertilize_crop");
                    event.setCancelled(true);
                } else if (crop != null && crop.isFertilized()) {
                    plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_already_fertilized");
                    event.setCancelled(true);
                }
                return;
            }
        }

        // --- Обработка левой кнопки мыши (сбор урожая) ---
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && crop != null) {
            boolean isHarvestable = false;
            // Теперь проверяем, если растение достигло последней стадии
            if(crop.getStage() == (crop.getType().getMaxStages() - 1)) {
                isHarvestable = true;
            }
            if (isHarvestable) {
                cropManager.harvestCrop(clickedBlock.getLocation());
                plugin.getSoundManager().playSound(event.getPlayer(), "harvest_crop");
            } else {
                plugin.getConfigManager().sendMessage(event.getPlayer(), "crop_not_ready");
            }
            event.setCancelled(true);
        }
    }
} 