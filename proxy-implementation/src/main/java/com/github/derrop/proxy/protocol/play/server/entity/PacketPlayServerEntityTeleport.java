package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.util.PlayerPositionPacketUtil;
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
public class PacketPlayServerEntityTeleport extends DefinedPacket {

    private int entityId;
    private int x;
    private int y;
    private int z;
    private byte yaw;
    private byte pitch;
    private boolean onGround;

    public PacketPlayServerEntityTeleport(int entityId, @NotNull Location location, boolean onGround) {
        this.entityId = entityId;
        this.onGround = onGround;

        this.x = PlayerPositionPacketUtil.getFixLocation(location.getX());
        this.y = PlayerPositionPacketUtil.getFixLocation(location.getY());
        this.z = PlayerPositionPacketUtil.getFixLocation(location.getZ());
        this.yaw = PlayerPositionPacketUtil.getFixRotation(location.getYaw());
        this.pitch = PlayerPositionPacketUtil.getFixRotation(location.getPitch());
    }

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.entityId = readVarInt(buf);
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.yaw = buf.readByte();
        this.pitch = buf.readByte();
        this.onGround = buf.readBoolean();
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeVarInt(this.entityId, buf);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        buf.writeBoolean(this.onGround);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_TELEPORT;
    }
}
