package com.github.derrop.proxy.network.wrapper;

import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.util.Identifiable;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DecodedPacket implements Identifiable {

    public DecodedPacket(@NotNull ByteBuf byteBuf, @Nullable Packet packet) {
        this.byteBuf = byteBuf;
        this.packet = packet;
    }

    private ByteBuf byteBuf;

    private final Packet packet;

    @NotNull
    public ByteBuf getByteBuf() {
        return this.byteBuf;
    }

    public void setByteBuf(@NotNull ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
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
