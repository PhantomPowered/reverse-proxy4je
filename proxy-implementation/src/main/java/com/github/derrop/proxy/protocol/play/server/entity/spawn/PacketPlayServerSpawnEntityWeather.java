package com.github.derrop.proxy.protocol.play.server.entity.spawn;

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
public class PacketPlayServerSpawnEntityWeather extends DefinedPacket implements PositionedPacket {

    private int entityId;
    private int x;
    private int y;
    private int z;
    private byte type;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.entityId = readVarInt(buf);
        this.type = buf.readByte();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeVarInt(this.entityId, buf);
        buf.writeByte(this.type);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }

    @Override
    public void setYaw(byte yaw) {
    }

    @Override
    public void setPitch(byte pitch) {
    }

    @Override
    public byte getYaw() {
        return 0;
    }

    @Override
    public byte getPitch() {
        return 0;
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SPAWN_ENTITY_WEATHER;
    }
}
