package de.derrop.minecraft.proxy.connection.cache.packet.world;

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
public class ChunkData extends DefinedPacket {

    private int x;
    private int z;
    private boolean b;
    private Extracted extracted;

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
