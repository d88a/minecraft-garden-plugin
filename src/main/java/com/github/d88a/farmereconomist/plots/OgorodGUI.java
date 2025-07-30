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

    public OgorodGUI(PlotManager plotManager) {
        this.plotManager = plotManager;
        this.plugin = plotManager.getPlugin();
    }

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, "§2Управление огородом");

        // Create buttons
        if (!plotManager.hasPlot(player)) {
            gui.setItem(13, createButton(Material.GRASS_BLOCK, "§aПолучить огород", "Нажмите, чтобы получить новый участок."));
        } else {
            gui.setItem(11, createButton(Material.COMPASS, "§bТелепортироваться домой", "Нажмите, чтобы вернуться на свой участок."));
            gui.setItem(13, createButton(Material.BARRIER, "§cУдалить огород", "ВНИМАНИЕ: Это действие необратимо!"));
            
            // Кнопка расширения участка
            Plot plot = plotManager.getPlot(player);
            if (plot != null && plot.canExpand()) {
                int cost = Plot.getExpansionCost(plot.getLevel());
                String expandLore = "§fСтоимость: §e" + cost + " " + plugin.getConfigManager().getCurrencyName();
                if (!plot.hasMinimumTime()) {
                    expandLore += "\n§cТребуется владеть участком 24 часа";
                }
                gui.setItem(15, createButton(Material.GOLDEN_SHOVEL, "§6Расширить участок", 
                    expandLore,
                    "§7Текущий размер: " + plot.getSize() + "x" + plot.getSize(),
                    "§7Новый размер: " + Plot.getSizeForLevel(plot.getLevel() + 1) + "x" + Plot.getSizeForLevel(plot.getLevel() + 1)
                ));
            } else if (plot != null && !plot.canExpand()) {
                gui.setItem(15, createButton(Material.BARRIER, "§cМаксимальный размер", "Ваш участок уже максимального размера!"));
            }
        }
        
        // Информация о текущем участке
        if (plotManager.hasPlot(player)) {
            Plot plot = plotManager.getPlot(player);
            double balance = plugin.getEconomyManager().getBalance(player);
            gui.setItem(31, createButton(Material.OAK_SIGN, "§eИнформация об участке", 
                "§fБаланс: §e" + String.format("%.2f", balance) + " " + plugin.getConfigManager().getCurrencyName(),
                "§fУровень: §a" + plot.getLevel(),
                "§fРазмер: §b" + plot.getSize() + "x" + plot.getSize(),
                "§7Время владения: " + formatTimeOwned(plot.getCreationTime())
            ));
        } else {
            gui.setItem(31, createButton(Material.OAK_SIGN, "§eИнформация об участке", 
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

    private String formatTimeOwned(long creationTime) {
        long timeOwned = System.currentTimeMillis() - creationTime;
        long hours = timeOwned / (1000 * 60 * 60);
        long minutes = (timeOwned % (1000 * 60 * 60)) / (1000 * 60);
        
        if (hours > 0) {
            return hours + "ч " + minutes + "м";
        } else {
            return minutes + "м";
        }
    }
} 