package com.github.derrop.proxy.api.connection.packet;

import org.jetbrains.annotations.NotNull;

public interface Packet {

    @Override
    boolean equals(@NotNull Object obj);

    @Override
    int hashCode();

    @Override
    String toString();
}
