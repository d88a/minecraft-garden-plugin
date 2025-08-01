package com.github.d88a.farmereconomist.items;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.crops.CustomCrop;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.UUID;
import org.bukkit.Bukkit;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.NamespacedKey;
import java.lang.reflect.Field;

public class ItemManager {

    // Ключ для хранения уникального ID предмета в PersistentDataContainer
    public static NamespacedKey ITEM_ID_KEY;

    // Этот метод нужно вызвать один раз при запуске плагина, например в onEnable()
    public static void init(FarmerEconomist plugin) {
        ITEM_ID_KEY = new NamespacedKey(plugin, "item_id");
    }


    public static ItemStack createWateringCan() {
        ItemStack item = new ItemStack(Material.IRON_HOE); // Используем мотыгу как основу
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bЛейка");
        meta.setLore(Arrays.asList("§7Наполните водой, чтобы поливать растения.", "§7Использование: ПКМ по растению."));
        meta.setCustomModelData(1); // Уникальный ID для ресурс-пака
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "WATERING_CAN");
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createFertilizer() {
        ItemStack item = new ItemStack(Material.BONE_MEAL); // Используем костную муку как основу
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aУдобрение");
        meta.setLore(Arrays.asList("§7Ускоряет рост растений.", "§7Использование: ПКМ по растению."));
        meta.setCustomModelData(1); // Если есть ресурс-пак, использовать другой ID
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "FERTILIZER");
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createLettuceSeeds() {
        return createSeed(Material.WHEAT_SEEDS, "§aСемена Скромного Латука", "LETTUCE_SEEDS", 1, "Можно посадить на вспаханную землю.");
    }
    
    public static ItemStack createTomatoSeeds() {
        return createSeed(Material.BEETROOT_SEEDS, "§cСемена Рубинового Томата", "TOMATO_SEEDS", 0, "Можно посадить на вспаханную землю.");
    }

    public static ItemStack createGlowshroomSpores() {
        return createSeed(Material.RED_MUSHROOM, "§dСпоры светящегося гриба", "GLOWSHROOM_SPORES", 0, "Можно посадить на мицелий или подзол.");
    }

    public static ItemStack createGlowshroomDust() {
        ItemStack item = new ItemStack(Material.GLOWSTONE_DUST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§dПыльца светящегося гриба");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "GLOWSHROOM_DUST");
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createTomato() {
        String texture = CustomCrop.CropType.TOMATO.getTextureForStage(CustomCrop.CropType.TOMATO.getMaxStages() - 1);
        return createCustomHead("TOMATO", texture, "§cРубиновый Томат", "Сочный и спелый!");
    }

    /**
     * Возвращает голову (блок) для определенной стадии роста растения.
     * @param crop Растение
     * @return ItemStack с кастомной головой
     */
    public static ItemStack getPlantStageHead(CustomCrop crop) {
        CustomCrop.CropType type = crop.getType();
        int stage = crop.getStage();
        String texture = type.getTextureForStage(stage);
        if (texture == null) {
            return null; // Не удалось найти текстуру для стадии
        }

        String name = (stage < type.getMaxStages() - 1) ? "Росток " + type.getDisplayName() : type.getDisplayName();
        return createHeadFromTexture(texture, name);
    }

    public static ItemStack createLettuce(boolean isWatered) {
        ItemStack item = createCustomHead("LETTUCE_ITEM", CustomCrop.CropType.LETTUCE.getTextureForStage(1), "§2Скромный Латук");
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§2Скромный Латук");
        if (isWatered) {
            meta.setLore(Arrays.asList("Качество: §aОтличное"));
            meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "LETTUCE_EXCELLENT");
            meta.setCustomModelData(101); // Модель для качественного латука
        } else {
            meta.setLore(Arrays.asList("Качество: §eОбычное"));
            meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "LETTUCE_NORMAL");
            meta.setCustomModelData(100); // Модель для обычного латука
        }
        item.setItemMeta(meta);
        return item;
    }

