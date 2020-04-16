package com.github.derrop.proxy.protocol.login.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketLoginOutSetCompression implements Packet {

    private int threshold;

    public PacketLoginOutSetCompression(int threshold) {
        this.threshold = threshold;
    }

    public PacketLoginOutSetCompression() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Login.SET_COMPRESSION;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.threshold = protoBuf.readVarInt();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.threshold);
    }

    public int getThreshold() {
        return this.threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public String toString() {
        return "PacketLoginOutSetCompression(threshold=" + this.getThreshold() + ")";
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof PacketLoginOutSetCompression))
            return false;
        final PacketLoginOutSetCompression other = (PacketLoginOutSetCompression) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getThreshold() != other.getThreshold()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PacketLoginOutSetCompression;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getThreshold();
        return result;
    }
}
