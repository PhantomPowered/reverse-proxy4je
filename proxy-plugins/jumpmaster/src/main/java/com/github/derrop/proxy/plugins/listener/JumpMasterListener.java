package com.github.derrop.proxy.plugins.listener;

import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.player.chunk.ChunkLoadEvent;
import com.github.derrop.proxy.api.service.ServiceRegistry;

public final class JumpMasterListener {

    private final ServiceRegistry serviceRegistry;

    public JumpMasterListener(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Listener
    public void handleChunkEvent(ChunkLoadEvent chunkLoadEvent) {

    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }
}