    // --- Методы для генерации ItemStack для каждого растения ---
    public static ItemStack createLunarBerry() {
        return createCustomHead("LUNAR_BERRY", CustomCrop.CropType.LUNAR_BERRY.getTextureForStage(2), "§bЛунная ягода", "Светится ночью, редкая.");
    }
    public static ItemStack createRainbowMushroom() {
        return createCustomHead("RAINBOW_MUSHROOM", CustomCrop.CropType.RAINBOW_MUSHROOM.getTextureForStage(1), "§dРадужный гриб", "Меняет цвет на разных стадиях.");
    }
    public static ItemStack createCrystalCactus() {
        return createCustomHead("CRYSTAL_CACTUS", CustomCrop.CropType.CRYSTAL_CACTUS.getTextureForStage(2), "§3Кристальный кактус", "Очень колючий!");
    }
    public static ItemStack createFlamePepper() {
        return createCustomHead("FLAME_PEPPER", CustomCrop.CropType.FLAME_PEPPER.getTextureForStage(1), "§cПылающий перец", "Осторожно, жжётся!");
    }
    public static ItemStack createMysticRoot() {
        return createCustomHead("MYSTIC_ROOT", CustomCrop.CropType.MYSTIC_ROOT.getTextureForStage(2), "§5Мистический корень", "Покрыт рунами.");
    }
    public static ItemStack createStarFruit() {
        return createCustomHead("STAR_FRUIT", CustomCrop.CropType.STAR_FRUIT.getTextureForStage(1), "§eЗвёздный плод", "Форма звезды, даёт энергию.");
    }
    public static ItemStack createPredatorFlower() {
        return createCustomHead("PREDATOR_FLOWER", CustomCrop.CropType.PREDATOR_FLOWER.getTextureForStage(2), "§4Цветок-хищник", "Похоже, у него есть зубы...");
    }
    public static ItemStack createElectroPumpkin() {
        return createCustomHead("ELECTRO_PUMPKIN", CustomCrop.CropType.ELECTRO_PUMPKIN.getTextureForStage(1), "§9Электро-тыква", "Потрескивает от энергии.");
    }
    public static ItemStack createMandrakeLeaf() {
        return createCustomHead("MANDRAKE_LEAF", CustomCrop.CropType.MANDRAKE_LEAF.getTextureForStage(1), "§aЛистья мандрагоры", "Смотрит на тебя.");
    }
    public static ItemStack createFlyingFruit() {
        return createCustomHead("FLYING_FRUIT", CustomCrop.CropType.FLYING_FRUIT.getTextureForStage(1), "§fЛетающий плод", "Плод с крыльями.");
    }
    public static ItemStack createSnowMint() {
        return createCustomHead("SNOW_MINT", CustomCrop.CropType.SNOW_MINT.getTextureForStage(1), "§bСнежная мята", "Покрыта инеем.");
    }
    public static ItemStack createSunPineapple() {
        return createCustomHead("SUN_PINEAPPLE", CustomCrop.CropType.SUN_PINEAPPLE.getTextureForStage(2), "§6Солнечный ананас", "Светится на солнце.");
    }
    public static ItemStack createFogBerry() {
        return createCustomHead("FOG_BERRY", CustomCrop.CropType.FOG_BERRY.getTextureForStage(1), "§7Туманная ягода", "Окутана дымкой.");
    }
    public static ItemStack createSandMelon() {
        return createCustomHead("SAND_MELON", CustomCrop.CropType.SAND_MELON.getTextureForStage(1), "§eПесчаный арбуз", "Растёт в пустыне.");
    }
    public static ItemStack createWitchMushroom() {
        return createCustomHead("WITCH_MUSHROOM", CustomCrop.CropType.WITCH_MUSHROOM.getTextureForStage(1), "§5Ведьмин гриб", "Пахнет магией.");
    }

    // Seeds for new crops
    public static ItemStack createStrawberrySeeds() {
        return createSeed(Material.WHEAT_SEEDS, "§dСемена Лучезарной Клубники", CustomCrop.CropType.STRAWBERRY.getSeedItemId(), 2, "Можно посадить на своем участке. Плодоносит несколько раз.");
    }

    public static ItemStack createRadishSeeds() {
        return createSeed(Material.WHEAT_SEEDS, "§fСемена Хрустящего Редиса", CustomCrop.CropType.RADISH.getSeedItemId(), 3, "Можно посадить на своем участке.");
    }

    public static ItemStack createWatermelonSeeds() {
        return createSeed(Material.MELON_SEEDS, "§aСемена Пустынного Арбуза", CustomCrop.CropType.WATERMELON.getSeedItemId(), 4, "Можно посадить на своем участке. Очень большой!");
    }

    // Harvested items for new crops
    public static ItemStack createStrawberry() {
        return createCustomHead("STRAWBERRY", CustomCrop.CropType.STRAWBERRY.getTextureForStage(1), "§dЛучезарная Клубника", "Сочная и ароматная.");
    }
    public static ItemStack createRadish() {
        return createCustomHead("RADISH", CustomCrop.CropType.RADISH.getTextureForStage(1), "§fХрустящий Редис", "Прямо с грядки!");
    }
    public static ItemStack createWatermelon() {
        return createCustomHead("WATERMELON", CustomCrop.CropType.WATERMELON.getTextureForStage(1), "§aПустынный Арбуз", "Огромный и сладкий!");
    }

    // Seeds for existing crops to ensure consistency
    public static ItemStack createLunarBerrySeeds() {
        return createSeed(Material.WHEAT_SEEDS, "§bСемена Лунной Ягоды", CustomCrop.CropType.LUNAR_BERRY.getSeedItemId(), 5, "Можно посадить на своем участке. Светится ночью.");
    }

    public static ItemStack createRainbowMushroomSeeds() {
        return createSeed(Material.BROWN_MUSHROOM, "§dСпоры Радужного Гриба", CustomCrop.CropType.RAINBOW_MUSHROOM.getSeedItemId(), 6, "Можно посадить на мицелий или подзол.");
    }

