package com.github.derrop.proxy.protocol.play.shared;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.network.NetworkUtils;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayKeepAlive implements Packet {

    private long randomId;

    public PacketPlayKeepAlive(long randomId) {
        this.randomId = randomId;
    }

    public PacketPlayKeepAlive() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.KEEP_ALIVE;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.randomId = protoBuf.readVarInt();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(NetworkUtils.longToInt(this.randomId));
    }

    public long getRandomId() {
        return this.randomId;
    }

    public String toString() {
        return "PacketPlayKeepAlive(randomId=" + this.getRandomId() + ")";
    }
}
