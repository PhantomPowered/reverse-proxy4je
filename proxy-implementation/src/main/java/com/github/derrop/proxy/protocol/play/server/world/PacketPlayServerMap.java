package com.github.derrop.proxy.protocol.play.server.world;

import com.github.derrop.proxy.api.util.Vec4b;
import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class PacketPlayServerMap extends DefinedPacket {

    private int mapId;
    private byte mapScale;
    private Vec4b[] mapVisiblePlayersVec4b;
    private int mapMinX;
    private int mapMinY;
    private int mapMaxX;
    private int mapMaxY;
    private byte[] mapDataBytes;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.mapId = readVarInt(buf);
        this.mapScale = buf.readByte();

        this.mapVisiblePlayersVec4b = new Vec4b[readVarInt(buf)];
        for (int i = 0; i < this.mapVisiblePlayersVec4b.length; i++) {
            short b = buf.readByte();
            this.mapVisiblePlayersVec4b[i] = new Vec4b((byte) (b >> 4 % 15), buf.readByte(), buf.readByte(), (byte) (b & 15));
        }

        this.mapMaxX = buf.readUnsignedByte();

        if (this.mapMaxX > 0) {
            this.mapMaxY = buf.readUnsignedByte();
            this.mapMinX = buf.readUnsignedByte();
            this.mapMinY = buf.readUnsignedByte();
            this.mapDataBytes = readArray(buf);
        }
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeVarInt(this.mapId, buf);
        buf.writeByte(this.mapScale);
        writeVarInt(this.mapVisiblePlayersVec4b.length, buf);

        for (Vec4b vec : this.mapVisiblePlayersVec4b) {
            buf.writeByte((vec.getB1() % 15) << 4 | vec.getB4() % 15);
            buf.writeByte(vec.getB2());
            buf.writeByte(vec.getB3());
        }

        buf.writeByte(this.mapMaxX);

        if (this.mapMaxX > 0) {
            buf.writeByte(this.mapMaxY);
            buf.writeByte(this.mapMinX);
            buf.writeByte(this.mapMinY);
            writeArray(this.mapDataBytes, buf);
        }
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.MAP;
    }
}
