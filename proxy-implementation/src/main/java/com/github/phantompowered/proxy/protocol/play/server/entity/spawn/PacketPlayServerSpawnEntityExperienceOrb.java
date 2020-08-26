package com.github.phantompowered.proxy.protocol.play.server.entity.spawn;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.util.PositionedPacket;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.server.entity.EntityPacket;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerSpawnEntityExperienceOrb implements Packet, EntityPacket, PositionedPacket {

    private int entityId;
    private int x;
    private int y;
    private int z;
    private int xpValue;

    public PacketPlayServerSpawnEntityExperienceOrb(int entityId, int x, int y, int z, int xpValue) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xpValue = xpValue;
    }

    public PacketPlayServerSpawnEntityExperienceOrb() {
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public byte getYaw() {
        return 0;
    }

    @Override
    public void setYaw(byte yaw) {
    }

    @Override
    public byte getPitch() {
        return 0;
    }

    @Override
    public void setPitch(byte pitch) {
    }

    public int getXpValue() {
        return xpValue;
    }

    public void setXpValue(int xpValue) {
        this.xpValue = xpValue;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.x = protoBuf.readInt();
        this.y = protoBuf.readInt();
        this.z = protoBuf.readInt();
        this.xpValue = protoBuf.readShort();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeInt(this.x);
        protoBuf.writeInt(this.y);
        protoBuf.writeInt(this.z);
        protoBuf.writeShort(this.xpValue);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SPAWN_ENTITY_EXPERIENCE_ORB;
    }
}
