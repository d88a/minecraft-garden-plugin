package com.github.d88a.farmereconomist.listeners;

import com.github.d88a.farmereconomist.FarmerEconomist;
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

    // В идеале, это должно загружаться из конфига
    private final Map<String, Double> sellPrices = new HashMap<>();

    public ShopListener(FarmerEconomist plugin) {
        this.plugin = plugin;
        this.shopGUI = new ShopGUI(plugin);
        this.economyManager = plugin.getEconomyManager();
        this.itemIdKey = new NamespacedKey(plugin, "item_id");
        loadPrices();
    }

    // В идеале, это должно загружаться из конфига
    private void loadPrices() {
        sellPrices.put("LETTUCE_NORMAL", 5.0);
        sellPrices.put("LETTUCE_EXCELLENT", 15.0);
        sellPrices.put("TOMATO", 20.0);
        sellPrices.put("GLOWSHROOM_DUST", 45.0);
        sellPrices.put("STRAWBERRY", 60.0);
        sellPrices.put("RADISH", 25.0);
        sellPrices.put("WATERMELON", 80.0);
        sellPrices.put("LUNAR_BERRY", 120.0);
        sellPrices.put("RAINBOW_MUSHROOM", 65.0);
        sellPrices.put("CRYSTAL_CACTUS", 100.0);
        sellPrices.put("FLAME_PEPPER", 75.0);
        sellPrices.put("MYSTIC_ROOT", 160.0);
        sellPrices.put("STAR_FRUIT", 145.0);
        sellPrices.put("PREDATOR_FLOWER", 200.0);
        sellPrices.put("ELECTRO_PUMPKIN", 130.0);
        sellPrices.put("MANDRAKE_LEAF", 90.0);
        sellPrices.put("FLYING_FRUIT", 115.0);
        sellPrices.put("SNOW_MINT", 80.0);
        sellPrices.put("SUN_PINEAPPLE", 180.0);
        sellPrices.put("FOG_BERRY", 70.0);
        sellPrices.put("SAND_MELON", 105.0);
        sellPrices.put("WITCH_MUSHROOM", 140.0);
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

        // Главное меню магазина
        if (title.equals("Магазин Старого Мирона")) {
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
        } else if (title.equals("Купить у Мирона")) { // Окно покупки
            event.setCancelled(true);
            if (clickedItem.getItemMeta() != null && clickedItem.getItemMeta().getLore() != null && clickedItem.getItemMeta().getLore().size() > 1 && clickedItem.getItemMeta().getLore().get(1).contains("купить")) {
                String priceString = ChatColor.stripColor(clickedItem.getItemMeta().getLore().get(0).split(" ")[1]);
                try {
                    double price = Double.parseDouble(priceString);
                    if (economyManager.getBalance(player) >= price) {
                        economyManager.takeBalance(player, price);
                        ItemStack itemToGive = clickedItem.clone();
                        itemToGive.setLore(new ArrayList<>());
                        player.getInventory().addItem(itemToGive);
                        plugin.getConfigManager().sendMessage(player, "shop_buy_success", "%item_name%", clickedItem.getItemMeta().getDisplayName());
                        plugin.getSoundManager().playSound(player, "buy_item");
                        plugin.getPlotManager().updatePlotSign(player);
                    } else {
                        plugin.getConfigManager().sendMessage(player, "shop_buy_fail_no_money");
                    }
                } catch (NumberFormatException ignored) {
                    // Игнорируем, если цена не является числом
                }
            }
        } else if (title.equals("Продать Мирону")) { // Окно продажи
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
        } else if (title.equals("§dСправочник событий") || title.equals("§aСправочник растений")) {
            event.setCancelled(true);
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