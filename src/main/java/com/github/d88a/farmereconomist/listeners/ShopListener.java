package com.github.d88a.farmereconomist.listeners;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.economy.EconomyManager;
import com.github.d88a.farmereconomist.items.ItemManager;
import com.github.d88a.farmereconomist.npc.ShopGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ShopListener implements Listener {

    private final FarmerEconomist plugin;
    private final ShopGUI shopGUI;
    private final EconomyManager economyManager;

    public ShopListener(FarmerEconomist plugin) {
        this.plugin = plugin;
        this.shopGUI = new ShopGUI(plugin);
        this.economyManager = plugin.getEconomyManager();
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() == EntityType.VILLAGER) {
            UUID npcId = plugin.getNpcManager().getNpcUniqueId();
            if (npcId != null && event.getRightClicked().getUniqueId().equals(npcId)) {
                event.setCancelled(true);
                shopGUI.open(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Магазин Старого Мирона")) {
            return;
        }
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
            return;
        }

        // --- Buy logic ---
        if (clickedItem.getItemMeta().getLore().get(1).contains("купить")) {
            String priceString = ChatColor.stripColor(clickedItem.getItemMeta().getLore().get(0).split(" ")[1]);
            double price = Double.parseDouble(priceString);

            if (economyManager.getBalance(player) >= price) {
                economyManager.takeBalance(player, price);
                ItemStack itemToGive = clickedItem.clone();
                itemToGive.setLore(null);
                player.getInventory().addItem(itemToGive);
                plugin.getConfigManager().sendMessage(player, "shop_buy_success", "%item_name%", clickedItem.getItemMeta().getDisplayName());
            } else {
                plugin.getConfigManager().sendMessage(player, "shop_buy_fail_no_money");
            }
        }
        // --- Sell logic ---
        else if (clickedItem.getItemMeta().getLore().get(1).contains("продать")) {
            String priceString = ChatColor.stripColor(clickedItem.getItemMeta().getLore().get(0).split(" ")[1]);
            double price = Double.parseDouble(priceString);
            
            ItemStack itemToSell = clickedItem.clone();
            itemToSell.setAmount(1);
            itemToSell.setLore(null);
            
            if(player.getInventory().containsAtLeast(itemToSell, 1)) {
                player.getInventory().removeItem(itemToSell);
                economyManager.addBalance(player, price);
                plugin.getConfigManager().sendMessage(player, "shop_sell_success", "%item_name%", clickedItem.getItemMeta().getDisplayName());
            } else {
                plugin.getConfigManager().sendMessage(player, "shop_sell_fail_no_item");
            }
        }
    }
} 