package com.github.derrop.proxy.plugins.gomme.match.event;

import java.util.UUID;

public class CoreDestroyedEvent extends MatchEvent {

    private final UUID destroyerId;
    private final String core;

    public CoreDestroyedEvent(UUID destroyerId, String core) {
        super(Type.CORE_DESTROYED);
        this.destroyerId = destroyerId;
        this.core = core;
    }

    public UUID getDestroyerId() {
        return this.destroyerId;
    }

    public String getCore() {
        return this.core;
    }
}
