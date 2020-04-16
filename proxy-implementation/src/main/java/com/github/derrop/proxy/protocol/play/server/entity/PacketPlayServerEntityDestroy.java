package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntityDestroy implements Packet {

    private int[] entityIds;

    public PacketPlayServerEntityDestroy(int[] entityIds) {
        this.entityIds = entityIds;
    }

    public PacketPlayServerEntityDestroy() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_DESTROY;
    }

    public int[] getEntityIds() {
        return this.entityIds;
    }

    public void setEntityIds(int[] entityIds) {
        this.entityIds = entityIds;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityIds = new int[protoBuf.readVarInt()];
        for (int i = 0; i < this.entityIds.length; i++) {
            this.entityIds[i] = protoBuf.readVarInt();
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityIds.length);
        for (int entityId : this.entityIds) {
            protoBuf.writeVarInt(entityId);
        }
    }

    public String toString() {
        return "PacketPlayServerEntityDestroy(entityIds=" + java.util.Arrays.toString(this.getEntityIds()) + ")";
    }
}
