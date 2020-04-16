package com.github.derrop.proxy.network.wrapper;

import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.api.util.Identifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DecodedPacket implements Identifiable {

    public DecodedPacket(@NotNull ProtoBuf protoBuf, @Nullable Packet packet) {
        this.protoBuf = protoBuf;
        this.packet = packet;
    }

    private final ProtoBuf protoBuf;

    private final Packet packet;

    @NotNull
    public ProtoBuf getProtoBuf() {
        return this.protoBuf;
    }

    @Nullable
    public Packet getPacket() {
        return packet;
    }

    @Override
    public int getId() {
        return this.packet == null ? -1 : this.packet.getId();
    }
}
