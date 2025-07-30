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
import org.bukkit.event.inventory.InventoryDragEvent;
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
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) return;

        // Главное меню магазина
        if (title.equals("Магазин Старого Мирона")) {
            event.setCancelled(true);
            if (clickedItem.getType() == Material.EMERALD) {
                shopGUI.openBuy(player);
            } else if (clickedItem.getType() == Material.GOLD_INGOT) {
                shopGUI.openSell(player);
            } else if (clickedItem.getType() == Material.GOLD_NUGGET) {
                // Обновляем баланс в меню
                shopGUI.open(player);
            } else if (clickedItem.getType() == Material.BOOK) {
                // Информация - ничего не делаем, просто показываем
                // Можно добавить звук или эффект
                plugin.getSoundManager().playSound(player, "teleport");
            }
            return;
        }
        
        // Окно покупки
        if (title.equals("Купить у Мирона")) {
            event.setCancelled(true);
            
            // Обработка кнопки "Назад"
            if (clickedItem.getType() == Material.BARRIER && 
                clickedItem.getItemMeta() != null && 
                clickedItem.getItemMeta().getDisplayName().equals("§cНазад")) {
                shopGUI.open(player);
                return;
            }
            
            if (clickedItem.getItemMeta() != null && clickedItem.getItemMeta().getLore() != null && clickedItem.getItemMeta().getLore().get(1).contains("купить")) {
                String priceString = ChatColor.stripColor(clickedItem.getItemMeta().getLore().get(0).split(" ")[1]);
                double price = Double.parseDouble(priceString);
                if (economyManager.getBalance(player) >= price) {
                    economyManager.takeBalance(player, price);
                    ItemStack itemToGive = clickedItem.clone();
                    itemToGive.setLore(null);
                    player.getInventory().addItem(itemToGive);
                    plugin.getConfigManager().sendMessage(player, "shop_buy_success", "%item_name%", clickedItem.getItemMeta().getDisplayName());
                    plugin.getSoundManager().playSound(player, "buy_item");
                    plugin.getPlotManager().updatePlotSign(player);
                } else {
                    plugin.getConfigManager().sendMessage(player, "shop_buy_fail_no_money");
                }
            }
            return;
        }
        
        // Окно продажи
        if (title.equals("Продать Мирону")) {
            event.setCancelled(true);
            
            // Обработка кнопки "Назад"
            if (clickedItem.getType() == Material.BARRIER && 
                clickedItem.getItemMeta() != null && 
                clickedItem.getItemMeta().getDisplayName().equals("§cНазад")) {
                shopGUI.open(player);
                return;
            }
            
            if (clickedItem.getItemMeta() != null && clickedItem.getItemMeta().getLore() != null && clickedItem.getItemMeta().getLore().get(1).contains("продать")) {
                String priceString = ChatColor.stripColor(clickedItem.getItemMeta().getLore().get(0).split(" ")[1]);
                double price = Double.parseDouble(priceString);
                ItemStack itemToSell = clickedItem.clone();
                itemToSell.setAmount(1);
                itemToSell.setLore(null);
                if(player.getInventory().containsAtLeast(itemToSell, 1)) {
                    player.getInventory().removeItem(itemToSell);
                    economyManager.addBalance(player, price);
                    plugin.getConfigManager().sendMessage(player, "shop_sell_success", "%item_name%", clickedItem.getItemMeta().getDisplayName());
                    plugin.getSoundManager().playSound(player, "sell_item");
                    plugin.getPlotManager().updatePlotSign(player);
                    // Обновить окно продажи
                    shopGUI.openSell(player);
                } else {
                    plugin.getConfigManager().sendMessage(player, "shop_sell_fail_no_item");
                }
            }
            return;
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        if (title.equals("Продать Мирону")) {
            // Проверяем, что предмет перетаскивается в окно продажи
            for (int slot : event.getRawSlots()) {
                if (slot < event.getView().getTopInventory().getSize()) {
                    // Получаем предмет, который игрок пытается продать
                    ItemStack dragged = event.getOldCursor();
                    if (dragged == null || dragged.getType() == Material.AIR) continue;
                    // Проверяем, можно ли продать этот предмет
                    double price = 0;
                    String itemName = null;
                    if (ItemManager.createLettuce(false).isSimilar(dragged)) {
                        price = 5; itemName = "Салат";
                    } else if (ItemManager.createLettuce(true).isSimilar(dragged)) {
                        price = 15; itemName = "Большой салат";
                    } else if (ItemManager.createTomato().isSimilar(dragged)) {
                        price = 20; itemName = "Томат";
                    } else if (ItemManager.createGlowshroomDust().isSimilar(dragged)) {
                        price = 45; itemName = "Светящийся грибной порошок";
                    } else if (ItemManager.createStrawberry().isSimilar(dragged)) {
                        price = 25; itemName = "Лучезарная Клубника";
                    } else if (ItemManager.createRadish().isSimilar(dragged)) {
                        price = 15; itemName = "Хрустящий Редис";
                    } else if (ItemManager.createWatermelon().isSimilar(dragged)) {
                        price = 35; itemName = "Пустынный Арбуз";
                    } else if (ItemManager.createLunarBerry().isSimilar(dragged)) {
                        price = 50; itemName = "Лунная ягода";
                    } else if (ItemManager.createRainbowMushroom().isSimilar(dragged)) {
                        price = 40; itemName = "Радужный гриб";
                    } else if (ItemManager.createCrystalCactus().isSimilar(dragged)) {
                        price = 30; itemName = "Кристальный кактус";
                    } else if (ItemManager.createFlamePepper().isSimilar(dragged)) {
                        price = 45; itemName = "Пылающий перец";
                    } else if (ItemManager.createMysticRoot().isSimilar(dragged)) {
                        price = 55; itemName = "Мистический корень";
                    } else if (ItemManager.createStarFruit().isSimilar(dragged)) {
                        price = 40; itemName = "Звёздный плод";
                    } else if (ItemManager.createPredatorFlower().isSimilar(dragged)) {
                        price = 60; itemName = "Цветок-хищник";
                    } else if (ItemManager.createElectroPumpkin().isSimilar(dragged)) {
                        price = 70; itemName = "Электро-тыква";
                    } else if (ItemManager.createMandrakeLeaf().isSimilar(dragged)) {
                        price = 35; itemName = "Листья мандрагоры";
                    } else if (ItemManager.createFlyingFruit().isSimilar(dragged)) {
                        price = 80; itemName = "Летающий плод";
                    } else if (ItemManager.createSnowMint().isSimilar(dragged)) {
                        price = 20; itemName = "Снежная мята";
                    } else if (ItemManager.createSunPineapple().isSimilar(dragged)) {
                        price = 75; itemName = "Солнечный ананас";
                    } else if (ItemManager.createFogBerry().isSimilar(dragged)) {
                        price = 25; itemName = "Туманная ягода";
                    } else if (ItemManager.createSandMelon().isSimilar(dragged)) {
                        price = 60; itemName = "Песчаный арбуз";
                    } else if (ItemManager.createWitchMushroom().isSimilar(dragged)) {
                        price = 85; itemName = "Ведьмин гриб";
                    }
                    if (price > 0 && player.getInventory().containsAtLeast(dragged, dragged.getAmount())) {
                        player.getInventory().removeItem(new ItemStack(dragged.getType(), dragged.getAmount()));
                        plugin.getEconomyManager().addBalance(player, price * dragged.getAmount());
                        plugin.getConfigManager().sendMessage(player, "shop_sell_success", "%item_name%", itemName);
                        plugin.getSoundManager().playSound(player, "sell_item");
                        plugin.getPlotManager().updatePlotSign(player);
                        shopGUI.openSell(player);
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
} 