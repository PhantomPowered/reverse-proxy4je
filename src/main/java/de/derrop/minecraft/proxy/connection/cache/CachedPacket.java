package de.derrop.minecraft.proxy.connection.cache;

import io.netty.buffer.ByteBuf;

public class CachedPacket {

    private int packetId;
    private ByteBuf packetData;

    public CachedPacket(int packetId, ByteBuf packetData) {
        this.packetId = packetId;
        this.packetData = packetData;
    }

    public int getPacketId() {
        return packetId;
    }

    public ByteBuf getPacketData() {
        return packetData;
    }
}
