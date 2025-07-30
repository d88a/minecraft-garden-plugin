package com.github.d88a.farmereconomist.npc;

import org.bukkit.Material;

public class Quest {
    public enum QuestType {
        PLANT, // посадить
        HARVEST, // собрать
        DELIVER // доставить
    }

    private final String id;
    private final String description;
    private final QuestType type;
    private final Material targetMaterial;
    private final int amount;
    private final int rewardMoney;
    private final String rewardItemName;

    public Quest(String id, String description, QuestType type, Material targetMaterial, int amount, int rewardMoney, String rewardItemName) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.targetMaterial = targetMaterial;
        this.amount = amount;
        this.rewardMoney = rewardMoney;
        this.rewardItemName = rewardItemName;
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public QuestType getType() { return type; }
    public Material getTargetMaterial() { return targetMaterial; }
    public int getAmount() { return amount; }
    public int getRewardMoney() { return rewardMoney; }
    public String getRewardItemName() { return rewardItemName; }
} 