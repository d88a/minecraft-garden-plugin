package com.github.d88a.farmereconomist.npc;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

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

        // Заполняем пустые слоты
        for (int i = 0; i < mainMenu.getSize(); i++) {
            if (mainMenu.getItem(i) == null) {
                mainMenu.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            }
        }

        player.openInventory(mainMenu);
    }

    public void openBuy(Player player) {
        Inventory buyInv = Bukkit.createInventory(null, 54, "Купить у Мирона");
        
        // Базовые растения
        buyInv.setItem(10, createBuyItem(ItemManager.createLettuceSeeds(), 10));
        buyInv.setItem(11, createBuyItem(ItemManager.createTomatoSeeds(), 25));
        buyInv.setItem(12, createBuyItem(ItemManager.createGlowshroomSpores(), 50));
        
        // Новые растения - первая строка
        buyInv.setItem(13, createBuyItem(ItemManager.createStrawberrySeeds(), 75));
        buyInv.setItem(14, createBuyItem(ItemManager.createRadishSeeds(), 30));
        buyInv.setItem(15, createBuyItem(ItemManager.createWatermelonSeeds(), 100));
        buyInv.setItem(16, createBuyItem(ItemManager.createLunarBerrySeeds(), 150));
        
        // Вторая строка
        buyInv.setItem(19, createBuyItem(ItemManager.createRainbowMushroomSeeds(), 80));
        buyInv.setItem(20, createBuyItem(ItemManager.createCrystalCactusSeeds(), 120));
        buyInv.setItem(21, createBuyItem(ItemManager.createFlamePepperSeeds(), 90));
        buyInv.setItem(22, createBuyItem(ItemManager.createMysticRootSeeds(), 200));
        
        // Третья строка
        buyInv.setItem(23, createBuyItem(ItemManager.createStarFruitSeeds(), 180));
        buyInv.setItem(24, createBuyItem(ItemManager.createPredatorFlowerSeeds(), 250));
        buyInv.setItem(25, createBuyItem(ItemManager.createElectroPumpkinSeeds(), 160));
        buyInv.setItem(26, createBuyItem(ItemManager.createMandrakeLeafSeeds(), 110));
        
        // Четвертая строка
        buyInv.setItem(28, createBuyItem(ItemManager.createFlyingFruitSeeds(), 140));
        buyInv.setItem(29, createBuyItem(ItemManager.createSnowMintSeeds(), 95));
        buyInv.setItem(30, createBuyItem(ItemManager.createSunPineappleSeeds(), 220));
        buyInv.setItem(31, createBuyItem(ItemManager.createFogBerrySeeds(), 85));
        
        // Пятая строка
        buyInv.setItem(32, createBuyItem(ItemManager.createSandMelonSeeds(), 130));
        buyInv.setItem(33, createBuyItem(ItemManager.createWitchMushroomSeeds(), 175));
        
        // Инструменты
        buyInv.setItem(37, createBuyItem(ItemManager.createWateringCan(), 100));
        buyInv.setItem(38, createBuyItem(ItemManager.createFertilizer(), 50));
        
        // Заполняем пустые слоты
        for (int i = 0; i < buyInv.getSize(); i++) {
            if (buyInv.getItem(i) == null) {
                buyInv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            }
        }
        player.openInventory(buyInv);
    }

    public void openSell(Player player) {
        Inventory sellInv = Bukkit.createInventory(null, 54, "Продать Мирону");
        
        int slot = 10;
        
        // Базовые растения
        if (player.getInventory().containsAtLeast(ItemManager.createLettuce(false), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createLettuce(false), 5));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createLettuce(true), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createLettuce(true), 15));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createTomato(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createTomato(), 20));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createGlowshroomDust(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createGlowshroomDust(), 45));
        }
        
        // Новые растения - первая строка
        if (player.getInventory().containsAtLeast(ItemManager.createStrawberry(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createStrawberry(), 60));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createRadish(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createRadish(), 25));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createWatermelon(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createWatermelon(), 80));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createLunarBerry(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createLunarBerry(), 120));
        }
        
        // Вторая строка
        if (player.getInventory().containsAtLeast(ItemManager.createRainbowMushroom(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createRainbowMushroom(), 65));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createCrystalCactus(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createCrystalCactus(), 100));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createFlamePepper(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createFlamePepper(), 75));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createMysticRoot(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createMysticRoot(), 160));
        }
        
        // Третья строка
        if (player.getInventory().containsAtLeast(ItemManager.createStarFruit(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createStarFruit(), 145));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createPredatorFlower(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createPredatorFlower(), 200));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createElectroPumpkin(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createElectroPumpkin(), 130));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createMandrakeLeaf(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createMandrakeLeaf(), 90));
        }
        
        // Четвертая строка
        if (player.getInventory().containsAtLeast(ItemManager.createFlyingFruit(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createFlyingFruit(), 115));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createSnowMint(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createSnowMint(), 80));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createSunPineapple(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createSunPineapple(), 180));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createFogBerry(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createFogBerry(), 70));
        }
        
        // Пятая строка
        if (player.getInventory().containsAtLeast(ItemManager.createSandMelon(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createSandMelon(), 105));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createWitchMushroom(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createWitchMushroom(), 140));
        }
        
        // Заполняем пустые слоты
        for (int i = 0; i < sellInv.getSize(); i++) {
            if (sellInv.getItem(i) == null) {
                sellInv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
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
                guideInv.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            }
        }
        
        player.openInventory(guideInv);
    }

    private ItemStack createBuyItem(ItemStack item, double price) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList("§fЦена: §e" + price + " " + plugin.getConfigManager().getCurrencyName(), "§aКлик, чтобы купить."));
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
} 