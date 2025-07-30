package com.github.d88a.farmereconomist.npc;

import java.util.UUID;

public class PlayerQuestData {
    private final UUID playerId;
    private Quest currentQuest;
    private int progress;
    private boolean completed;

    public PlayerQuestData(UUID playerId) {
        this.playerId = playerId;
        this.currentQuest = null;
        this.progress = 0;
        this.completed = false;
    }

    public UUID getPlayerId() { return playerId; }
    public Quest getCurrentQuest() { return currentQuest; }
    public void setCurrentQuest(Quest quest) {
        this.currentQuest = quest;
        this.progress = 0;
        this.completed = false;
    }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public void incrementProgress() { this.progress++; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
} 