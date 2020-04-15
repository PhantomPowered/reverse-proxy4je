package com.github.derrop.proxy.connection.cache;

import com.github.derrop.proxy.api.network.Packet;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.DefinedPacket;

public class CachedPacket {

    private int packetId;
    private Packet deserializedPacket;
    private ByteBuf packetData;

    public CachedPacket(int packetId, Packet deserializedPacket, ByteBuf packetData) {
        this.packetId = packetId;
        this.deserializedPacket = deserializedPacket;
        this.packetData = packetData;
    }

    public int getPacketId() {
        return packetId;
    }

    public Packet getDeserializedPacket() {
        return deserializedPacket;
    }

    public ByteBuf getPacketData() {
        return packetData;
    }
}
