package com.github.d88a.farmereconomist.npc;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import org.bukkit.NamespacedKey;

public class ShopGUI {

    private final FarmerEconomist plugin;

    public ShopGUI(FarmerEconomist plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory mainMenu = Bukkit.createInventory(null, 27, "Магазин Старого Мирона");
        ItemStack buy = new ItemStack(Material.EMERALD);
        ItemMeta buyMeta = buy.getItemMeta();
        buyMeta.setDisplayName("§aКупить");
        buy.setItemMeta(buyMeta);
        mainMenu.setItem(10, buy);

        ItemStack sell = new ItemStack(Material.GOLD_INGOT);
        ItemMeta sellMeta = sell.getItemMeta();
        sellMeta.setDisplayName("§6Продать");
        sell.setItemMeta(sellMeta);
        mainMenu.setItem(12, sell);

        // Кнопка справочника растений
        ItemStack guide = new ItemStack(Material.BOOK);
        ItemMeta guideMeta = guide.getItemMeta();
        guideMeta.setDisplayName("§eСправочник растений");
        guideMeta.setLore(Arrays.asList(
            "§7Нажмите, чтобы открыть",
            "§7подробный справочник по всем растениям"
        ));
        guide.setItemMeta(guideMeta);
        mainMenu.setItem(14, guide);

        // Кнопка справочника событий
        ItemStack events = new ItemStack(Material.BEACON);
        ItemMeta eventsMeta = events.getItemMeta();
        eventsMeta.setDisplayName("§dИгровые события");
        eventsMeta.setLore(Arrays.asList(
            "§7Узнайте о временных бонусах,",
            "§7которые действуют на сервере."
        ));
        events.setItemMeta(eventsMeta);
        mainMenu.setItem(16, events);

        // Заполняем пустые слоты
        for (int i = 0; i < mainMenu.getSize(); i++) {
            if (mainMenu.getItem(i) == null) {
                mainMenu.setItem(i, createFiller());
            }
        }

        player.openInventory(mainMenu);
    }

    public void openBuy(Player player) {
        Inventory buyInv = Bukkit.createInventory(null, 54, "Купить у Мирона");

        // Загружаем предметы с ценами из конфига
        addItemToBuyMenu(buyInv, 10, ItemManager.createLettuceSeeds());
        addItemToBuyMenu(buyInv, 11, ItemManager.createTomatoSeeds());
        addItemToBuyMenu(buyInv, 12, ItemManager.createGlowshroomSpores());
        addItemToBuyMenu(buyInv, 13, ItemManager.createStrawberrySeeds());
        addItemToBuyMenu(buyInv, 14, ItemManager.createRadishSeeds());
        addItemToBuyMenu(buyInv, 15, ItemManager.createWatermelonSeeds());
        addItemToBuyMenu(buyInv, 16, ItemManager.createLunarBerrySeeds());
        // ... и так далее для всех предметов ...

        // Инструменты
        addItemToBuyMenu(buyInv, 37, ItemManager.createWateringCan());
        addItemToBuyMenu(buyInv, 38, ItemManager.createFertilizer());

        // Заполняем пустые слоты
        for (int i = 0; i < buyInv.getSize(); i++) {
            if (buyInv.getItem(i) == null) {
                buyInv.setItem(i, createFiller());
            }
        }
        player.openInventory(buyInv);
    }

    private void addItemToBuyMenu(Inventory inv, int slot, ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        String itemId = meta.getPersistentDataContainer().get(ItemManager.ITEM_ID_KEY, PersistentDataType.STRING);
        if (itemId == null) {
            plugin.getLogger().warning("Item " + item.getType() + " in ShopGUI has no item ID!");
            return;
        }

        double price = plugin.getConfig().getDouble("shop-prices.buy." + itemId, -1.0);
        if (price >= 0) { // Используем >= 0, чтобы разрешить бесплатные предметы
            inv.setItem(slot, createBuyItem(item, price));
        }
    }

