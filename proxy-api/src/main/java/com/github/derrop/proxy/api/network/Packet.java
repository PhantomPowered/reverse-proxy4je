package com.github.derrop.proxy.api.network;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public interface Packet {

    int getId();

    void read(@NotNull ByteBuf byteBuf);

    default void read(@NotNull ByteBuf byteBuf, @NotNull ProtocolDirection direction) {
        this.read(byteBuf);
    }

    void write(@NotNull ByteBuf byteBuf);

    default void write(@NotNull ByteBuf byteBuf, @NotNull ProtocolDirection direction) {
        this.write(byteBuf);
    }

    @Override
    boolean equals(@NotNull Object obj);

    @Override
    int hashCode();

    @Override
    String toString();
}
