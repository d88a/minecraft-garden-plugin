package com.minecraft.garden.listeners;

import com.minecraft.garden.GardenPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
} 