package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntityStatus implements Packet {

    private int entityId;
    private byte status;

    public PacketPlayServerEntityStatus(int entityId, byte status) {
        this.entityId = entityId;
        this.status = status;
    }

    public PacketPlayServerEntityStatus() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_STATUS;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public byte getStatus() {
        return this.status;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readInt();
        this.status = protoBuf.readByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeInt(this.entityId);
        protoBuf.writeByte(this.status);
    }

    public String toString() {
        return "PacketPlayServerEntityStatus(entityId=" + this.getEntityId() + ", status=" + this.getStatus() + ")";
    }
}
