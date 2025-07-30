package com.github.d88a.farmereconomist.events;

import com.github.d88a.farmereconomist.FarmerEconomist;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class FarmingEventManager {

    private final FarmerEconomist plugin;
    private final Map<String, FarmingEvent> events = new HashMap<>();
    private FarmingEvent currentEvent = null;
    private long eventStartTime = 0;
    private final long EVENT_DURATION = 20L * 60 * 30; // 30 минут

    public FarmingEventManager(FarmerEconomist plugin) {
        this.plugin = plugin;
        setupEvents();
        startEventScheduler();
    }

    private void setupEvents() {
        // Событие "Золотая лихорадка"
        events.put("golden_rush", new FarmingEvent(
            "golden_rush",
            "Золотая лихорадка",
            "§6💰 Все растения дают в 3 раза больше монет!",
            30 * 60, // 30 минут
            EventType.MONEY_BOOST,
            3.0
        ));

        // Событие "Скоростной рост"
        events.put("speed_growth", new FarmingEvent(
            "speed_growth",
            "Скоростной рост",
            "§a⚡ Все растения растут в 2 раза быстрее!",
            20 * 60, // 20 минут
            EventType.GROWTH_BOOST,
            2.0
        ));

        // Событие "Двойной урожай"
        events.put("double_harvest", new FarmingEvent(
            "double_harvest",
            "Двойной урожай",
            "§e🌾 Все растения дают двойной урожай!",
            25 * 60, // 25 минут
            EventType.HARVEST_BOOST,
            2.0
        ));

        // Событие "Счастливый час"
        events.put("lucky_hour", new FarmingEvent(
            "lucky_hour",
            "Счастливый час",
            "§d🍀 50% шанс получить редкие семена при сборе!",
            15 * 60, // 15 минут
            EventType.LUCKY_DROP,
            0.5
        ));

        // Событие "Фестиваль цветов"
        events.put("flower_festival", new FarmingEvent(
            "flower_festival",
            "Фестиваль цветов",
            "§d🌸 Цветочные культуры дают в 4 раза больше урожая!",
            35 * 60, // 35 минут
            EventType.FLOWER_BOOST,
            4.0
        ));

        // Событие "Ночное цветение"
        events.put("night_bloom", new FarmingEvent(
            "night_bloom",
            "Ночное цветение",
            "§5🌙 Светящиеся культуры растут в 3 раза быстрее!",
            40 * 60, // 40 минут
            EventType.NIGHT_BOOST,
            3.0
        ));

        // Событие "Дождь из монет"
        events.put("coin_rain", new FarmingEvent(
            "coin_rain",
            "Дождь из монет",
            "§6☔ Каждое растение дает дополнительные монеты!",
            10 * 60, // 10 минут
            EventType.COIN_RAIN,
            1.0
        ));

        // Событие "Мастер-класс"
        events.put("master_class", new FarmingEvent(
            "master_class",
            "Мастер-класс",
            "§b📚 Получайте в 2 раза больше опыта за все действия!",
            45 * 60, // 45 минут
            EventType.EXPERIENCE_BOOST,
            2.0
        ));
    }

    private void startEventScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Проверяем, нужно ли запустить новое событие
                if (currentEvent == null) {
                    startRandomEvent();
                } else {
                    // Проверяем, не закончилось ли текущее событие
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - eventStartTime >= currentEvent.getDuration() * 1000) {
                        endCurrentEvent();
                    }
                }
            }
        }.runTaskTimer(plugin, 20L * 60 * 5, 20L * 60 * 5); // Каждые 5 минут
    }

    private void startRandomEvent() {
        if (events.isEmpty()) return;

        // Выбираем случайное событие
        List<String> eventIds = new ArrayList<>(events.keySet());
        String randomEventId = eventIds.get(new Random().nextInt(eventIds.size()));
        FarmingEvent event = events.get(randomEventId);

        currentEvent = event;
        eventStartTime = System.currentTimeMillis();

        // Объявляем событие
        announceEventStart(event);
    }

    private void endCurrentEvent() {
        if (currentEvent != null) {
            announceEventEnd(currentEvent);
            currentEvent = null;
            eventStartTime = 0;
        }
    }

    private void announceEventStart(FarmingEvent event) {
        String message = "§a§l🎉 СОБЫТИЕ НАЧАЛОСЬ! " + event.getName();
        Bukkit.broadcastMessage(message);
        Bukkit.broadcastMessage(event.getDescription());
        Bukkit.broadcastMessage("§7Длительность: " + formatDuration(event.getDuration()));

        // Звуковой эффект для всех игроков
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
        }

        // Частицы для всех игроков
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getWorld().spawnParticle(org.bukkit.Particle.FIREWORK, player.getLocation(), 50);
        }
    }

    private void announceEventEnd(FarmingEvent event) {
        String message = "§c§l⏰ СОБЫТИЕ ЗАКОНЧИЛОСЬ! " + event.getName();
        Bukkit.broadcastMessage(message);
        Bukkit.broadcastMessage("§7Спасибо за участие!");

        // Звуковой эффект
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.5f);
        }
    }

    private String formatDuration(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%d:%02d", minutes, remainingSeconds);
    }

    public FarmingEvent getCurrentEvent() {
        return currentEvent;
    }

    public boolean isEventActive() {
        return currentEvent != null;
    }

    public boolean isEventActive(String eventId) {
        return currentEvent != null && currentEvent.getId().equals(eventId);
    }

    public double getEventMultiplier(EventType type) {
        if (currentEvent != null && currentEvent.getType() == type) {
            return currentEvent.getMultiplier();
        }
        return 1.0;
    }

    public void startEvent(String eventId) {
        FarmingEvent event = events.get(eventId);
        if (event != null) {
            if (currentEvent != null) {
                endCurrentEvent();
            }
            currentEvent = event;
            eventStartTime = System.currentTimeMillis();
            announceEventStart(event);
        }
    }

    public void stopCurrentEvent() {
        if (currentEvent != null) {
            endCurrentEvent();
        }
    }

    public long getEventTimeRemaining() {
        if (currentEvent == null) return 0;
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - eventStartTime;
        long remaining = currentEvent.getDuration() * 1000 - elapsed;
        return Math.max(0, remaining);
    }

    public String getEventStatus() {
        if (currentEvent == null) {
            return "§7Событий нет";
        }
        
        long remaining = getEventTimeRemaining();
        int minutes = (int) (remaining / 60000);
        int seconds = (int) ((remaining % 60000) / 1000);
        
        return String.format("§a%s §7(%d:%02d осталось)", 
                           currentEvent.getName(), minutes, seconds);
    }

    public Map<String, FarmingEvent> getAllEvents() {
        return new HashMap<>(events);
    }
} 