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
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aСемена Скромного Латука");
        meta.setLore(Arrays.asList("Можно посадить на вспаханную землю."));
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "LETTUCE_SEEDS");
        meta.setCustomModelData(1);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack createTomatoSeeds() {
        ItemStack item = new ItemStack(Material.BEETROOT_SEEDS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cСемена Рубинового Томата");
        meta.setLore(Arrays.asList("Можно посадить на вспаханную землю."));
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "TOMATO_SEEDS");
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createGlowshroomSpores() {
        ItemStack item = new ItemStack(Material.RED_MUSHROOM);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§dСпоры светящегося гриба");
        meta.setLore(Arrays.asList("Можно посадить на мицелий или подзол."));
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "GLOWSHROOM_SPORES");
        item.setItemMeta(meta);
        return item;
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
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS); // Стандартизируем внешний вид
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§dСемена Лучезарной Клубники");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.STRAWBERRY.getSeedItemId());
        meta.setCustomModelData(2); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на своем участке. Плодоносит несколько раз."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createRadishSeeds() {
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS); // Стандартизируем внешний вид
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§fСемена Хрустящего Редиса");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.RADISH.getSeedItemId());
        meta.setCustomModelData(3); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на своем участке."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createWatermelonSeeds() {
        ItemStack item = new ItemStack(Material.MELON_SEEDS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aСемена Пустынного Арбуза");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.WATERMELON.getSeedItemId());
        meta.setCustomModelData(4); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на своем участке. Очень большой!"));
        item.setItemMeta(meta);
        return item;
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
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS); // ИСПРАВЛЕНО: Убираем "манговое дерево"
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bСемена Лунной Ягоды");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.LUNAR_BERRY.getSeedItemId());
        meta.setCustomModelData(5); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на своем участке. Светится ночью."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createRainbowMushroomSeeds() {
        ItemStack item = new ItemStack(Material.BROWN_MUSHROOM);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§dСпоры Радужного Гриба");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.RAINBOW_MUSHROOM.getSeedItemId());
        meta.setCustomModelData(6); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на мицелий или подзол."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createCrystalCactusSeeds() {
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS); // Стандартизируем внешний вид
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§3Семена Кристального Кактуса");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.CRYSTAL_CACTUS.getSeedItemId());
        meta.setCustomModelData(7); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на песок. Очень колючий!"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createFlamePepperSeeds() {
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS); // Стандартизируем внешний вид
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cСемена Пылающего Перца");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.FLAME_PEPPER.getSeedItemId());
        meta.setCustomModelData(8); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на адский камень. Жжётся!"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createMysticRootSeeds() {
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS); // Стандартизируем внешний вид
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§5Семена Мистического Корня");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.MYSTIC_ROOT.getSeedItemId());
        meta.setCustomModelData(9); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на землю. Покрыт рунами."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createStarFruitSeeds() {
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS); // Стандартизируем внешний вид
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eСемена Звёздного Плода");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.STAR_FRUIT.getSeedItemId());
        meta.setCustomModelData(10); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на землю. Светится."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createPredatorFlowerSeeds() {
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS); // Стандартизируем внешний вид
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§4Семена Цветка-Хищника");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.PREDATOR_FLOWER.getSeedItemId());
        meta.setCustomModelData(11); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на землю. Осторожно, кусается!"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createElectroPumpkinSeeds() {
        ItemStack item = new ItemStack(Material.PUMPKIN_SEEDS); // Используем правильные семена
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§9Семена Электро-тыквы");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.ELECTRO_PUMPKIN.getSeedItemId());
        meta.setCustomModelData(12); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на землю. Потрескивает."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createMandrakeLeafSeeds() {
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS); // Стандартизируем внешний вид
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aСемена Листьев Мандрагоры");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.MANDRAKE_LEAF.getSeedItemId());
        meta.setCustomModelData(13); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на землю. Издает странные звуки."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createFlyingFruitSeeds() {
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS); // Стандартизируем внешний вид
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§fСемена Летающего Плода");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.FLYING_FRUIT.getSeedItemId());
        meta.setCustomModelData(14); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на землю. Иногда исчезает."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createSnowMintSeeds() {
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS); // Стандартизируем внешний вид
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bСемена Снежной Мяты");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.SNOW_MINT.getSeedItemId());
        meta.setCustomModelData(15); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на снег. Освежает!"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createSunPineappleSeeds() {
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS); // Стандартизируем внешний вид
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Семена Солнечного Ананаса");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.SUN_PINEAPPLE.getSeedItemId());
        meta.setCustomModelData(16); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на землю. Светится на солнце."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createFogBerrySeeds() {
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS); // Стандартизируем внешний вид
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§7Семена Туманной Ягоды");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.FOG_BERRY.getSeedItemId());
        meta.setCustomModelData(17); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на землю. Окутана дымкой."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createSandMelonSeeds() {
        ItemStack item = new ItemStack(Material.MELON_SEEDS); // Используем правильные семена
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eСемена Песчаного Арбуза");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.SAND_MELON.getSeedItemId());
        meta.setCustomModelData(18); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на песок. Очень жаростойкий."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createWitchMushroomSeeds() {
        ItemStack item = new ItemStack(Material.CRIMSON_FUNGUS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§5Споры Ведьмина Гриба");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, CustomCrop.CropType.WITCH_MUSHROOM.getSeedItemId());
        meta.setCustomModelData(19); // Уникальная модель для ресурс-пака
        meta.setLore(Arrays.asList("Можно посадить на незерак. Пахнет магией."));
        item.setItemMeta(meta);
        return item;
    }

    // Универсальный метод для создания головы с текстурой
    private static ItemStack createCustomHead(String itemId, String texture, String name, String... lore) {
        ItemStack head = createCustomHead(itemId, texture, name, Arrays.asList(lore));
        return head;
    }
    private static ItemStack createCustomHead(String itemId, String texture, String name, String lore) {
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

        meta.setDisplayName(name);
        if (lore != null && !lore.isEmpty()) {
            meta.setLore(Arrays.asList(lore));
        }
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, itemId);
        head.setItemMeta(meta);
        return head;
    }
    private static ItemStack createCustomHead(String itemId, String texture, String name, java.util.List<String> lore) {
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

        meta.setDisplayName(name);
        meta.setLore(lore);
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