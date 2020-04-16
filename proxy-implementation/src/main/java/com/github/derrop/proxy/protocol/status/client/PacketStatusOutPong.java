package com.github.derrop.proxy.protocol.status.client;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketStatusOutPong implements Packet {

    private long clientTime;

    public PacketStatusOutPong(long clientTime) {
        this.clientTime = clientTime;
    }

    public PacketStatusOutPong() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Status.PONG;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.clientTime = protoBuf.readLong();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeLong(this.clientTime);
    }

    public long getClientTime() {
        return this.clientTime;
    }

    public String toString() {
        return "PacketStatusOutPong(clientTime=" + this.getClientTime() + ")";
    }
}