    private boolean playerHasItem(Player player, String itemId) {
        NamespacedKey key = ItemManager.ITEM_ID_KEY;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    if (meta.getPersistentDataContainer().get(key, PersistentDataType.STRING).equals(itemId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void openSell(Player player) {
        Inventory sellInv = Bukkit.createInventory(null, 54, "Продать Мирону");
        
        int slot = 10;
        
        // Базовые растения
        if (playerHasItem(player, "LETTUCE_NORMAL")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createLettuce(false), 5));
        }
        if (playerHasItem(player, "LETTUCE_EXCELLENT")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createLettuce(true), 15));
        }
        if (playerHasItem(player, "TOMATO")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createTomato(), 20));
        }
        if (playerHasItem(player, "GLOWSHROOM_DUST")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createGlowshroomDust(), 45));
        }
        
        // Новые растения - первая строка
        if (playerHasItem(player, "STRAWBERRY")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createStrawberry(), 60));
        }
        if (playerHasItem(player, "RADISH")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createRadish(), 25));
        }
        if (playerHasItem(player, "WATERMELON")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createWatermelon(), 80));
        }
        if (playerHasItem(player, "LUNAR_BERRY")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createLunarBerry(), 120));
        }
        
        // Вторая строка
        if (playerHasItem(player, "RAINBOW_MUSHROOM")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createRainbowMushroom(), 65));
        }
        if (playerHasItem(player, "CRYSTAL_CACTUS")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createCrystalCactus(), 100));
        }
        if (playerHasItem(player, "FLAME_PEPPER")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createFlamePepper(), 75));
        }
        if (playerHasItem(player, "MYSTIC_ROOT")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createMysticRoot(), 160));
        }
        
        // Третья строка
        if (playerHasItem(player, "STAR_FRUIT")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createStarFruit(), 145));
        }
        if (playerHasItem(player, "PREDATOR_FLOWER")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createPredatorFlower(), 200));
        }
        if (playerHasItem(player, "ELECTRO_PUMPKIN")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createElectroPumpkin(), 130));
        }
        if (playerHasItem(player, "MANDRAKE_LEAF")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createMandrakeLeaf(), 90));
        }
        
        // Четвертая строка
        if (playerHasItem(player, "FLYING_FRUIT")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createFlyingFruit(), 115));
        }
        if (playerHasItem(player, "SNOW_MINT")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createSnowMint(), 80));
        }
        if (playerHasItem(player, "SUN_PINEAPPLE")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createSunPineapple(), 180));
        }
        if (playerHasItem(player, "FOG_BERRY")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createFogBerry(), 70));
        }
        
        // Пятая строка
        if (playerHasItem(player, "SAND_MELON")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createSandMelon(), 105));
        }
        if (playerHasItem(player, "WITCH_MUSHROOM")) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createWitchMushroom(), 140));
        }
        
        // Заполняем пустые слоты
        for (int i = 0; i < sellInv.getSize(); i++) {
            if (sellInv.getItem(i) == null) {
                sellInv.setItem(i, createFiller());
            }
        }
        player.openInventory(sellInv);
    }

    public void openPlantGuide(Player player) {
        Inventory guideInv = Bukkit.createInventory(null, 54, "§aСправочник растений");
        
        // Первая страница - базовые растения
        guideInv.setItem(10, createGuideItem(ItemManager.createLettuceSeeds(), 
            "§aСкромный Латук", 
            "§7Цена покупки: §e10 монет",
            "§7Цена продажи: §e5-15 монет",
            "§7Стадии: §b2",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fВспаханная земля",
            "§7Особенности: §fКачество зависит от полива"
        ));
        
        guideInv.setItem(11, createGuideItem(ItemManager.createTomatoSeeds(), 
            "§cРубиновый Томат", 
            "§7Цена покупки: §e25 монет",
            "§7Цена продажи: §e20 монет",
            "§7Стадии: §b2",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fВспаханная земля",
            "§7Особенности: §fОдноразовый сбор"
        ));
        
        guideInv.setItem(12, createGuideItem(ItemManager.createGlowshroomSpores(), 
            "§dСветящийся Гриб", 
            "§7Цена покупки: §e50 монет",
            "§7Цена продажи: §e45 монет",
            "§7Стадии: §b2",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fМицелий/Подзол",
            "§7Особенности: §fДропает пыльцу"
        ));
        
        guideInv.setItem(13, createGuideItem(ItemManager.createStrawberrySeeds(), 
            "§dЛучезарная Клубника", 
            "§7Цена покупки: §e75 монет",
            "§7Цена продажи: §e60 монет",
            "§7Стадии: §b2",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fВспаханная земля",
            "§7Особенности: §fМногоразовый сбор (3 раза)"
        ));
        
        guideInv.setItem(14, createGuideItem(ItemManager.createLunarBerrySeeds(), 
            "§bЛунная Ягода", 
            "§7Цена покупки: §e150 монет",
            "§7Цена продажи: §e120 монет",
            "§7Стадии: §b3",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fВспаханная земля",
            "§7Особенности: §fСветится ночью, многоразовый"
        ));
        
        guideInv.setItem(15, createGuideItem(ItemManager.createRainbowMushroomSeeds(), 
            "§dРадужный Гриб", 
            "§7Цена покупки: §e80 монет",
            "§7Цена продажи: §e65 монет",
            "§7Стадии: §b2",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fМицелий/Подзол",
            "§7Особенности: §fМеняет цвет"
        ));
        
        guideInv.setItem(16, createGuideItem(ItemManager.createCrystalCactusSeeds(), 
            "§3Кристальный Кактус", 
            "§7Цена покупки: §e120 монет",
            "§7Цена продажи: §e100 монет",
            "§7Стадии: §b3",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fПесок",
            "§7Особенности: §fМногоразовый сбор"
        ));
        
        // Вторая строка
        guideInv.setItem(19, createGuideItem(ItemManager.createFlamePepperSeeds(), 
            "§cПылающий Перец", 
            "§7Цена покупки: §e90 монет",
            "§7Цена продажи: §e75 монет",
            "§7Стадии: §b2",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fАдский камень",
            "§7Особенности: §fЭффект огня"
        ));
        
        guideInv.setItem(20, createGuideItem(ItemManager.createMysticRootSeeds(), 
            "§5Мистический Корень", 
            "§7Цена покупки: §e200 монет",
            "§7Цена продажи: §e160 монет",
            "§7Стадии: §b3",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fЗемля",
            "§7Особенности: §fПокрыт рунами, многоразовый"
        ));
        
        guideInv.setItem(21, createGuideItem(ItemManager.createStarFruitSeeds(), 
            "§eЗвёздный Плод", 
            "§7Цена покупки: §e180 монет",
            "§7Цена продажи: §e145 монет",
            "§7Стадии: §b2",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fЗемля",
            "§7Особенности: §fФорма звезды, многоразовый"
        ));
        
        guideInv.setItem(22, createGuideItem(ItemManager.createPredatorFlowerSeeds(), 
            "§4Цветок-Хищник", 
            "§7Цена покупки: §e250 монет",
            "§7Цена продажи: §e200 монет",
            "§7Стадии: §b3",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fЗемля",
            "§7Особенности: §fОсторожно, кусается!"
        ));
        
        guideInv.setItem(23, createGuideItem(ItemManager.createElectroPumpkinSeeds(), 
            "§9Электро-Тыква", 
            "§7Цена покупки: §e160 монет",
            "§7Цена продажи: §e130 монет",
            "§7Стадии: §b2",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fЗемля",
            "§7Особенности: §fПотрескивает, многоразовый"
        ));
        
        guideInv.setItem(24, createGuideItem(ItemManager.createWitchMushroomSeeds(), 
            "§5Ведьмин Гриб", 
            "§7Цена покупки: §e175 монет",
            "§7Цена продажи: §e140 монет",
            "§7Стадии: §b2",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fНезерак",
            "§7Особенности: §fПахнет магией"
        ));
        
        // Третья строка
        guideInv.setItem(28, createGuideItem(ItemManager.createRadishSeeds(),
            "§fХрустящий Редис",
            "§7Цена покупки: §e30 монет",
            "§7Цена продажи: §e25 монет",
            "§7Стадии: §b2",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fВспаханная земля",
            "§7Особенности: §fБыстро растет"
        ));

        guideInv.setItem(29, createGuideItem(ItemManager.createWatermelonSeeds(),
            "§aПустынный Арбуз",
            "§7Цена покупки: §e100 монет",
            "§7Цена продажи: §e80 монет",
            "§7Стадии: §b2",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fВспаханная земля",
            "§7Особенности: §fОчень большой!"
        ));

        guideInv.setItem(30, createGuideItem(ItemManager.createMandrakeLeafSeeds(),
            "§aЛистья Мандрагоры",
            "§7Цена покупки: §e110 монет",
            "§7Цена продажи: §e90 монет",
            "§7Стадии: §b2",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fЗемля",
            "§7Особенности: §fИздает странные звуки"
        ));

        guideInv.setItem(31, createGuideItem(ItemManager.createFlyingFruitSeeds(),
            "§fЛетающий Плод",
            "§7Цена покупки: §e140 монет",
            "§7Цена продажи: §e115 монет",
            "§7Стадии: §b2",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fЗемля",
            "§7Особенности: §fИногда исчезает"
        ));

        // Четвертая строка
        guideInv.setItem(32, createGuideItem(ItemManager.createSnowMintSeeds(),
            "§bСнежная Мята",
            "§7Цена покупки: §e95 монет",
            "§7Цена продажи: §e80 монет",
            "§7Стадии: §b2",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fСнежный блок",
            "§7Особенности: §fОсвежает!"
        ));

        guideInv.setItem(33, createGuideItem(ItemManager.createSunPineappleSeeds(),
            "§6Солнечный Ананас",
            "§7Цена покупки: §e220 монет",
            "§7Цена продажи: §e180 монет",
            "§7Стадии: §b3",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fЗемля",
            "§7Особенности: §fСветится на солнце"
        ));

        guideInv.setItem(34, createGuideItem(ItemManager.createFogBerrySeeds(),
            "§7Туманная Ягода",
            "§7Цена покупки: §e85 монет",
            "§7Цена продажи: §e70 монет",
            "§7Стадии: §b2",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fЗемля",
            "§7Особенности: §fМногоразовый, окутана дымкой"
        ));

        guideInv.setItem(35, createGuideItem(ItemManager.createSandMelonSeeds(),
            "§eПесчаный Арбуз",
            "§7Цена покупки: §e130 монет",
            "§7Цена продажи: §e105 монет",
            "§7Стадии: §b2",
            "§7Время роста: §b1 мин/стадия",
            "§7Посадка: §fПесок",
            "§7Особенности: §fОчень жаростойкий"
        ));

        // Информационная панель
        guideInv.setItem(49, createInfoItem(Material.EMERALD, 
            "§aИнформация", 
            "§7• Все растения растут 1 минуту на стадию",
            "§7• Полив и удобрение ускоряют рост на 20%",
            "§7• Многоразовые растения дают урожай несколько раз",
            "§7• Качество зависит от ухода за растением"
        ));
        
        // Заполняем пустые слоты
        for (int i = 0; i < guideInv.getSize(); i++) {
            if (guideInv.getItem(i) == null) {
                guideInv.setItem(i, createFiller(Material.BLACK_STAINED_GLASS_PANE));
            }
        }
        
        player.openInventory(guideInv);
    }

    public void openEventGuide(Player player) {
        Inventory guideInv = Bukkit.createInventory(null, 27, "§dСправочник событий");

        guideInv.setItem(10, createInfoItem(Material.WHEAT,
            "§aУскоренный рост (Growth Boost)",
            "§7Все растения на сервере растут",
            "§7значительно быстрее.",
            "§7Идеальное время для посадки новых культур!"
        ));

        guideInv.setItem(12, createInfoItem(Material.DIAMOND,
            "§bБогатый урожай (Harvest Boost)",
            "§7При сборе урожая вы получаете",
            "§7гораздо больше плодов.",
            "§7Время собирать созревшие растения!"
        ));

        guideInv.setItem(14, createInfoItem(Material.GOLD_NUGGET,
            "§eМонетный дождь (Coin Rain)",
            "§7Каждые несколько минут все игроки",
            "§7онлайн получают небольшую сумму денег.",
            "§7Просто будьте на сервере, чтобы разбогатеть!"
        ));

        guideInv.setItem(16, createInfoItem(Material.EXPERIENCE_BOTTLE,
            "§dДвойной опыт (XP Boost)",
            "§7За сбор урожая и другие фермерские",
            "§7действия вы получаете в два раза",
            "§7больше опыта для уровня фермера."
        ));

        // Fill empty slots
        for (int i = 0; i < guideInv.getSize(); i++) {
            if (guideInv.getItem(i) == null) {
                guideInv.setItem(i, createFiller(Material.BLACK_STAINED_GLASS_PANE));
            }
        }
        player.openInventory(guideInv);
    }

    private ItemStack createBuyItem(ItemStack item, double price) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList("§fЦена: §e" + price + " " + plugin.getConfigManager().getCurrencyName(), "§aКлик, чтобы купить."));
        // Сохраняем цену в метаданных предмета для надежного получения в листенере
        // В идеале, ключ должен быть статическим полем где-нибудь в ItemManager или ShopGUI
        NamespacedKey priceKey = new NamespacedKey(plugin, "item_price");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(priceKey, PersistentDataType.DOUBLE, price);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createSellItem(ItemStack item, double price) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList("§fЦена: §e" + price + " " + plugin.getConfigManager().getCurrencyName(), "§cКлик, чтобы продать."));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createGuideItem(ItemStack seed, String name, String... lore) {
        ItemStack item = seed.clone();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createInfoItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createFiller() {
        return createFiller(Material.GRAY_STAINED_GLASS_PANE);
    }

    private ItemStack createFiller(Material material) {
        ItemStack filler = new ItemStack(material);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);
        return filler;
    }
} 