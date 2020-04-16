package com.github.derrop.proxy.connection.reconnect;

import java.util.UUID;

public class ReconnectProfile {

    private UUID uniqueId;
    private UUID targetUniqueId;
    private long timeout;

    public ReconnectProfile(UUID uniqueId, UUID targetUniqueId) {
        this.uniqueId = uniqueId;
        this.targetUniqueId = targetUniqueId;
        this.timeout = System.currentTimeMillis() + 60000;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public UUID getTargetUniqueId() {
        return targetUniqueId;
    }

    public long getTimeout() {
        return timeout;
    }
}
