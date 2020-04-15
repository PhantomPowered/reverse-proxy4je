package com.github.derrop.proxy.network.wrapper;

import com.github.derrop.proxy.api.network.Packet;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public final class DecodedPacket {

    public DecodedPacket(@NotNull ByteBuf byteBuf, @NotNull Packet packet) {
        this.byteBuf = byteBuf;
        this.packet = packet;
    }

    private ByteBuf byteBuf;

    private final Packet packet;

    @NotNull
    public ByteBuf getByteBuf() {
        return this.byteBuf.copy();
    }

    @NotNull
    public ByteBuf getRealByteBuf() {
        return this.byteBuf;
    }

    public void setByteBuf(@NotNull ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @NotNull
    public Packet getPacket() {
        return packet;
    }
}
