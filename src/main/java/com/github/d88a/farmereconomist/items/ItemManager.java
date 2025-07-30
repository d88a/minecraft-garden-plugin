package com.github.d88a.farmereconomist.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

public class ItemManager {

    // --- Texture values for custom heads ---
    private static final String GREEN_TOMATO_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA5YjU5YjJlNmYxOTBhYjI0MTBkYjNkY2U5MGMxNGU0ZGNlYjVjMmFiN2NhYTc5N2ZlM2U5Y2E2NDQ3ZGEifX19";
    private static final String RED_TOMATO_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI4YTk5Y2I4ZWU3ZmY4MzBjY2Y4MWMyM2YwY2E3YTM4M2VjYWM3NDUzYjFhMTQ5Y2Y5Y2UyNGY0NDY1YSJ9fX0=";
    private static final String SMALL_MUSHROOM_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1NDgzNjJhMjY3YjYxY2Y5YjY1OTQ0YjY4ZDRmM2Y3OTY0MGI1Y2Q3M2Y1YWE5YTU2NmI4YjliZWMyZGU5In19fQ==";
    private static final String GLOWSHROOM_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTI4OWQzYjE1YjE1NDk5YjcxY2RjYThmZDE0MmM0YmZmMjE4YjQ5OWQ4Y2IyZGI5YTM0Yzc4ZDM5YjE1YjEifX19";


    public static ItemStack createWateringCan() {
        ItemStack item = new ItemStack(Material.IRON_HOE); // Используем мотыгу как основу
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bЛейка");
        meta.setLore(Arrays.asList("Наполните водой, чтобы поливать растения."));
        meta.setCustomModelData(1); // Уникальный ID для ресурс-пака
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createLettuceSeeds() {
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aСемена Скромного Латука");
        meta.setLore(Arrays.asList("Можно посадить на своем участке."));
        meta.setCustomModelData(1);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack createTomatoSeeds() {
        ItemStack item = new ItemStack(Material.BEETROOT_SEEDS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cСемена Рубинового Томата");
        meta.setLore(Arrays.asList("Можно посадить на своем участке."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createGlowshroomSpores() {
        ItemStack item = new ItemStack(Material.RED_MUSHROOM);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§dСпоры светящегося гриба");
        meta.setLore(Arrays.asList("Можно посадить на мицелий или подзол."));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createGlowshroomDust() {
        ItemStack item = new ItemStack(Material.GLOWSTONE_DUST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§dПыльца светящегося гриба");
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createTomato() {
        ItemStack item = new ItemStack(Material.APPLE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cРубиновый Томат");
        meta.setLore(Arrays.asList("Сочный и спелый!"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createGlowshroomStage(int stage) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        String texture = (stage == 0) ? SMALL_MUSHROOM_TEXTURE : GLOWSHROOM_TEXTURE;

        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        profile.setProperty("textures", texture, null);
        meta.setPlayerProfile(profile);

        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack createTomatoStage(int stage) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        String texture = (stage == 0) ? GREEN_TOMATO_TEXTURE : RED_TOMATO_TEXTURE;

        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        profile.setProperty("textures", texture, null);
        meta.setPlayerProfile(profile);

        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack createLettuce(boolean isWatered) {
        ItemStack item = new ItemStack(Material.POISONOUS_POTATO); // Используем "плохой" картофель как основу
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§2Скромный Латук");
        if (isWatered) {
            meta.setLore(Arrays.asList("Качество: §aОтличное"));
            meta.setCustomModelData(101); // Модель для качественного латука
        } else {
            meta.setLore(Arrays.asList("Качество: §eОбычное"));
            meta.setCustomModelData(100); // Модель для обычного латука
        }
        item.setItemMeta(meta);
        return item;
    }
} 