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
        Inventory shop = Bukkit.createInventory(null, 27, "Магазин Старого Мирона");

        // --- Items to buy ---
        shop.setItem(10, createBuyItem(ItemManager.createLettuceSeeds(), 10));
        shop.setItem(11, createBuyItem(ItemManager.createWateringCan(), 100));

        // --- Items to sell ---
        shop.setItem(15, createSellItem(ItemManager.createLettuce(false), 5));
        shop.setItem(16, createSellItem(ItemManager.createLettuce(true), 15));
        
        // --- Fill empty space ---
        for (int i = 0; i < shop.getSize(); i++) {
            if (shop.getItem(i) == null) {
                shop.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            }
        }

        player.openInventory(shop);
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