package com.github.derrop.proxy.api.network.registry.packet;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.Packet;
import org.jetbrains.annotations.NotNull;

public interface PacketRegistryEntry {

    @NotNull ProtocolDirection getDirection();

    @NotNull ProtocolState getState();

    @NotNull Packet getPacket();
}
