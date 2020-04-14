package com.github.derrop.proxy.protocol.server;

import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.util.PlayerPositionPacketUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
public class PacketPlayOutEntityTeleport extends DefinedPacket {

    private int entityId;
    private int x;
    private int y;
    private int z;
    private byte yaw;
    private byte pitch;
    private boolean ground;

    public PacketPlayOutEntityTeleport(int entityId, @NotNull Location location, boolean ground) {
        this.entityId = entityId;
        this.ground = ground;
        this.x = PlayerPositionPacketUtil.getFixLocation(location.getX());
        this.y = PlayerPositionPacketUtil.getFixLocation(location.getY());
        this.z = PlayerPositionPacketUtil.getFixLocation(location.getZ());
        this.yaw = (byte) ((int) (location.getYaw() * 256.0F / 360.0F));
        this.pitch = (byte) ((int) (location.getPitch() * 256.0F / 360.0F));
    }

    @Override
    public void write(ByteBuf buf) {
        writeVarInt(this.entityId, buf);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        buf.writeBoolean(this.ground);
    }

    @Override
    public void read(ByteBuf buf) {
        this.entityId = readVarInt(buf);
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.yaw = buf.readByte();
        this.pitch = buf.readByte();
        this.ground = buf.readBoolean();
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {

    }
}
