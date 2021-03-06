package com.github.phantompowered.proxy.protocol.play.server.entity;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.item.ItemStack;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntityEquipment implements Packet, EntityPacket {

    private int entityId;
    private int slot;
    private ItemStack item;

    public PacketPlayServerEntityEquipment(int entityId, int slot, ItemStack item) {
        this.entityId = entityId;
        this.slot = slot;
        this.item = item;
    }

    public PacketPlayServerEntityEquipment() {
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.slot = protoBuf.readShort();
        this.item = protoBuf.readItemStack();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeShort(this.slot);
        protoBuf.writeItemStack(this.item);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_EQUIPMENT;
    }

    @Override
    public String toString() {
        return "PacketPlayServerEntityEquipment{"
                + "entityId=" + entityId
                + ", slot=" + slot
                + ", item=" + item
                + '}';
    }
}
