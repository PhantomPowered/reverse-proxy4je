package com.github.derrop.proxy.api.network.registry.packet;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.registry.packet.exception.PacketAlreadyRegisteredException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface PacketRegistry {

    void registerPacket(@NotNull ProtocolDirection direction, @NotNull ProtocolState state, @NotNull Packet packet) throws PacketAlreadyRegisteredException;

    @Nullable Packet getPacket(@NotNull ProtocolDirection direction, @NotNull ProtocolState state, int packetId);

    @NotNull Collection<PacketRegistryEntry> getEntries();
}
