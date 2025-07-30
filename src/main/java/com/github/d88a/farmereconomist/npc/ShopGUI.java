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
        
        // Кнопка "Купить"
        ItemStack buy = new ItemStack(Material.EMERALD);
        ItemMeta buyMeta = buy.getItemMeta();
        buyMeta.setDisplayName("§aКупить");
        buy.setItemMeta(buyMeta);
        mainMenu.setItem(11, buy);

        // Кнопка "Продать"
        ItemStack sell = new ItemStack(Material.GOLD_INGOT);
        ItemMeta sellMeta = sell.getItemMeta();
        sellMeta.setDisplayName("§6Продать");
        sell.setItemMeta(sellMeta);
        mainMenu.setItem(15, sell);
        
        // Кнопка "Баланс"
        ItemStack balance = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta balanceMeta = balance.getItemMeta();
        balanceMeta.setDisplayName("§eБаланс");
        double playerBalance = plugin.getEconomyManager().getBalance(player);
        balanceMeta.setLore(Arrays.asList("§fВаш баланс: §e" + String.format("%.2f", playerBalance) + " " + plugin.getConfigManager().getCurrencyName()));
        balance.setItemMeta(balanceMeta);
        mainMenu.setItem(13, balance);
        
        // Кнопка "Информация"
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§bИнформация");
        infoMeta.setLore(Arrays.asList(
            "§7Как выращивать растения:",
            "§7• Сажайте семена на подходящую почву",
            "§7• Используйте лейку для полива",
            "§7• Используйте удобрения для ускорения",
            "§7• Собирайте урожай левой кнопкой мыши"
        ));
        info.setItemMeta(infoMeta);
        mainMenu.setItem(22, info);

        player.openInventory(mainMenu);
    }

    public void openBuy(Player player) {
        Inventory buyInv = Bukkit.createInventory(null, 36, "Купить у Мирона");
        buyInv.setItem(10, createBuyItem(ItemManager.createLettuceSeeds(), 10));
        buyInv.setItem(11, createBuyItem(ItemManager.createTomatoSeeds(), 25));
        buyInv.setItem(12, createBuyItem(ItemManager.createGlowshroomSpores(), 50));
        buyInv.setItem(13, createBuyItem(ItemManager.createWateringCan(), 100));
        buyInv.setItem(14, createBuyItem(ItemManager.createFertilizer(), 75));
        buyInv.setItem(15, createBuyItem(ItemManager.createStrawberrySeeds(), 30));
        buyInv.setItem(16, createBuyItem(ItemManager.createRadishSeeds(), 20));
        buyInv.setItem(17, createBuyItem(ItemManager.createWatermelonSeeds(), 40));
        buyInv.setItem(18, createBuyItem(ItemManager.createLunarBerrySeeds(), 60));
        buyInv.setItem(19, createBuyItem(ItemManager.createRainbowMushroomSeeds(), 45));
        buyInv.setItem(20, createBuyItem(ItemManager.createCrystalCactusSeeds(), 35));
        buyInv.setItem(21, createBuyItem(ItemManager.createFlamePepperSeeds(), 55));
        buyInv.setItem(22, createBuyItem(ItemManager.createMysticRootSeeds(), 65));
        buyInv.setItem(23, createBuyItem(ItemManager.createStarFruitSeeds(), 50));
        buyInv.setItem(24, createBuyItem(ItemManager.createPredatorFlowerSeeds(), 70));
        buyInv.setItem(25, createBuyItem(ItemManager.createElectroPumpkinSeeds(), 80));
        buyInv.setItem(26, createBuyItem(ItemManager.createMandrakeLeafSeeds(), 40));
        buyInv.setItem(27, createBuyItem(ItemManager.createFlyingFruitSeeds(), 90));
        buyInv.setItem(28, createBuyItem(ItemManager.createSnowMintSeeds(), 25));
        buyInv.setItem(29, createBuyItem(ItemManager.createSunPineappleSeeds(), 85));
        buyInv.setItem(30, createBuyItem(ItemManager.createFogBerrySeeds(), 30));
        buyInv.setItem(31, createBuyItem(ItemManager.createSandMelonSeeds(), 70));
        buyInv.setItem(32, createBuyItem(ItemManager.createWitchMushroomSeeds(), 95));
        
        // Кнопка "Назад"
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§cНазад");
        backMeta.setLore(Arrays.asList("§7Вернуться в главное меню"));
        backButton.setItemMeta(backMeta);
        buyInv.setItem(31, backButton);
        
        for (int i = 0; i < buyInv.getSize(); i++) {
            if (buyInv.getItem(i) == null) {
                buyInv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            }
        }
        player.openInventory(buyInv);
    }

    public void openSell(Player player) {
        Inventory sellInv = Bukkit.createInventory(null, 36, "Продать Мирону");
        // Показываем только то, что есть у игрока и можно продать
        int slot = 10;
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
        if (player.getInventory().containsAtLeast(ItemManager.createStrawberry(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createStrawberry(), 25));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createRadish(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createRadish(), 15));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createWatermelon(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createWatermelon(), 35));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createLunarBerry(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createLunarBerry(), 50));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createRainbowMushroom(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createRainbowMushroom(), 40));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createCrystalCactus(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createCrystalCactus(), 30));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createFlamePepper(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createFlamePepper(), 45));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createMysticRoot(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createMysticRoot(), 55));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createStarFruit(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createStarFruit(), 40));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createPredatorFlower(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createPredatorFlower(), 60));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createElectroPumpkin(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createElectroPumpkin(), 70));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createMandrakeLeaf(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createMandrakeLeaf(), 35));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createFlyingFruit(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createFlyingFruit(), 80));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createSnowMint(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createSnowMint(), 20));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createSunPineapple(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createSunPineapple(), 75));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createFogBerry(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createFogBerry(), 25));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createSandMelon(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createSandMelon(), 60));
        }
        if (player.getInventory().containsAtLeast(ItemManager.createWitchMushroom(), 1)) {
            sellInv.setItem(slot++, createSellItem(ItemManager.createWitchMushroom(), 85));
        }
        
        // Кнопка "Назад"
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§cНазад");
        backMeta.setLore(Arrays.asList("§7Вернуться в главное меню"));
        backButton.setItemMeta(backMeta);
        sellInv.setItem(31, backButton);
        
        for (int i = 0; i < sellInv.getSize(); i++) {
            if (sellInv.getItem(i) == null) {
                sellInv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            }
        }
        player.openInventory(sellInv);
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
} 