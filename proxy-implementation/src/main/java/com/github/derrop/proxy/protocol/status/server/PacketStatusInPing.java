package com.github.derrop.proxy.protocol.status.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketStatusInPing implements Packet {

    private long time;

    public PacketStatusInPing(long time) {
        this.time = time;
    }

    public PacketStatusInPing() {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Status.PING;
    }

    public long getTime() {
        return this.time;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.time = protoBuf.readLong();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeLong(this.time);
    }

    public String toString() {
        return "PacketStatusInPing(time=" + this.getTime() + ")";
    }
}
