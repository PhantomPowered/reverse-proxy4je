package com.github.derrop.proxy.protocol.play.server;

import com.github.derrop.proxy.api.location.Location;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Getter
public class PacketPlayServerEntityTeleport extends DefinedPacket {

    private int entityId;
    private double x;
    private double y;
    private double z;
    private byte yaw;
    private byte pitch;
    private boolean ground;

    public PacketPlayServerEntityTeleport(int entityId, @NotNull Location location, boolean ground) {
        this.entityId = entityId;
        this.ground = ground;

        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = (byte) ((int) (location.getYaw() * 256.0F / 360.0F));
        this.pitch = (byte) ((int) (location.getPitch() * 256.0F / 360.0F));
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        buf.writeBoolean(this.ground);
    }

    @Override
    public void read(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readByte();
        this.pitch = buf.readByte();
        this.ground = buf.readBoolean();
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {

    }
}
