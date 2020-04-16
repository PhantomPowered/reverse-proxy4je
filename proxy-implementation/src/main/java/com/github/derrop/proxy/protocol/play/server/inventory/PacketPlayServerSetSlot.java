package com.github.derrop.proxy.protocol.play.server.inventory;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.connection.PacketUtil;
import com.github.derrop.proxy.connection.cache.InventoryItem;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerSetSlot implements Packet {

    private byte windowId;
    private int slot;
    private InventoryItem item;

    public PacketPlayServerSetSlot(byte windowId, int slot, InventoryItem item) {
        this.windowId = windowId;
        this.slot = slot;
        this.item = item;
    }

    public PacketPlayServerSetSlot() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SET_SLOT;
    }

    public byte getWindowId() {
        return this.windowId;
    }

    public int getSlot() {
        return this.slot;
    }

    public InventoryItem getItem() {
        return this.item;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.windowId = protoBuf.readByte();
        this.slot = protoBuf.readShort();
        this.item = PacketUtil.readItem(protoBuf);
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeByte(this.windowId);
        protoBuf.writeShort(this.slot);
        PacketUtil.writeItem(protoBuf, this.item);
    }

    public String toString() {
        return "PacketPlayServerSetSlot(windowId=" + this.getWindowId() + ", slot=" + this.getSlot() + ", item=" + this.getItem() + ")";
    }
}
