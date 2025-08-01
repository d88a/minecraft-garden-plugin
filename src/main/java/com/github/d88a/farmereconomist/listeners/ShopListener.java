package com.github.d88a.farmereconomist.listeners;

import com.github.d88a.farmereconomist.FarmerEconomist;
import com.github.d88a.farmereconomist.config.ConfigManager;
import com.github.d88a.farmereconomist.economy.EconomyManager;
import com.github.d88a.farmereconomist.items.ItemManager;
import com.github.d88a.farmereconomist.npc.ShopGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.NamespacedKey;

public class ShopListener implements Listener {

    private final FarmerEconomist plugin;
    private final ShopGUI shopGUI;
    private final EconomyManager economyManager;
    private final NamespacedKey itemIdKey;
    private final NamespacedKey itemPriceKey;

    private final Map<String, Double> sellPrices = new HashMap<>();

    public ShopListener(FarmerEconomist plugin) {
        this.plugin = plugin;
        this.shopGUI = new ShopGUI(plugin);
        this.economyManager = plugin.getEconomyManager();
        // Используем статический ключ из ItemManager
        this.itemIdKey = ItemManager.ITEM_ID_KEY;
        this.itemPriceKey = new NamespacedKey(plugin, "item_price");
        loadPrices();
    }

    // Загружаем цены продажи из config.yml
    private void loadPrices() {
        org.bukkit.configuration.ConfigurationSection sellPricesSection = plugin.getConfig().getConfigurationSection("shop-prices.sell");
        if (sellPricesSection != null) {
            for (String key : sellPricesSection.getKeys(false)) {
                sellPrices.put(key, sellPricesSection.getDouble(key));
            }
        }
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
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE || clickedItem.getType() == Material.BLACK_STAINED_GLASS_PANE) {
            event.setCancelled(true);
            return;
        }

        switch (title) {
            case "Магазин Старого Мирона":
                handleMainMenuClick(event, player, clickedItem);
                break;
            case "Купить у Мирона":
                handleBuyMenuClick(event, player, clickedItem);
                break;
            case "Продать Мирону":
                handleSellMenuClick(event, player, clickedItem);
                break;
            case "§dСправочник событий":
            case "§aСправочник растений":
                event.setCancelled(true);
                break;
        }
    }

    private void handleMainMenuClick(InventoryClickEvent event, Player player, ItemStack clickedItem) {
        event.setCancelled(true);
        // Делаем проверку более точной, по имени предмета, чтобы избежать конфликтов
        ItemMeta clickedMeta = clickedItem.getItemMeta();
        if (clickedMeta == null) return;

        if (clickedItem.getType() == Material.EMERALD && clickedMeta.getDisplayName().equals("§aКупить")) {
            shopGUI.openBuy(player);
        } else if (clickedItem.getType() == Material.GOLD_INGOT && clickedMeta.getDisplayName().equals("§6Продать")) {
            shopGUI.openSell(player);
        } else if (clickedItem.getType() == Material.BOOK && clickedMeta.getDisplayName().equals("§eСправочник растений")) {
            shopGUI.openPlantGuide(player);
        } else if (clickedItem.getType() == Material.BEACON && clickedMeta.getDisplayName().equals("§dИгровые события")) {
            shopGUI.openEventGuide(player);
        }
    }

    private void handleBuyMenuClick(InventoryClickEvent event, Player player, ItemStack clickedItem) {
        event.setCancelled(true);
        if (clickedItem.getItemMeta() != null && clickedItem.getItemMeta().getLore() != null && clickedItem.getItemMeta().getLore().size() > 1 && clickedItem.getItemMeta().getLore().get(1).contains("купить")) {
            // Получаем цену из метаданных, а не из лора. Это надежнее.
            ItemMeta meta = clickedItem.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();

            if (container.has(itemPriceKey, PersistentDataType.DOUBLE)) {
                double price = container.get(itemPriceKey, PersistentDataType.DOUBLE);
                if (economyManager.getBalance(player) >= price) {
                    economyManager.takeBalance(player, price);
                    ItemStack itemToGive = clickedItem.clone();
                    itemToGive.setLore(new ArrayList<>());
                    // Очищаем цену из метаданных копии, чтобы не засорять инвентарь
                    ItemMeta toGiveMeta = itemToGive.getItemMeta();
                    toGiveMeta.getPersistentDataContainer().remove(itemPriceKey);
                    itemToGive.setItemMeta(toGiveMeta);

                    player.getInventory().addItem(itemToGive);
                    plugin.getConfigManager().sendMessage(player, "shop_buy_success", "%item_name%", clickedItem.getItemMeta().getDisplayName());
                    plugin.getSoundManager().playSound(player, "buy_item");
                    plugin.getPlotManager().updatePlotSign(player);
                } else {
                    plugin.getConfigManager().sendMessage(player, "shop_buy_fail_no_money");
                }
            }
        }
    }

    private void handleSellMenuClick(InventoryClickEvent event, Player player, ItemStack clickedItem) {
        event.setCancelled(true);
        if (clickedItem == null || clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
            return;
        }

        // Нас интересуют только клики в верхнем инвентаре (GUI магазина)
        if (event.getClickedInventory() != player.getOpenInventory().getTopInventory()) {
            return;
        }

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null) return;

        String itemId = meta.getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING);
        if (itemId == null || !sellPrices.containsKey(itemId)) {
            return; // Это не предмет, который можно продать через это GUI
        }

        double pricePerItem = sellPrices.get(itemId);
        int totalAmountSold = 0;

        // Ищем и удаляем все предметы этого типа из инвентаря игрока
        ItemStack[] contents = player.getInventory().getStorageContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack inventoryItem = contents[i];
            if (inventoryItem != null && !inventoryItem.getType().isAir()) {
                ItemMeta invItemMeta = inventoryItem.getItemMeta();
                if (invItemMeta != null && itemId.equals(invItemMeta.getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING))) {
                    totalAmountSold += inventoryItem.getAmount();
                    player.getInventory().setItem(i, null); // Удаляем стак
                }
            }
        }

        if (totalAmountSold > 0) {
            double totalEarnings = pricePerItem * totalAmountSold;
            economyManager.addBalance(player, totalEarnings);
            plugin.getConfigManager().sendMessage(player, "shop_sell_success", "%item_name%", meta.getDisplayName());
            plugin.getSoundManager().playSound(player, "sell_item");
            plugin.getPlotManager().updatePlotSign(player);
            shopGUI.openSell(player); // Обновляем GUI после продажи
        } else {
            plugin.getConfigManager().sendMessage(player, "shop_sell_fail_no_item");
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (title.equals("Продать Мирону")) {
            event.setCancelled(true);
        }
    }
} 