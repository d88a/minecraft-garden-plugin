package com.github.d88a.farmereconomist.events;

public class FarmingEvent {
    private final String id;
    private final String name;
    private final String description;
    private final int duration; // в секундах
    private final EventType type;
    private final double multiplier;

    public FarmingEvent(String id, String name, String description, int duration, EventType type, double multiplier) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.type = type;
        this.multiplier = multiplier;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getDuration() {
        return duration;
    }

    public EventType getType() {
        return type;
    }

    public double getMultiplier() {
        return multiplier;
    }
} 