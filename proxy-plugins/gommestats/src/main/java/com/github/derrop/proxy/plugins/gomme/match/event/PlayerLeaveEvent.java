package com.github.derrop.proxy.plugins.gomme.match.event;

import java.util.UUID;

public class PlayerLeaveEvent extends MatchEvent {

    private final UUID playerId;

    public PlayerLeaveEvent(Type type, UUID playerId) {
        super(type);
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }
}
