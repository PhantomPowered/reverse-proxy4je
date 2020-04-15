package com.github.derrop.proxy.network.wrapper;

import com.github.derrop.proxy.api.network.Packet;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DecodedPacket {

    public DecodedPacket(@NotNull ByteBuf byteBuf, @Nullable Packet packet) {
        this.byteBuf = byteBuf;
        this.packet = packet;
    }

    private ByteBuf byteBuf;

    private final Packet packet;

    @NotNull
    public ByteBuf getByteBuf() {
        return this.byteBuf.copy();
    }

    public void setByteBuf(@NotNull ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Nullable
    public Packet getPacket() {
        return packet;
    }
}