    public static ItemStack createCrystalCactusSeeds() {
        return createSeed(Material.WHEAT_SEEDS, "§3Семена Кристального Кактуса", CustomCrop.CropType.CRYSTAL_CACTUS.getSeedItemId(), 7, "Можно посадить на песок. Очень колючий!");
    }

    public static ItemStack createFlamePepperSeeds() {
        return createSeed(Material.WHEAT_SEEDS, "§cСемена Пылающего Перца", CustomCrop.CropType.FLAME_PEPPER.getSeedItemId(), 8, "Можно посадить на адский камень. Жжётся!");
    }

    public static ItemStack createMysticRootSeeds() {
        return createSeed(Material.WHEAT_SEEDS, "§5Семена Мистического Корня", CustomCrop.CropType.MYSTIC_ROOT.getSeedItemId(), 9, "Можно посадить на землю. Покрыт рунами.");
    }

    public static ItemStack createStarFruitSeeds() {
        return createSeed(Material.WHEAT_SEEDS, "§eСемена Звёздного Плода", CustomCrop.CropType.STAR_FRUIT.getSeedItemId(), 10, "Можно посадить на землю. Светится.");
    }

    public static ItemStack createPredatorFlowerSeeds() {
        return createSeed(Material.WHEAT_SEEDS, "§4Семена Цветка-Хищника", CustomCrop.CropType.PREDATOR_FLOWER.getSeedItemId(), 11, "Можно посадить на землю. Осторожно, кусается!");
    }

    public static ItemStack createElectroPumpkinSeeds() {
        return createSeed(Material.PUMPKIN_SEEDS, "§9Семена Электро-тыквы", CustomCrop.CropType.ELECTRO_PUMPKIN.getSeedItemId(), 12, "Можно посадить на землю. Потрескивает.");
    }

    public static ItemStack createMandrakeLeafSeeds() {
        return createSeed(Material.WHEAT_SEEDS, "§aСемена Листьев Мандрагоры", CustomCrop.CropType.MANDRAKE_LEAF.getSeedItemId(), 13, "Можно посадить на землю. Издает странные звуки.");
    }

    public static ItemStack createFlyingFruitSeeds() {
        return createSeed(Material.WHEAT_SEEDS, "§fСемена Летающего Плода", CustomCrop.CropType.FLYING_FRUIT.getSeedItemId(), 14, "Можно посадить на землю. Иногда исчезает.");
    }

    public static ItemStack createSnowMintSeeds() {
        return createSeed(Material.WHEAT_SEEDS, "§bСемена Снежной Мяты", CustomCrop.CropType.SNOW_MINT.getSeedItemId(), 15, "Можно посадить на снег. Освежает!");
    }

    public static ItemStack createSunPineappleSeeds() {
        return createSeed(Material.WHEAT_SEEDS, "§6Семена Солнечного Ананаса", CustomCrop.CropType.SUN_PINEAPPLE.getSeedItemId(), 16, "Можно посадить на землю. Светится на солнце.");
    }

    public static ItemStack createFogBerrySeeds() {
        return createSeed(Material.WHEAT_SEEDS, "§7Семена Туманной Ягоды", CustomCrop.CropType.FOG_BERRY.getSeedItemId(), 17, "Можно посадить на землю. Окутана дымкой.");
    }

    public static ItemStack createSandMelonSeeds() {
        return createSeed(Material.MELON_SEEDS, "§eСемена Песчаного Арбуза", CustomCrop.CropType.SAND_MELON.getSeedItemId(), 18, "Можно посадить на песок. Очень жаростойкий.");
    }

    public static ItemStack createWitchMushroomSeeds() {
        return createSeed(Material.CRIMSON_FUNGUS, "§5Споры Ведьмина Гриба", CustomCrop.CropType.WITCH_MUSHROOM.getSeedItemId(), 19, "Можно посадить на незерак. Пахнет магией.");
    }

    // Универсальный метод для создания семян
    private static ItemStack createSeed(Material material, String displayName, String seedId, int modelData, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, seedId);
        if (modelData > 0) {
            meta.setCustomModelData(modelData);
        }
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
    
    // Универсальный метод для создания головы с текстурой
    private static ItemStack createCustomHead(String itemId, String texture, String name, String... lore) {
        return createCustomHead(itemId, texture, name, Arrays.asList(lore));
    }

    private static ItemStack createCustomHead(String itemId, String texture, String name, java.util.List<String> lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));
        try {
            Field profileField = meta.getClass().getDeclaredField("profile"); // Для старых версий может быть "serializedProfile"
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        meta.setDisplayName(name);
        // Проверяем, что описание не пустое, чтобы не добавлять пустую строку
        if (lore != null && !lore.isEmpty() && !(lore.size() == 1 && lore.get(0).isEmpty())) {
            meta.setLore(lore);
        }
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, itemId);
        head.setItemMeta(meta);
        return head;
    }

    // Вспомогательный метод для создания головы-блока без ID (для установки в мире)
    private static ItemStack createHeadFromTexture(String texture, String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));
        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        meta.setDisplayName("§a" + name); // Даем имя, чтобы было понятно, что это
        head.setItemMeta(meta);
        return head;
    }
} 