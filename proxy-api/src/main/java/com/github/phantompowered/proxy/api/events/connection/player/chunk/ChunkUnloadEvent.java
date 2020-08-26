package com.github.phantompowered.proxy.api.events.connection.player.chunk;

import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import org.jetbrains.annotations.NotNull;

public class ChunkUnloadEvent extends ChunkEvent {
    public ChunkUnloadEvent(@NotNull ServiceConnection serviceConnection, int x, int z) {
        super(serviceConnection, x, z);
    }
}
