package com.github.derrop.proxy.api.network;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.util.Identifiable;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public interface Packet extends Identifiable {

    void read(@NotNull ByteBuf byteBuf);

    default void read(@NotNull ByteBuf byteBuf, @NotNull ProtocolDirection direction) {
        this.read(byteBuf);
    }

    default void read(@NotNull ByteBuf byteBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.read(byteBuf, direction);
    }

    void write(@NotNull ByteBuf byteBuf);

    default void write(@NotNull ByteBuf byteBuf, @NotNull ProtocolDirection direction) {
        this.write(byteBuf);
    }

    default void write(@NotNull ByteBuf byteBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.write(byteBuf, direction);
    }

    @Override
    boolean equals(@NotNull Object obj);

    @Override
    int hashCode();

    @Override
    String toString();
}
