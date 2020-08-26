package com.github.phantompowered.proxy.api.events.connection.player.chunk;

import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class ChunkEvent extends Event {

    private final ServiceConnection serviceConnection;
    private final int x;
    private final int z;

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
