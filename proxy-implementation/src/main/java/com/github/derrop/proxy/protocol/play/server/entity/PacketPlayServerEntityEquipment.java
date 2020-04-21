package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.connection.PacketUtil;
import com.github.derrop.proxy.connection.cache.InventoryItem;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntityEquipment implements Packet, EntityPacket {

    private int entityId;
    private int slot;
    private InventoryItem item;

    public PacketPlayServerEntityEquipment(int entityId, int slot, InventoryItem item) {
        this.entityId = entityId;
        this.slot = slot;
        this.item = item;
    }

    public PacketPlayServerEntityEquipment() {
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public InventoryItem getItem() {
        return item;
    }

    public void setItem(InventoryItem item) {
        this.item = item;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.slot = protoBuf.readShort();
        this.item = PacketUtil.readItem(protoBuf);
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeShort(this.slot);
        PacketUtil.writeItem(protoBuf, this.item);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_EQUIPMENT;
    }
}
