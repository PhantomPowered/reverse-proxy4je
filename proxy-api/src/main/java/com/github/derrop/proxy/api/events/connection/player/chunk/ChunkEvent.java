package com.github.derrop.proxy.api.events.connection.player.chunk;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class ChunkEvent extends Event {

    private final ServiceConnection serviceConnection;
    private final int x, z;

    public ChunkEvent(@NotNull ServiceConnection serviceConnection, int x, int z) {
        this.serviceConnection = serviceConnection;
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @NotNull
    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }
}
