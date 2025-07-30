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
        mainMenu.setItem(11, buy);

        ItemStack sell = new ItemStack(Material.GOLD_INGOT);
        ItemMeta sellMeta = sell.getItemMeta();
        sellMeta.setDisplayName("§6Продать");
        sell.setItemMeta(sellMeta);
        mainMenu.setItem(15, sell);

        player.openInventory(mainMenu);
    }

    public void openBuy(Player player) {
        Inventory buyInv = Bukkit.createInventory(null, 36, "Купить у Мирона");
        buyInv.setItem(10, createBuyItem(ItemManager.createLettuceSeeds(), 10));
        buyInv.setItem(11, createBuyItem(ItemManager.createTomatoSeeds(), 25));
        buyInv.setItem(12, createBuyItem(ItemManager.createGlowshroomSpores(), 50));
        buyInv.setItem(13, createBuyItem(ItemManager.createWateringCan(), 100));
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