package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.util.PlayerPositionPacketUtil;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntityTeleport implements Packet {

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

    public PacketPlayServerEntityTeleport(int entityId, int x, int y, int z, byte yaw, byte pitch, boolean onGround) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public PacketPlayServerEntityTeleport() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_TELEPORT;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public byte getYaw() {
        return this.yaw;
    }

    public byte getPitch() {
        return this.pitch;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.x = protoBuf.readInt();
        this.y = protoBuf.readInt();
        this.z = protoBuf.readInt();
        this.yaw = protoBuf.readByte();
        this.pitch = protoBuf.readByte();
        this.onGround = protoBuf.readBoolean();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeInt(this.x);
        protoBuf.writeInt(this.y);
        protoBuf.writeInt(this.z);
        protoBuf.writeByte(this.yaw);
        protoBuf.writeByte(this.pitch);
        protoBuf.writeBoolean(this.onGround);
    }

    public String toString() {
        return "PacketPlayServerEntityTeleport(entityId=" + this.getEntityId() + ", x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ", onGround=" + this.isOnGround() + ")";
    }
}
