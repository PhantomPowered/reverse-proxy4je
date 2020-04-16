package com.github.derrop.proxy.protocol.play.server.world;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerMapChunk implements Packet {

    private int x;
    private int z;
    private boolean fullChunk;
    private Extracted extracted;

    public PacketPlayServerMapChunk(int x, int z, boolean fullChunk, Extracted extracted) {
        this.x = x;
        this.z = z;
        this.fullChunk = fullChunk;
        this.extracted = extracted;
    }

    public PacketPlayServerMapChunk() {
    }

    public static int getArraySize(int dataLengthBits, boolean hasSky, boolean fullChunk) {
        int blockStateBytes = dataLengthBits * 2 * 4096;
        int blockLightBytes = dataLengthBits * 4096 / 2;
        int skyLightBytes = hasSky ? blockLightBytes : 0;
        int biomeBytes = fullChunk ? 256 : 0;
        return blockStateBytes + blockLightBytes + skyLightBytes + biomeBytes;
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.MAP_CHUNK;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public boolean isFullChunk() {
        return this.fullChunk;
    }

    public Extracted getExtracted() {
        return this.extracted;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.x = protoBuf.readInt();
        this.z = protoBuf.readInt();
        this.fullChunk = protoBuf.readBoolean();
        this.extracted = new Extracted();
        this.extracted.dataLength = protoBuf.readShort();
        this.extracted.data = protoBuf.readArray();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeInt(this.x);
        protoBuf.writeInt(this.z);
        protoBuf.writeBoolean(this.fullChunk);
        protoBuf.writeShort(this.extracted.dataLength);
        protoBuf.writeArray(this.extracted.data);
    }

    public String toString() {
        return "PacketPlayServerMapChunk(x=" + this.getX() + ", z=" + this.getZ() + ", fullChunk=" + this.isFullChunk() + ", extracted=" + this.getExtracted() + ")";
    }

    public static class Extracted {
        public byte[] data;
        public int dataLength;
    }

}
