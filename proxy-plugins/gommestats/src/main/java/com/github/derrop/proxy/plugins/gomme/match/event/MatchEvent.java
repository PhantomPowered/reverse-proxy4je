package com.github.derrop.proxy.plugins.gomme.match.event;

public class MatchEvent {

    private final Type type;
    private final long timestamp;

    public MatchEvent(Type type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public Type getType() {
        return this.type;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public enum Type {
        CORE_DESTROYED, PLAYER_DIED, SPECTATOR_JOINED, SPECTATOR_LEFT
    }

}
