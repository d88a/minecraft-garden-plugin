package com.github.d88a.farmereconomist.plots;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class OgorodGUI {

    private final PlotManager plotManager;

    public OgorodGUI(PlotManager plotManager) {
        this.plotManager = plotManager;
    }

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§2Управление огородом");

        // Create buttons
        gui.setItem(11, createButton(Material.GRASS_BLOCK, "§aПолучить огород", "Нажмите, чтобы получить новый участок."));
        gui.setItem(13, createButton(Material.COMPASS, "§bТелепортироваться домой", "Нажмите, чтобы вернуться на свой участок."));
        gui.setItem(15, createButton(Material.BARRIER, "§cУдалить огород", "ВНИМАНИЕ: Это действие необратимо!"));

        // Fill empty space
        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            }
        }

        player.openInventory(gui);
    }

    private ItemStack createButton(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
} 