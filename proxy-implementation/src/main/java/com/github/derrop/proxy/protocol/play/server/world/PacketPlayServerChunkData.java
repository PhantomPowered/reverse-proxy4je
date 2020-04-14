package com.github.derrop.proxy.protocol.play.server.world;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketPlayServerChunkData extends DefinedPacket {

    private int x;
    private int z;
    private boolean b;
    private Extracted extracted;

    public static int getArraySize(int dataLengthBits, boolean hasSky, boolean fullChunk) {
        int blockStateBytes = dataLengthBits * 2 * 4096;
        int blockLightBytes = dataLengthBits * 4096 / 2;
        int skyLightBytes = hasSky ? blockLightBytes : 0;
        int biomeBytes = fullChunk ? 256 : 0;
        return blockStateBytes + blockLightBytes + skyLightBytes + biomeBytes;
    }

    @Override
    public void read(ByteBuf buf) {
        this.x = buf.readInt();
        this.z = buf.readInt();
        this.b = buf.readBoolean();
        this.extracted = new Extracted();
        this.extracted.dataLength = buf.readShort();
        this.extracted.data = readArray(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.z);
        buf.writeBoolean(this.b);
        buf.writeShort(this.extracted.dataLength);
        writeArrayNoLimit(this.extracted.data, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }

    public static class Extracted {
        public byte[] data;
        public int dataLength;
    }

}
