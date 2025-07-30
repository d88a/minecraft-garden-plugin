package com.github.d88a.farmereconomist.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.UUID;
import org.bukkit.Bukkit;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class ItemManager {

    // --- Texture values for custom heads ---
    private static final String GREEN_TOMATO_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA5YjU5YjJlNmYxOTBhYjI0MTBkYjNkY2U5MGMxNGU0ZGNlYjVjMmFiN2NhYTc5N2ZlM2U5Y2E2NDQ3ZGEifX19";
    private static final String RED_TOMATO_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI4YTk5Y2I4ZWU3ZmY4MzBjY2Y4MWMyM2YwY2E3YTM4M2VjYWM3NDUzYjFhMTQ5Y2Y5Y2UyNGY0NDY1YSJ9fX0=";
    private static final String SMALL_MUSHROOM_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1NDgzNjJhMjY3YjYxY2Y5YjY1OTQ0YjY4ZDRmM2Y3OTY0MGI1Y2Q3M2Y1YWE5YTU2NmI4YjliZWMyZGU5In19fQ==";
    private static final String GLOWSHROOM_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTI4OWQzYjE1YjE1NDk5YjcxY2RjYThmZDE0MmM0YmZmMjE4YjQ5OWQ4Y2IyZGI5YTM0Yzc4ZDM5YjE1YjEifX19";

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


    public static ItemStack createWateringCan() {
        ItemStack item = new ItemStack(Material.IRON_HOE); // Используем мотыгу как основу
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bЛейка");
        meta.setLore(Arrays.asList("§7Наполните водой, чтобы поливать растения.", "§7Использование: ПКМ по растению."));
        meta.setCustomModelData(1); // Уникальный ID для ресурс-пака
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createFertilizer() {
        ItemStack item = new ItemStack(Material.BONE_MEAL); // Используем костную муку как основу
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aУдобрение");
        meta.setLore(Arrays.asList("§7Ускоряет рост растений.", "§7Использование: ПКМ по растению."));
        meta.setCustomModelData(1); // Если есть ресурс-пак, использовать другой ID
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createLettuceSeeds() {
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aСемена Скромного Латука");
        meta.setLore(Arrays.asList("Можно посадить на своем участке."));
        meta.setCustomModelData(1);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack createTomatoSeeds() {
        ItemStack item = new ItemStack(Material.BEETROOT_SEEDS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cСемена Рубинового Томата");
        meta.setLore(Arrays.asList("Можно посадить на своем участке."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createGlowshroomSpores() {
        ItemStack item = new ItemStack(Material.RED_MUSHROOM);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§dСпоры светящегося гриба");
        meta.setLore(Arrays.asList("Можно посадить на мицелий или подзол."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createGlowshroomDust() {
        ItemStack item = new ItemStack(Material.GLOWSTONE_DUST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§dПыльца светящегося гриба");
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createTomato() {
        return createCustomHead(RED_TOMATO_TEXTURE, "§cРубиновый Томат", "Сочный и спелый!");
    }

    public static ItemStack createGlowshroomStage(int stage) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        String texture = (stage == 0) ? SMALL_MUSHROOM_TEXTURE : GLOWSHROOM_TEXTURE;

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));
        try {
            java.lang.reflect.Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack createTomatoStage(int stage) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        String texture = (stage == 0) ? GREEN_TOMATO_TEXTURE : RED_TOMATO_TEXTURE;

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));
        try {
            java.lang.reflect.Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack createLettuce(boolean isWatered) {
        ItemStack item = new ItemStack(Material.POISONOUS_POTATO); // Используем "плохой" картофель как основу
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§2Скромный Латук");
        if (isWatered) {
            meta.setLore(Arrays.asList("Качество: §aОтличное"));
            meta.setCustomModelData(101); // Модель для качественного латука
        } else {
            meta.setLore(Arrays.asList("Качество: §eОбычное"));
            meta.setCustomModelData(100); // Модель для обычного латука
        }
        item.setItemMeta(meta);
        return item;
    }

    // --- Методы для генерации ItemStack для каждого растения ---
    public static ItemStack createLunarBerry() {
        return createCustomHead(LUNAR_BERRY_TEXTURE, "§bЛунная ягода", "Светится ночью, редкая.");
    }
    public static ItemStack createRainbowMushroom() {
        return createCustomHead(RAINBOW_MUSHROOM_TEXTURE, "§dРадужный гриб", "Меняет цвет на разных стадиях.");
    }
    public static ItemStack createCrystalCactus() {
        return createCustomHead(CRYSTAL_CACTUS_TEXTURE, "§3Кристальный кактус", "Очень колючий!");
    }
    public static ItemStack createFlamePepper() {
        return createCustomHead(FLAME_PEPPER_TEXTURE, "§cПылающий перец", "Осторожно, жжётся!");
    }
    public static ItemStack createMysticRoot() {
        return createCustomHead(MYSTIC_ROOT_TEXTURE, "§5Мистический корень", "Покрыт рунами.");
    }
    public static ItemStack createStarFruit() {
        return createCustomHead(STAR_FRUIT_TEXTURE, "§eЗвёздный плод", "Форма звезды, даёт энергию.");
    }
    public static ItemStack createPredatorFlower() {
        return createCustomHead(PREDATOR_FLOWER_TEXTURE, "§4Цветок-хищник", "Похоже, у него есть зубы...");
    }
    public static ItemStack createElectroPumpkin() {
        return createCustomHead(ELECTRO_PUMPKIN_TEXTURE, "§9Электро-тыква", "Потрескивает от энергии.");
    }
    public static ItemStack createMandrakeLeaf() {
        return createCustomHead(MANDRAKE_LEAF_TEXTURE, "§aЛистья мандрагоры", "Смотрит на тебя.");
    }
    public static ItemStack createFlyingFruit() {
        return createCustomHead(FLYING_FRUIT_TEXTURE, "§fЛетающий плод", "Плод с крыльями.");
    }
    public static ItemStack createSnowMint() {
        return createCustomHead(SNOW_MINT_TEXTURE, "§bСнежная мята", "Покрыта инеем.");
    }
    public static ItemStack createSunPineapple() {
        return createCustomHead(SUN_PINEAPPLE_TEXTURE, "§6Солнечный ананас", "Светится на солнце.");
    }
    public static ItemStack createFogBerry() {
        return createCustomHead(FOG_BERRY_TEXTURE, "§7Туманная ягода", "Окутана дымкой.");
    }
    public static ItemStack createSandMelon() {
        return createCustomHead(SAND_MELON_TEXTURE, "§eПесчаный арбуз", "Растёт в пустыне.");
    }
    public static ItemStack createWitchMushroom() {
        return createCustomHead(WITCH_MUSHROOM_TEXTURE, "§5Ведьмин гриб", "Пахнет магией.");
    }

    // Seeds for new crops
    public static ItemStack createStrawberrySeeds() {
        ItemStack item = new ItemStack(Material.SWEET_BERRIES); // Используем сладкие ягоды как семена
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§dСемена Лучезарной Клубники");
        meta.setLore(Arrays.asList("Можно посадить на своем участке. Плодоносит несколько раз."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createRadishSeeds() {
        ItemStack item = new ItemStack(Material.CARROT); // Используем морковь как семена
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§fСемена Хрустящего Редиса");
        meta.setLore(Arrays.asList("Можно посадить на своем участке."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createWatermelonSeeds() {
        ItemStack item = new ItemStack(Material.MELON_SEEDS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aСемена Пустынного Арбуза");
        meta.setLore(Arrays.asList("Можно посадить на своем участке. Очень большой!"));
        item.setItemMeta(meta);
        return item;
    }

    // Harvested items for new crops
    public static ItemStack createStrawberry() {
        return createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjJmNGM2ODQwOTdlZGRkZDhkNDJhYTU0NDg3Yzk0MjljYjkzYjkzMzU4YTdhNzg4OTUxY2Y2MDkxNzliMmQzZiJ9fX0=", "§dЛучезарная Клубника", "Сочная и ароматная.");
    }

    public static ItemStack createRadish() {
        return createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTgzNjk1NDExYjY3MTU1ZjQ2NTI2Y2RkNWJhODhlZmIzMGU0ZjQ0NjY3OGNmYTYzMmNiNGE5ZTIyZTM0OTFjIn19fQ==", "§fХрустящий Редис", "Прямо с грядки!");
    }

    public static ItemStack createWatermelon() {
        return createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzk1ZGU1NzY4Y2M3NGEwZDY1NjFlNjVkMjI0ODc1MzMxZTg2OWQ4OWQyYmU0Mjg2YzYyYTg5NGEyYjY1NyJ9fX0=", "§aПустынный Арбуз", "Огромный и сладкий!");
    }

    // Seeds for existing crops to ensure consistency
    public static ItemStack createLunarBerrySeeds() {
        ItemStack item = new ItemStack(Material.MANGROVE_PROPAGULE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bСемена Лунной Ягоды");
        meta.setLore(Arrays.asList("Можно посадить на своем участке. Светится ночью."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createRainbowMushroomSeeds() {
        ItemStack item = new ItemStack(Material.BROWN_MUSHROOM);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§dСпоры Радужного Гриба");
        meta.setLore(Arrays.asList("Можно посадить на мицелий или подзол."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createCrystalCactusSeeds() {
        ItemStack item = new ItemStack(Material.CACTUS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§3Семена Кристального Кактуса");
        meta.setLore(Arrays.asList("Можно посадить на песок. Очень колючий!"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createFlamePepperSeeds() {
        ItemStack item = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cСемена Пылающего Перца");
        meta.setLore(Arrays.asList("Можно посадить на адский камень. Жжётся!"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createMysticRootSeeds() {
        ItemStack item = new ItemStack(Material.VINE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§5Семена Мистического Корня");
        meta.setLore(Arrays.asList("Можно посадить на землю. Покрыт рунами."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createStarFruitSeeds() {
        ItemStack item = new ItemStack(Material.GLOW_BERRIES);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eСемена Звёздного Плода");
        meta.setLore(Arrays.asList("Можно посадить на землю. Светится."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createPredatorFlowerSeeds() {
        ItemStack item = new ItemStack(Material.WITHER_ROSE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§4Семена Цветка-Хищника");
        meta.setLore(Arrays.asList("Можно посадить на землю. Осторожно, кусается!"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createElectroPumpkinSeeds() {
        ItemStack item = new ItemStack(Material.CARVED_PUMPKIN);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§9Семена Электро-тыквы");
        meta.setLore(Arrays.asList("Можно посадить на землю. Потрескивает."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createMandrakeLeafSeeds() {
        ItemStack item = new ItemStack(Material.FERN);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aСемена Листьев Мандрагоры");
        meta.setLore(Arrays.asList("Можно посадить на землю. Издает странные звуки."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createFlyingFruitSeeds() {
        ItemStack item = new ItemStack(Material.CHORUS_FRUIT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§fСемена Летающего Плода");
        meta.setLore(Arrays.asList("Можно посадить на землю. Иногда исчезает."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createSnowMintSeeds() {
        ItemStack item = new ItemStack(Material.SNOWBALL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bСемена Снежной Мяты");
        meta.setLore(Arrays.asList("Можно посадить на снег. Освежает!"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createSunPineappleSeeds() {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Семена Солнечного Ананаса");
        meta.setLore(Arrays.asList("Можно посадить на землю. Светится на солнце."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createFogBerrySeeds() {
        ItemStack item = new ItemStack(Material.WEEPING_VINES);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§7Семена Туманной Ягоды");
        meta.setLore(Arrays.asList("Можно посадить на землю. Окутана дымкой."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createSandMelonSeeds() {
        ItemStack item = new ItemStack(Material.BONE_MEAL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eСемена Песчаного Арбуза");
        meta.setLore(Arrays.asList("Можно посадить на песок. Очень жаростойкий."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createWitchMushroomSeeds() {
        ItemStack item = new ItemStack(Material.CRIMSON_FUNGUS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§5Споры Ведьмина Гриба");
        meta.setLore(Arrays.asList("Можно посадить на незерак. Пахнет магией."));
        item.setItemMeta(meta);
        return item;
    }

    // Универсальный метод для создания головы с текстурой
    private static ItemStack createCustomHead(String texture, String name, String lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));
        try {
            java.lang.reflect.Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        head.setItemMeta(meta);
        return head;
    }
} 