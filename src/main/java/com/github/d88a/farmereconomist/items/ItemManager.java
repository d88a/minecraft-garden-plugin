package com.github.d88a.farmereconomist.items;

import com.github.d88a.farmereconomist.FarmerEconomist;
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
import org.bukkit.inventory.meta.PlayerProfile;

public class ItemManager {

    // Ключ для хранения уникального ID предмета в PersistentDataContainer
    private static NamespacedKey ITEM_ID_KEY;

    // --- Текстуры для плодов ---
    private static final String RED_TOMATO_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI4YTk5Y2I4ZWU3ZmY4MzBjY2Y4MWMyM2YwY2E3YTM4M2VjYWM3NDUzYjFhMTQ5Y2Y5Y2UyNGY0NDY1YSJ9fX0=";

    // --- Текстуры для стадий роста растений ---
    private static final String SPROUT_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjVlZDRiYjM0ZDRlY2YyYjBlY2MwYmYxY2RkYjYxYjI3OWY0NzYyYjM5YjQ4ZGY0ZGNkY2I0YjYyYjlkY2YxIn19fQ=="; // Общий росток
    private static final String TOMATO_PLANT_MATURE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjYyYmM3YjM5Y2E5YzA0NDc0N2QxYjI0Y2I5MDUxODcxYjY4YjY3ZTk0ZGY2Y2E0MGMxZmU5ZDUxM2U2M2UifX19";
    private static final String PUMPKIN_PLANT_STAGE_0_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmE2MDQ2YjE5YjI3YjQxYTQyODQxNDFhYjQyZDNkY2YxMTk5ZGU3Mjk2YjY0YjE2ZWIzY2YxYjY0Zjc4YjNmIn19fQ==";
    private static final String PUMPKIN_PLANT_STAGE_1_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Y5YjE4Y2Y2MTQzNmQ3YjE4YTgwYjA4MjY3ODg2ZDIxYjQ1YjU3YjE1Y2YyYjQ1Y2E0YjYyYjlkY2YxIn19fQ==";

