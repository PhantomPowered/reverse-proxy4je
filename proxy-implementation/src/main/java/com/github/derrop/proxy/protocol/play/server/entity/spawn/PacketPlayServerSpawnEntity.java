package com.github.derrop.proxy.protocol.play.server.entity.spawn;

import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class PacketPlayServerSpawnEntity extends DefinedPacket implements PositionedPacket {

    private int entityId;
    private int x;
    private int y;
    private int z;
    private int speedX;
    private int speedY;
    private int speedZ;
    private byte pitch;
    private byte yaw;
    private int type;
    private int field_149020_k;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.entityId = readVarInt(buf);
        this.type = buf.readByte();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.pitch = buf.readByte();
        this.yaw = buf.readByte();
        this.field_149020_k = buf.readInt();

        if (this.field_149020_k > 0) {
            this.speedX = buf.readShort();
            this.speedY = buf.readShort();
            this.speedZ = buf.readShort();
        }
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeVarInt(this.entityId, buf);
        buf.writeByte(this.type);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeByte(this.pitch);
        buf.writeByte(this.yaw);
        buf.writeInt(this.field_149020_k);

        if (this.field_149020_k > 0) {
            buf.writeShort(this.speedX);
            buf.writeShort(this.speedY);
            buf.writeShort(this.speedZ);
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }

    @Override
    public int getId() {
        return ProtocolIds.ClientBound.Play.SPAWN_ENTITY;
    }
}
