package com.github.derrop.proxy.api.util.player;

import java.util.UUID;

public class PlayerId {

    private final UUID uniqueId;
    private final String name;
    private final long timestamp;

    public PlayerId(UUID uniqueId, String name) {
        this(uniqueId, name, System.currentTimeMillis());
    }

    public PlayerId(UUID uniqueId, String name, long timestamp) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.timestamp = timestamp;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public String getName() {
        return this.name;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String toString() {
        return this.uniqueId + "#" + this.name;
    }
}
