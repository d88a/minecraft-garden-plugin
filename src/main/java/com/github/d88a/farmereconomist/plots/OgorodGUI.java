package com.github.d88a.farmereconomist.plots;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class OgorodGUI {

    private final PlotManager plotManager;
    private final FarmerEconomist plugin;

    public OgorodGUI(PlotManager plotManager, FarmerEconomist plugin) {
        this.plotManager = plotManager;
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§2Управление огородом");

        // Create buttons
        gui.setItem(11, createButton(Material.GRASS_BLOCK, "§aПолучить огород", "Нажмите, чтобы получить новый участок."));
        gui.setItem(13, createButton(Material.COMPASS, "§bТелепортироваться домой", "Нажмите, чтобы вернуться на свой участок."));
        gui.setItem(15, createButton(Material.BARRIER, "§cУдалить огород", "ВНИМАНИЕ: Это действие необратимо!"));
        
        // Информация о текущем участке
        if (plotManager.hasPlot(player)) {
            double balance = plugin.getEconomyManager().getBalance(player);
            gui.setItem(22, createButton(Material.SIGN, "§eИнформация об участке", 
                "§fБаланс: §e" + String.format("%.2f", balance) + " " + plugin.getConfigManager().getCurrencyName(),
                "§fУ вас есть участок",
                "§7Используйте /balance для проверки баланса"
            ));
        } else {
            gui.setItem(22, createButton(Material.SIGN, "§eИнформация об участке", 
                "§fУ вас нет участка",
                "§7Получите участок, нажав на травяной блок"
            ));
        }

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