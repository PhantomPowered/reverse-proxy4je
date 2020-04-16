package com.github.derrop.proxy.protocol.status.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketStatusInRequest implements Packet {

    public PacketStatusInRequest() {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Status.START;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
    }

    public String toString() {
        return "PacketStatusInRequest()";
    }
}