    // --- Custom heads for unique crops ---
    // Пример текстур: можно заменить на более красивые/подходящие позже
    private static final String LUNAR_BERRY_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2In19fQ==";
    private static final String RAINBOW_MUSHROOM_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2In19fQ==";
    private static final String CRYSTAL_CACTUS_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2In19fQ==";
    private static final String FLAME_PEPPER_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2In19fQ==";
    private static final String MYSTIC_ROOT_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2In19fQ==";
    private static final String STAR_FRUIT_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2In19fQ==";
    private static final String PREDATOR_FLOWER_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2In19fQ==";
    private static final String ELECTRO_PUMPKIN_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2In19fQ==";
    private static final String MANDRAKE_LEAF_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2In19fQ==";
    private static final String FLYING_FRUIT_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2In19fQ==";
    private static final String SNOW_MINT_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2In19fQ==";
    private static final String SUN_PINEAPPLE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2In19fQ==";
    private static final String FOG_BERRY_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2In19fQ==";
    private static final String SAND_MELON_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2In19fQ==";
    private static final String WITCH_MUSHROOM_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2YjY2In19fQ==";

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
        meta.setLore(Arrays.asList("Можно посадить на своем участке."));
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "LETTUCE_SEEDS");
        meta.setCustomModelData(1);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack createTomatoSeeds() {
        ItemStack item = new ItemStack(Material.BEETROOT_SEEDS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cСемена Рубинового Томата");
        meta.setLore(Arrays.asList("Можно посадить на своем участке."));
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
        return createCustomHead("TOMATO", RED_TOMATO_TEXTURE, "§cРубиновый Томат", "Сочный и спелый!");
    }

    /**
     * Возвращает голову (блок) для определенной стадии роста растения.
     * @param type Тип растения
     * @param stage Стадия роста (начиная с 0)
     * @return ItemStack с кастомной головой
     */
    public static ItemStack getPlantStageHead(com.github.d88a.farmereconomist.crops.CustomCrop.CropType type, int stage) {
        String texture = SPROUT_TEXTURE; // По умолчанию для стадии 0 используется росток
        String name = "Росток";

        // Для каждой культуры определяем свою текстуру на каждой стадии
        switch (type) {
            case TOMATO:
                name = "Куст томата";
                if (stage == 1) { // Зрелая стадия для томата
                    texture = TOMATO_PLANT_MATURE_TEXTURE;
                }
                break;
            case ELECTRO_PUMPKIN:
                name = "Росток электро-тыквы";
                if (stage == 0) {
                    texture = PUMPKIN_PLANT_STAGE_0_TEXTURE;
                } else if (stage == 1) { // Зрелая стадия
                    texture = PUMPKIN_PLANT_STAGE_1_TEXTURE;
                    name = "Электро-тыква";
                }
                break;
            // ДОБАВЬТЕ СЮДА ДРУГИЕ РАСТЕНИЯ
            // case STRAWBERRY:
            //     if (stage == 1) texture = STRAWBERRY_MATURE_TEXTURE;
            //     break;
            default:
                // Для всех остальных растений на стадии 0 будет росток
                if (stage > 0) texture = SPROUT_TEXTURE; // Можно добавить текстуры по умолчанию для других стадий
                break;
        }
        return createHeadFromTexture(texture, name);
    }

    public static ItemStack createLettuce(boolean isWatered) {
        ItemStack item = new ItemStack(Material.POISONOUS_POTATO); // Используем "плохой" картофель как основу
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
        return createCustomHead("LUNAR_BERRY", LUNAR_BERRY_TEXTURE, "§bЛунная ягода", "Светится ночью, редкая.");
    }
    public static ItemStack createRainbowMushroom() {
        return createCustomHead("RAINBOW_MUSHROOM", RAINBOW_MUSHROOM_TEXTURE, "§dРадужный гриб", "Меняет цвет на разных стадиях.");
    }
    public static ItemStack createCrystalCactus() {
        return createCustomHead("CRYSTAL_CACTUS", CRYSTAL_CACTUS_TEXTURE, "§3Кристальный кактус", "Очень колючий!");
    }
    public static ItemStack createFlamePepper() {
        return createCustomHead("FLAME_PEPPER", FLAME_PEPPER_TEXTURE, "§cПылающий перец", "Осторожно, жжётся!");
    }
    public static ItemStack createMysticRoot() {
        return createCustomHead("MYSTIC_ROOT", MYSTIC_ROOT_TEXTURE, "§5Мистический корень", "Покрыт рунами.");
    }
    public static ItemStack createStarFruit() {
        return createCustomHead("STAR_FRUIT", STAR_FRUIT_TEXTURE, "§eЗвёздный плод", "Форма звезды, даёт энергию.");
    }
    public static ItemStack createPredatorFlower() {
        return createCustomHead("PREDATOR_FLOWER", PREDATOR_FLOWER_TEXTURE, "§4Цветок-хищник", "Похоже, у него есть зубы...");
    }
    public static ItemStack createElectroPumpkin() {
        return createCustomHead("ELECTRO_PUMPKIN", ELECTRO_PUMPKIN_TEXTURE, "§9Электро-тыква", "Потрескивает от энергии.");
    }
    public static ItemStack createMandrakeLeaf() {
        return createCustomHead("MANDRAKE_LEAF", MANDRAKE_LEAF_TEXTURE, "§aЛистья мандрагоры", "Смотрит на тебя.");
    }
    public static ItemStack createFlyingFruit() {
        return createCustomHead("FLYING_FRUIT", FLYING_FRUIT_TEXTURE, "§fЛетающий плод", "Плод с крыльями.");
    }
    public static ItemStack createSnowMint() {
        return createCustomHead("SNOW_MINT", SNOW_MINT_TEXTURE, "§bСнежная мята", "Покрыта инеем.");
    }
    public static ItemStack createSunPineapple() {
        return createCustomHead("SUN_PINEAPPLE", SUN_PINEAPPLE_TEXTURE, "§6Солнечный ананас", "Светится на солнце.");
    }
    public static ItemStack createFogBerry() {
        return createCustomHead("FOG_BERRY", FOG_BERRY_TEXTURE, "§7Туманная ягода", "Окутана дымкой.");
    }
    public static ItemStack createSandMelon() {
        return createCustomHead("SAND_MELON", SAND_MELON_TEXTURE, "§eПесчаный арбуз", "Растёт в пустыне.");
    }
    public static ItemStack createWitchMushroom() {
        return createCustomHead("WITCH_MUSHROOM", WITCH_MUSHROOM_TEXTURE, "§5Ведьмин гриб", "Пахнет магией.");
    }

    // Seeds for new crops
    public static ItemStack createStrawberrySeeds() {
        ItemStack item = new ItemStack(Material.SWEET_BERRIES); // Используем сладкие ягоды как семена
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§dСемена Лучезарной Клубники");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "STRAWBERRY_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на своем участке. Плодоносит несколько раз."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createRadishSeeds() {
        ItemStack item = new ItemStack(Material.CARROT); // Используем морковь как семена
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§fСемена Хрустящего Редиса");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "RADISH_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на своем участке."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createWatermelonSeeds() {
        ItemStack item = new ItemStack(Material.MELON_SEEDS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aСемена Пустынного Арбуза");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "WATERMELON_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на своем участке. Очень большой!"));
        item.setItemMeta(meta);
        return item;
    }

    // Harvested items for new crops
    public static ItemStack createStrawberry() {
        return createCustomHead("STRAWBERRY", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjJmNGM2ODQwOTdlZGRkZDhkNDJhYTU0NDg3Yzk0MjljYjkzYjkzMzU4YTdhNzg4OTUxY2Y2MDkxNzliMmQzZiJ9fX0=", "§dЛучезарная Клубника", "Сочная и ароматная.");
    }

    public static ItemStack createRadish() {
        return createCustomHead("RADISH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTgzNjk1NDExYjY3MTU1ZjQ2NTI2Y2RkNWJhODhlZmIzMGU0ZjQ0NjY3OGNmYTYzMmNiNGE5ZTIyZTM0OTFjIn19fQ==", "§fХрустящий Редис", "Прямо с грядки!");
    }

    public static ItemStack createWatermelon() {
        return createCustomHead("WATERMELON", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzk1ZGU1NzY4Y2M3NGEwZDY1NjFlNjVkMjI0ODc1MzMxZTg2OWQ4OWQyYmU0Mjg2YzYyYTg5NGEyYjY1NyJ9fX0=", "§aПустынный Арбуз", "Огромный и сладкий!");
    }

    // Seeds for existing crops to ensure consistency
    public static ItemStack createLunarBerrySeeds() {
        ItemStack item = new ItemStack(Material.MANGROVE_PROPAGULE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bСемена Лунной Ягоды");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "LUNAR_BERRY_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на своем участке. Светится ночью."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createRainbowMushroomSeeds() {
        ItemStack item = new ItemStack(Material.BROWN_MUSHROOM);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§dСпоры Радужного Гриба");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "RAINBOW_MUSHROOM_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на мицелий или подзол."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createCrystalCactusSeeds() {
        ItemStack item = new ItemStack(Material.CACTUS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§3Семена Кристального Кактуса");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "CRYSTAL_CACTUS_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на песок. Очень колючий!"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createFlamePepperSeeds() {
        ItemStack item = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cСемена Пылающего Перца");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "FLAME_PEPPER_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на адский камень. Жжётся!"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createMysticRootSeeds() {
        ItemStack item = new ItemStack(Material.VINE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§5Семена Мистического Корня");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "MYSTIC_ROOT_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на землю. Покрыт рунами."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createStarFruitSeeds() {
        ItemStack item = new ItemStack(Material.GLOW_BERRIES);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eСемена Звёздного Плода");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "STAR_FRUIT_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на землю. Светится."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createPredatorFlowerSeeds() {
        ItemStack item = new ItemStack(Material.WITHER_ROSE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§4Семена Цветка-Хищника");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "PREDATOR_FLOWER_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на землю. Осторожно, кусается!"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createElectroPumpkinSeeds() {
        ItemStack item = new ItemStack(Material.CARVED_PUMPKIN);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§9Семена Электро-тыквы");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "ELECTRO_PUMPKIN_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на землю. Потрескивает."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createMandrakeLeafSeeds() {
        ItemStack item = new ItemStack(Material.FERN);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aСемена Листьев Мандрагоры");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "MANDRAKE_LEAF_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на землю. Издает странные звуки."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createFlyingFruitSeeds() {
        ItemStack item = new ItemStack(Material.CHORUS_FRUIT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§fСемена Летающего Плода");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "FLYING_FRUIT_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на землю. Иногда исчезает."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createSnowMintSeeds() {
        ItemStack item = new ItemStack(Material.SNOWBALL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bСемена Снежной Мяты");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "SNOW_MINT_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на снег. Освежает!"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createSunPineappleSeeds() {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Семена Солнечного Ананаса");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "SUN_PINEAPPLE_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на землю. Светится на солнце."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createFogBerrySeeds() {
        ItemStack item = new ItemStack(Material.WEEPING_VINES);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§7Семена Туманной Ягоды");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "FOG_BERRY_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на землю. Окутана дымкой."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createSandMelonSeeds() {
        ItemStack item = new ItemStack(Material.BONE_MEAL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eСемена Песчаного Арбуза");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "SAND_MELON_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на песок. Очень жаростойкий."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createWitchMushroomSeeds() {
        ItemStack item = new ItemStack(Material.CRIMSON_FUNGUS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§5Споры Ведьмина Гриба");
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, "WITCH_MUSHROOM_SEEDS");
        meta.setLore(Arrays.asList("Можно посадить на незерак. Пахнет магией."));
        item.setItemMeta(meta);
        return item;
    }

    // Универсальный метод для создания головы с текстурой
    private static ItemStack createCustomHead(String itemId, String texture, String name, String lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        // Современный и надежный способ установки текстуры
        PlayerProfile playerProfile = Bukkit.createPlayerProfile(UUID.randomUUID());
        playerProfile.getProperties().add(new Property("textures", texture));
        meta.setOwnerProfile(playerProfile);

        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, itemId);
        head.setItemMeta(meta);
        return head;
    }

    // Вспомогательный метод для создания головы-блока без ID (для установки в мире)
    private static ItemStack createHeadFromTexture(String texture, String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        PlayerProfile playerProfile = Bukkit.createPlayerProfile(UUID.randomUUID());
        playerProfile.getProperties().add(new Property("textures", texture));
        meta.setOwnerProfile(playerProfile);
        meta.setDisplayName("§a" + name); // Даем имя, чтобы было понятно, что это
        head.setItemMeta(meta);
        return head;
    }
} 