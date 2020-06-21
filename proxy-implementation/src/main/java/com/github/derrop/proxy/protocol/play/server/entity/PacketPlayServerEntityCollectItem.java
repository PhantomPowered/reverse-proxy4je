package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntityCollectItem implements Packet, EntityPacket {

    private int collectedItemEntityId;
    private int entityId;

    public PacketPlayServerEntityCollectItem(int collectedItemEntityId, int entityId) {
        this.collectedItemEntityId = collectedItemEntityId;
        this.entityId = entityId;
    }

    public PacketPlayServerEntityCollectItem() {
    }

    public int getCollectedItemEntityId() {
        return collectedItemEntityId;
    }

    public void setCollectedItemEntityId(int collectedItemEntityId) {
        this.collectedItemEntityId = collectedItemEntityId;
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
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.collectedItemEntityId = protoBuf.readVarInt();
        this.entityId = protoBuf.readVarInt();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.collectedItemEntityId);
        protoBuf.writeVarInt(this.entityId);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.COLLECT_ITEM;
    }
}
