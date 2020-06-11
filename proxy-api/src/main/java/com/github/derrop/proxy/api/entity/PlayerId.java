package com.github.derrop.proxy.api.entity;

import java.util.UUID;

public class PlayerId {

    private UUID uniqueId;
    private String name;

    public PlayerId(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public String getName() {
        return this.name;
    }
}
