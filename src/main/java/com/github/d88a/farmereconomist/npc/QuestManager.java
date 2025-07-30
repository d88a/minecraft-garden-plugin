package com.github.d88a.farmereconomist.npc;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.*;

public class QuestManager {
    private final Map<String, Quest> quests = new HashMap<>();
    private final Map<UUID, PlayerQuestData> playerQuests = new HashMap<>();

    public QuestManager() {
        // Пример квестов
        addQuest(new Quest("plant_strawberry", "Посади 5 клубник", Quest.QuestType.PLANT, Material.SWEET_BERRIES, 5, 50, null));
        addQuest(new Quest("harvest_tomato", "Собери 10 томатов", Quest.QuestType.HARVEST, Material.APPLE, 10, 100, null));
        addQuest(new Quest("deliver_pumpkin", "Доставь 3 электро-тыквы Мирону", Quest.QuestType.DELIVER, Material.CARVED_PUMPKIN, 3, 150, null));
    }

    public void addQuest(Quest quest) {
        quests.put(quest.getId(), quest);
    }

    public Collection<Quest> getAllQuests() {
        return quests.values();
    }

    public Quest getQuestById(String id) {
        return quests.get(id);
    }

    public PlayerQuestData getPlayerQuestData(Player player) {
        return playerQuests.computeIfAbsent(player.getUniqueId(), PlayerQuestData::new);
    }

    public void assignQuest(Player player, Quest quest) {
        getPlayerQuestData(player).setCurrentQuest(quest);
    }

    public void incrementProgress(Player player) {
        PlayerQuestData data = getPlayerQuestData(player);
        if (data.getCurrentQuest() != null && !data.isCompleted()) {
            data.incrementProgress();
            if (data.getProgress() >= data.getCurrentQuest().getAmount()) {
                data.setCompleted(true);
            }
        }
    }

    public void completeQuest(Player player) {
        PlayerQuestData data = getPlayerQuestData(player);
        if (data.getCurrentQuest() != null && data.isCompleted()) {
            // Награда выдается снаружи (через NPC)
            data.setCurrentQuest(null);
        }
    }
} 