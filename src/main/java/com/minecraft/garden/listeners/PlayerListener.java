package com.minecraft.garden.listeners;

import com.minecraft.garden.GardenPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
    
    private final GardenPlugin plugin;
    
    public PlayerListener(GardenPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Инициализируем игрока в экономике
        plugin.getEconomyManager().initializePlayer(player);
        
        // Если у игрока нет участка, предлагаем создать
        if (!plugin.getPlotManager().hasPlot(player.getUniqueId())) {
            player.sendMessage("§aДобро пожаловать в игру 'Вырасти сад'!");
            player.sendMessage("§eИспользуйте §6/garden §eдля получения участка и начала игры.");
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = event.getItemInHand();
        
        // Проверяем, что это кастомное семя
        if (plugin.getCustomItemManager().isCustomSeed(item)) {
            // Отменяем стандартное размещение блока
            event.setCancelled(true);
            
            // Убираем один предмет из руки
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
            
            // Получаем базовый материал для посадки
            Material baseMaterial = plugin.getCustomItemManager().getBaseMaterial(item);
            
            // Пытаемся посадить семя
            boolean success = plugin.getPlantManager().plantSeed(player, baseMaterial, block.getLocation());
            
            if (success) {
                player.sendMessage("§aСемя посажено! Растение будет готово через некоторое время.");
            } else {
                // Возвращаем предмет, если посадка не удалась
                player.getInventory().addItem(item);
                player.sendMessage("§cНе удалось посадить семя! Убедитесь, что вы на своём участке и земля вспахана.");
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        
        if (block == null) return;
        
        // Проверяем, что игрок кликает правой кнопкой мыши
        if (!event.getAction().toString().contains("RIGHT_CLICK")) return;
        
        // Проверяем, что это растение
        if (isCrop(block.getType())) {
            // Пытаемся собрать урожай
            boolean success = plugin.getPlantManager().harvestCrop(player, block.getLocation());
            
            if (success) {
                // Отменяем стандартное взаимодействие
                event.setCancelled(true);
            }
        }
    }
    
    private boolean isSeed(Material material) {
        return material == Material.WHEAT_SEEDS ||
               material == Material.CARROT ||
               material == Material.POTATO ||
               material == Material.BEETROOT_SEEDS ||
               material == Material.PUMPKIN_SEEDS ||
               material == Material.MELON_SEEDS;
    }
    
    private boolean isCrop(Material material) {
        return material == Material.WHEAT ||
               material == Material.CARROTS ||
               material == Material.POTATOES ||
               material == Material.BEETROOTS ||
               material == Material.PUMPKIN ||
               material == Material.MELON;
    }
} 