package com.github.derrop.proxy.network.registry.packet;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.registry.packet.PacketRegistry;
import com.github.derrop.proxy.api.network.registry.packet.PacketRegistryEntry;
import com.github.derrop.proxy.api.network.registry.packet.exception.PacketAlreadyRegisteredException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultPacketRegistry implements PacketRegistry {

    private final Collection<PacketRegistryEntry> entries = new CopyOnWriteArrayList<>();

    @Override
    public void registerPacket(@NotNull ProtocolDirection direction, @NotNull ProtocolState state, @NotNull Packet packet) throws PacketAlreadyRegisteredException {
        if (this.getPacket(direction, state, packet.getId()) != null) {
            throw new PacketAlreadyRegisteredException(packet);
        }

        this.entries.add(new DefaultPacketRegistryEntry(direction, state, packet));
    }

    @Override
    public @Nullable Packet getPacket(@NotNull ProtocolDirection direction, @NotNull ProtocolState state, int packetId) {
        for (PacketRegistryEntry entry : this.entries) {
            if (entry.getDirection() == direction && entry.getState() == state && entry.getPacket().getId() == packetId) {
                return entry.getPacket();
            }
        }

        return null;
    }

    @Override
    public @NotNull Collection<PacketRegistryEntry> getEntries() {
        return Collections.unmodifiableCollection(this.entries);
    }
}
