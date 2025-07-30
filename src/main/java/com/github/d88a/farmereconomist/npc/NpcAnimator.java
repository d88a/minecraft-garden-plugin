package com.github.d88a.farmereconomist.npc;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.UUID;

public class NpcAnimator {
    private final FarmerEconomist plugin;
    private final NpcManager npcManager;

    public NpcAnimator(FarmerEconomist plugin, NpcManager npcManager) {
        this.plugin = plugin;
        this.npcManager = npcManager;
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                UUID npcId = npcManager.getNpcUniqueId();
                if (npcId == null) return;
                Entity entity = Bukkit.getEntity(npcId);
                if (!(entity instanceof Villager)) return;
                Villager npc = (Villager) entity;
                Location npcLoc = npc.getLocation();
                Player nearest = null;
                double minDist = 8.0; // Радиус "внимания"
                for (Player player : npc.getWorld().getPlayers()) {
                    double dist = player.getLocation().distance(npcLoc);
                    if (dist < minDist) {
                        minDist = dist;
                        nearest = player;
                    }
                }
                if (nearest != null) {
                    Location target = nearest.getLocation();
                    float yaw = getYaw(npcLoc, target);
                    float pitch = getPitch(npcLoc, target);
                    npc.teleport(new Location(npc.getWorld(), npcLoc.getX(), npcLoc.getY(), npcLoc.getZ(), yaw, pitch));
                } else {
                    // Если игроков рядом нет — слегка "шевелим" голову
                    float yaw = npcLoc.getYaw() + (float)(Math.random() * 10 - 5);
                    float pitch = npcLoc.getPitch() + (float)(Math.random() * 4 - 2);
                    npc.teleport(new Location(npc.getWorld(), npcLoc.getX(), npcLoc.getY(), npcLoc.getZ(), yaw, pitch));
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Каждую секунду
    }

    private float getYaw(Location from, Location to) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();
        return (float) (Math.toDegrees(Math.atan2(-dx, dz)));
    }

    private float getPitch(Location from, Location to) {
        double dy = to.getY() - from.getY();
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        return (float) (Math.toDegrees(-Math.atan2(dy, dist)));
    }
}