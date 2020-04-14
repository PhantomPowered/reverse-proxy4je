package com.github.derrop.proxy.protocol.client;

import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.util.PlayerPositionPacketUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
public class PacketC06PlayerPosLook extends DefinedPacket {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean onGround;

    public PacketC06PlayerPosLook(@NotNull Player player) {
        this.x = player.getLocation().getX();
        this.y = player.getLocation().getY();
        this.z = player.getLocation().getZ();
        this.yaw = PlayerPositionPacketUtil.getFixLocation(player.getLocation().getYaw());
        this.pitch = PlayerPositionPacketUtil.getFixLocation(player.getLocation().getPitch());
        this.onGround = player.isOnGround();
    }

    @Override
    public void read(ByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
        this.onGround = buf.readUnsignedByte() != 0;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
        buf.writeByte(this.onGround ? 1 : 0);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {

    }
}
