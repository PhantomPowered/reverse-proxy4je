package com.github.derrop.proxy.network.registry.packet;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.registry.packet.PacketRegistryEntry;
import org.jetbrains.annotations.NotNull;

public final class DefaultPacketRegistryEntry implements PacketRegistryEntry {

    DefaultPacketRegistryEntry(ProtocolDirection protocolDirection, ProtocolState protocolState, Packet packet) {
        this.protocolDirection = protocolDirection;
        this.protocolState = protocolState;
        this.packet = packet;
    }

    private final ProtocolDirection protocolDirection;

    private final ProtocolState protocolState;

    private final Packet packet;

    @Override
    public @NotNull ProtocolDirection getDirection() {
        return this.protocolDirection;
    }

    @Override
    public @NotNull ProtocolState getState() {
        return this.protocolState;
    }

    @Override
    public @NotNull Packet getPacket() {
        return this.packet;
    }
}
