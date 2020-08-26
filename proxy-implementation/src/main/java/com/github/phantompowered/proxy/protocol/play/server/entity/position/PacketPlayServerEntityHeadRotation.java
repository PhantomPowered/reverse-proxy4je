package com.github.phantompowered.proxy.protocol.play.server.entity.position;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.server.entity.EntityPacket;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntityHeadRotation implements Packet, EntityPacket {

    private int entityId;
    private byte yaw;

    public PacketPlayServerEntityHeadRotation(int entityId, byte yaw) {
        this.entityId = entityId;
        this.yaw = yaw;
    }

    public PacketPlayServerEntityHeadRotation() {
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public byte getYaw() {
        return yaw;
    }

    public void setYaw(byte yaw) {
        this.yaw = yaw;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.yaw = protoBuf.readByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeByte(this.yaw);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_HEAD_ROTATION;
    }

    @Override
    public String toString() {
        return "PacketPlayServerEntityHeadRotation{"
                + "entityId=" + entityId
                + ", yaw=" + yaw
                + '}';
    }
}
