package de.derrop.minecraft.proxy.connection.cache;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.DefinedPacket;

public class CachedPacket {

    private int packetId;
    private DefinedPacket deserializedPacket;
    private ByteBuf packetData;

    public CachedPacket(int packetId, DefinedPacket deserializedPacket, ByteBuf packetData) {
        this.packetId = packetId;
        this.deserializedPacket = deserializedPacket;
        this.packetData = packetData;
    }

    public int getPacketId() {
        return packetId;
    }

    public DefinedPacket getDeserializedPacket() {
        return deserializedPacket;
    }

    public ByteBuf getPacketData() {
        return packetData;
    }
}
