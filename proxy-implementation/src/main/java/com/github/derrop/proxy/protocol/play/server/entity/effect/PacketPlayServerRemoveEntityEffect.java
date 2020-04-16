package com.github.derrop.proxy.protocol.play.server.entity.effect;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerRemoveEntityEffect implements Packet {

    public PacketPlayServerRemoveEntityEffect() {
    }

    public PacketPlayServerRemoveEntityEffect(int entityId, int effectId) {
        this.entityId = entityId;
        this.effectId = effectId;
    }

    private int entityId;
    private int effectId;

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.REMOVE_ENTITY_EFFECT;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.effectId = protoBuf.readUnsignedByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeByte(this.effectId);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int getEffectId() {
        return this.effectId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String toString() {
        return "PacketPlayServerRemoveEntityEffect(entityId=" + this.getEntityId() + ", effectId=" + this.getEffectId() + ")";
    }
}
