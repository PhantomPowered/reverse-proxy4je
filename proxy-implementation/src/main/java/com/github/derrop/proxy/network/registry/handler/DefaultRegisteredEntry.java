package com.github.derrop.proxy.network.registry.handler;

import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.registry.handler.PacketHandlerRegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class DefaultRegisteredEntry implements PacketHandlerRegistryEntry.RegisteredEntry {

    DefaultRegisteredEntry(int[] handledPackets, Method method, ProtocolState protocolState) {
        this.handledPackets = handledPackets;
        this.method = method;
        this.protocolState = protocolState;
    }

    private final int[] handledPackets;

    private final Method method;

    private final ProtocolState protocolState;

    @Override
    public @NotNull int[] getHandledPackets() {
        return this.handledPackets;
    }

    @Override
    public @NotNull Method getMethod() {
        return this.method;
    }

    @Override
    public @NotNull ProtocolState getState() {
        return this.protocolState;
    }
}
