package com.github.derrop.proxy.api.events.connection.player.chunk;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import org.jetbrains.annotations.NotNull;

public class ChunkUnloadEvent extends ChunkEvent {
    public ChunkUnloadEvent(@NotNull ServiceConnection serviceConnection, int x, int z) {
        super(serviceConnection, x, z);
    }
}