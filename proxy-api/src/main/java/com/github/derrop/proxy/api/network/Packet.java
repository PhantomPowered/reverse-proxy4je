package com.github.derrop.proxy.api.network;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public interface Packet {

    int getId();

    void read(@NotNull ByteBuf byteBuf);

    void write(@NotNull ByteBuf byteBuf);

    @Override
    boolean equals(@NotNull Object obj);

    @Override
    int hashCode();

    @Override
    String toString();
}
