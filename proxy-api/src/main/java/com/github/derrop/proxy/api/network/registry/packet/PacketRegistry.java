package com.github.derrop.proxy.api.network.registry.packet;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.Packet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PacketRegistry {

    void registerPacket(@NotNull ProtocolDirection direction, @NotNull ProtocolState state, @NotNull Packet packet);

    @Nullable
    Packet getPacket(@NotNull ProtocolDirection direction, @NotNull ProtocolState state, int packetId);
}
