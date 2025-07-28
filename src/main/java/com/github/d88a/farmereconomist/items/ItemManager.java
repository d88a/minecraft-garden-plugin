package com.github.d88a.farmereconomist.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ItemManager {

    public static ItemStack createWateringCan() {
        ItemStack item = new ItemStack(Material.IRON_HOE); // Используем мотыгу как основу
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bЛейка");
        meta.setLore(Arrays.asList("Наполните водой, чтобы поливать растения."));
        meta.setCustomModelData(1); // Уникальный ID для ресурс-пака
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
} 