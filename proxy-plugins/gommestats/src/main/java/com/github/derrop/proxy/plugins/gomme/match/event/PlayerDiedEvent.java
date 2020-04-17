package com.github.derrop.proxy.plugins.gomme.match.event;

import java.util.UUID;

public class PlayerDiedEvent extends MatchEvent {

    private final UUID playerId;

    public PlayerDiedEvent(UUID playerId) {
        super(Type.PLAYER_DIED);
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }
}
