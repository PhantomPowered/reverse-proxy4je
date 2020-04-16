package com.github.derrop.proxy.api.network;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.api.util.Identifiable;
import org.jetbrains.annotations.NotNull;

public interface Packet extends Identifiable {

    void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion);

    void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion);

    @Override
    String toString();
}
