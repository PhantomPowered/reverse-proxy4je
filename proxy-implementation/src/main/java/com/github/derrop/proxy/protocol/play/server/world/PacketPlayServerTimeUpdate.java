package com.github.derrop.proxy.protocol.play.server.world;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerTimeUpdate implements Packet {

    private long totalWorldTime;
    private long worldTime;

    public PacketPlayServerTimeUpdate(long totalWorldTime, long worldTime) {
        this.totalWorldTime = totalWorldTime;
        this.worldTime = worldTime;
    }

    public PacketPlayServerTimeUpdate() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.UPDATE_TIME;
    }

    public long getTotalWorldTime() {
        return this.totalWorldTime;
    }

    public long getWorldTime() {
        return this.worldTime;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.totalWorldTime = protoBuf.readLong();
        this.worldTime = protoBuf.readLong();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeLong(this.totalWorldTime);
        protoBuf.writeLong(this.worldTime);
    }

    public String toString() {
        return "PacketPlayServerTimeUpdate(totalWorldTime=" + this.getTotalWorldTime() + ", worldTime=" + this.getWorldTime() + ")";
    }
}
