package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntityAttach implements Packet, EntityPacket {

    private int leash;
    private int entityId;
    private int vehicleEntityId;

    public PacketPlayServerEntityAttach(int leash, int entityId, int vehicleEntityId) {
        this.leash = leash;
        this.entityId = entityId;
        this.vehicleEntityId = vehicleEntityId;
    }

    public PacketPlayServerEntityAttach() {
    }

    public int getLeash() {
        return leash;
    }

    public void setLeash(int leash) {
        this.leash = leash;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getVehicleEntityId() {
        return vehicleEntityId;
    }

    public void setVehicleEntityId(int vehicleEntityId) {
        this.vehicleEntityId = vehicleEntityId;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readInt();
        this.vehicleEntityId = protoBuf.readInt();
        this.leash = protoBuf.readUnsignedByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeInt(this.entityId);
        protoBuf.writeInt(this.vehicleEntityId);
        protoBuf.writeByte(this.leash);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_ATTACH;
    }
}
