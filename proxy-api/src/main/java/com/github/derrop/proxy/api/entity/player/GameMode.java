package com.github.derrop.proxy.api.entity.player;

public enum GameMode {

    NOT_SET(-1, ""),
    SURVIVAL(0, "survival"),
    CREATIVE(1, "creative"),
    ADVENTURE(2, "adventure"),
    SPECTATOR(3, "spectator"),
    ;

    private int id;
    private String name;

    GameMode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean isAdventure() {
        return this == ADVENTURE || this == SPECTATOR;
    }

    public boolean isCreative() {
        return this == CREATIVE;
    }

    public boolean isSurvivalOrAdventure() {
        return this == SURVIVAL || this == ADVENTURE;
    }

    public static GameMode getById(int id) {
        for (GameMode value : values()) {
            if (value.id == id) {
                return value;
            }
        }
        return SURVIVAL;
    }

}
