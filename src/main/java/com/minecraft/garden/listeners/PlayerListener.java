package com.minecraft.garden.listeners;

import com.minecraft.garden.GardenPlugin;
import com.minecraft.garden.managers.PlotManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Iterator;
import java.util.UUID;

public class PlayerListener implements Listener {
    
    private final GardenPlugin plugin;
    
    public PlayerListener(GardenPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block clickedBlock = event.getClickedBlock();
        
        if (action == Action.RIGHT_CLICK_BLOCK && clickedBlock != null) {
            // Проверяем, что игрок кликает по вспаханной земле
            if (clickedBlock.getType() == Material.FARMLAND) {
                Location plantLocation = clickedBlock.getLocation().add(0, 1, 0);
                
                // Проверяем, что место для посадки свободно
                if (plantLocation.getBlock().getType().isAir()) {
                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    
                    if (itemInHand != null && itemInHand.getType() != Material.AIR) {
                        // Проверяем, является ли это семенем
                        Material seedType = itemInHand.getType();
                        
                        // Проверяем, что это известное семя
                        if (plugin.getPlantManager().isValidSeed(seedType) || 
                            plugin.getCustomPlantManager().isCustomPlantSeed(itemInHand)) {
                            
                            // Пытаемся посадить семя
                            boolean success = plugin.getPlantManager().plantSeed(player, seedType, plantLocation);
                            
                            if (success) {
                                // Отменяем стандартное взаимодействие
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();
        
        // Проверяем, находится ли блок на участке
        boolean isOnPlot = false;
        UUID plotOwner = null;
        
        for (var entry : plugin.getPlotManager().getAllPlots().entrySet()) {
            if (entry.getValue().isInPlot(location)) {
                isOnPlot = true;
                plotOwner = entry.getKey();
                break;
            }
        }
        
        if (isOnPlot) {
            // Проверяем права на участке
            if (!plotOwner.equals(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage("§cВы не можете ломать блоки на чужом участке!");
                return;
            }
            
            // Проверяем, не является ли это посаженным растением
            if (plugin.getPlantManager().hasPlantedCrop(location)) {
                // Позволяем собирать урожай
                boolean harvested = plugin.getPlantManager().harvestCrop(player, location);
                if (harvested) {
                    event.setCancelled(true); // Отменяем стандартный сбор
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();
        
        // Проверяем, находится ли блок на участке
        boolean isOnPlot = false;
        UUID plotOwner = null;
        
        for (var entry : plugin.getPlotManager().getAllPlots().entrySet()) {
            if (entry.getValue().isInPlot(location)) {
                isOnPlot = true;
                plotOwner = entry.getKey();
                break;
            }
        }
        
        if (isOnPlot) {
            // Проверяем права на участке
            if (!plotOwner.equals(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage("§cВы не можете ставить блоки на чужом участке!");
            }
        }
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        // Защищаем участки от взрывов
        Iterator<Block> iterator = event.blockList().iterator();
        
        while (iterator.hasNext()) {
            Block block = iterator.next();
            Location location = block.getLocation();
            
            // Проверяем, находится ли блок на участке
            boolean isOnPlot = false;
            
            for (var entry : plugin.getPlotManager().getAllPlots().entrySet()) {
                if (entry.getValue().isInPlot(location)) {
                    isOnPlot = true;
                    break;
                }
            }
            
            // Если блок на участке, убираем его из списка взрыва
            if (isOnPlot) {
                iterator.remove();
                plugin.getLogger().info("Защищен блок от взрыва на участке: " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Если у игрока нет участка, предлагаем создать
        if (!plugin.getPlotManager().hasPlot(player.getUniqueId())) {
            player.sendMessage("§aДобро пожаловать в игру 'Вырасти сад'!");
            player.sendMessage("§eИспользуйте §6/garden create §eдля получения участка и начала игры.");
        } else {
            // Показываем информацию о балансе
            int balance = plugin.getEconomyManager().getBalance(player);
            player.sendMessage("§aДобро пожаловать обратно! Ваш баланс: §e" + balance + " рублей");
        }
    }
} 