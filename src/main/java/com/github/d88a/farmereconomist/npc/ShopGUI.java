package com.github.d88a.farmereconomist.npc;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ShopGUI {

    private final FarmerEconomist plugin;
    private final NamespacedKey itemPriceKey;
    private final Map<String, Supplier<ItemStack>> itemSuppliers = new HashMap<>();

    public ShopGUI(FarmerEconomist plugin) {
        this.plugin = plugin;
        this.itemPriceKey = new NamespacedKey(plugin, "item_price");
        registerItemSuppliers();
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
        ConfigurationSection buyLayout = plugin.getConfig().getConfigurationSection("shop-layout.buy");

        if (buyLayout != null) {
            for (String key : buyLayout.getKeys(false)) {
                int slot = buyLayout.getInt(key + ".slot", -1);
                String itemId = buyLayout.getString(key + ".item-id");

                if (slot != -1 && itemId != null) {
                    Supplier<ItemStack> supplier = itemSuppliers.get(itemId);
                    if (supplier != null) {
                        addItemToBuyMenu(buyInv, slot, supplier.get());
                    } else {
                        plugin.getLogger().warning("Unknown item ID in shop-layout.buy: " + itemId);
                    }
                }
            }
        }

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
        int slot = 10; // Начальный слот

        ConfigurationSection sellLayout = plugin.getConfig().getConfigurationSection("shop-layout.sell");
        if (sellLayout != null) {
            // Предполагается, что ключи в конфиге (item1, item2...) определяют порядок
            for (String key : sellLayout.getKeys(false)) {
                String itemId = sellLayout.getString(key + ".item-id");
                if (itemId != null) {
                    Supplier<ItemStack> supplier = itemSuppliers.get(itemId);
                    if (supplier != null) {
                        slot = addSellableItem(sellInv, player, slot, itemId, supplier);
                    } else {
                        plugin.getLogger().warning("Unknown item ID in shop-layout.sell: " + itemId);
                    }
                }
            }
        }

        // Заполняем пустые слоты
        for (int i = 0; i < sellInv.getSize(); i++) {
            if (sellInv.getItem(i) == null) {
                sellInv.setItem(i, createFiller());
            }
        }
        player.openInventory(sellInv);
    }

    private int addSellableItem(Inventory inv, Player player, int currentSlot, String itemId, Supplier<ItemStack> itemSupplier) {
        if (playerHasItem(player, itemId)) {
            double price = plugin.getConfig().getDouble("shop-prices.sell." + itemId, 0.0);
            if (price > 0) {
                inv.setItem(currentSlot, createSellItem(itemSupplier.get(), price));
                return currentSlot + 1;
            }
        }
        return currentSlot;
    }

    public void openPlantGuide(Player player) {
        Inventory guideInv = Bukkit.createInventory(null, 54, "§aСправочник растений");
        ConfigurationSection guideLayout = plugin.getConfig().getConfigurationSection("plant-guide");
        if (guideLayout != null) {
            for (String key : guideLayout.getKeys(false)) {
                ConfigurationSection itemSection = guideLayout.getConfigurationSection(key);
                if (itemSection == null) continue;

                int slot = itemSection.getInt("slot", -1);
                String seedItemId = itemSection.getString("seed-item-id");
                String title = itemSection.getString("title");
                List<String> lore = itemSection.getStringList("lore");

                if (slot != -1 && seedItemId != null && title != null) {
                    Supplier<ItemStack> supplier = itemSuppliers.get(seedItemId);
                    if (supplier != null) {
                        guideInv.setItem(slot, createGuideItem(supplier.get(), title, lore));
                    }
                }
            }
        }

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
        // Используем ключ, созданный в конструкторе, для сохранения цены в метаданных
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(this.itemPriceKey, PersistentDataType.DOUBLE, price);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createSellItem(ItemStack item, double price) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList("§fЦена: §e" + price + " " + plugin.getConfigManager().getCurrencyName(), "§cКлик, чтобы продать."));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createGuideItem(ItemStack seed, String name, List<String> lore) {
        ItemStack item = seed.clone();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
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

    private void registerItemSuppliers() {
        // Seeds
        itemSuppliers.put("LETTUCE_SEEDS", ItemManager::createLettuceSeeds);
        itemSuppliers.put("TOMATO_SEEDS", ItemManager::createTomatoSeeds);
        itemSuppliers.put("GLOWSHROOM_SPORES", ItemManager::createGlowshroomSpores);
        itemSuppliers.put("STRAWBERRY_SEEDS", ItemManager::createStrawberrySeeds);
        itemSuppliers.put("RADISH_SEEDS", ItemManager::createRadishSeeds);
        itemSuppliers.put("WATERMELON_SEEDS", ItemManager::createWatermelonSeeds);
        itemSuppliers.put("LUNAR_BERRY_SEEDS", ItemManager::createLunarBerrySeeds);
        itemSuppliers.put("RAINBOW_MUSHROOM_SEEDS", ItemManager::createRainbowMushroomSeeds);
        itemSuppliers.put("CRYSTAL_CACTUS_SEEDS", ItemManager::createCrystalCactusSeeds);
        itemSuppliers.put("FLAME_PEPPER_SEEDS", ItemManager::createFlamePepperSeeds);
        itemSuppliers.put("MYSTIC_ROOT_SEEDS", ItemManager::createMysticRootSeeds);
        itemSuppliers.put("STAR_FRUIT_SEEDS", ItemManager::createStarFruitSeeds);
        itemSuppliers.put("PREDATOR_FLOWER_SEEDS", ItemManager::createPredatorFlowerSeeds);
        itemSuppliers.put("ELECTRO_PUMPKIN_SEEDS", ItemManager::createElectroPumpkinSeeds);
        itemSuppliers.put("MANDRAKE_LEAF_SEEDS", ItemManager::createMandrakeLeafSeeds);
        itemSuppliers.put("FLYING_FRUIT_SEEDS", ItemManager::createFlyingFruitSeeds);
        itemSuppliers.put("SNOW_MINT_SEEDS", ItemManager::createSnowMintSeeds);
        itemSuppliers.put("SUN_PINEAPPLE_SEEDS", ItemManager::createSunPineappleSeeds);
        itemSuppliers.put("FOG_BERRY_SEEDS", ItemManager::createFogBerrySeeds);
        itemSuppliers.put("SAND_MELON_SEEDS", ItemManager::createSandMelonSeeds);
        itemSuppliers.put("WITCH_MUSHROOM_SEEDS", ItemManager::createWitchMushroomSeeds);

        // Tools
        itemSuppliers.put("WATERING_CAN", ItemManager::createWateringCan);
        itemSuppliers.put("FERTILIZER", ItemManager::createFertilizer);

        // Products
        itemSuppliers.put("LETTUCE_NORMAL", () -> ItemManager.createLettuce(false));
        itemSuppliers.put("LETTUCE_EXCELLENT", () -> ItemManager.createLettuce(true));
        itemSuppliers.put("TOMATO", ItemManager::createTomato);
        itemSuppliers.put("GLOWSHROOM_DUST", ItemManager::createGlowshroomDust);
        itemSuppliers.put("STRAWBERRY", ItemManager::createStrawberry);
        itemSuppliers.put("RADISH", ItemManager::createRadish);
        itemSuppliers.put("WATERMELON", ItemManager::createWatermelon);
        itemSuppliers.put("LUNAR_BERRY", ItemManager::createLunarBerry);
        itemSuppliers.put("RAINBOW_MUSHROOM", ItemManager::createRainbowMushroom);
        itemSuppliers.put("CRYSTAL_CACTUS", ItemManager::createCrystalCactus);
        itemSuppliers.put("FLAME_PEPPER", ItemManager::createFlamePepper);
        itemSuppliers.put("MYSTIC_ROOT", ItemManager::createMysticRoot);
        itemSuppliers.put("STAR_FRUIT", ItemManager::createStarFruit);
        itemSuppliers.put("PREDATOR_FLOWER", ItemManager::createPredatorFlower);
        itemSuppliers.put("ELECTRO_PUMPKIN", ItemManager::createElectroPumpkin);
        itemSuppliers.put("MANDRAKE_LEAF", ItemManager::createMandrakeLeaf);
        itemSuppliers.put("FLYING_FRUIT", ItemManager::createFlyingFruit);
        itemSuppliers.put("SNOW_MINT", ItemManager::createSnowMint);
        itemSuppliers.put("SUN_PINEAPPLE", ItemManager::createSunPineapple);
        itemSuppliers.put("FOG_BERRY", ItemManager::createFogBerry);
        itemSuppliers.put("SAND_MELON", ItemManager::createSandMelon);
        itemSuppliers.put("WITCH_MUSHROOM", ItemManager::createWitchMushroom);
    }
} 