package com.github.derrop.proxy.protocol.play.server.inventory;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.connection.PacketUtil;
import com.github.derrop.proxy.connection.cache.InventoryItem;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerWindowItems implements Packet {

    private byte windowId;
    private InventoryItem[] items;

    public PacketPlayServerWindowItems(byte windowId, InventoryItem[] items) {
        this.windowId = windowId;
        this.items = items;
    }

    public PacketPlayServerWindowItems() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.WINDOW_ITEMS;
    }

    public byte getWindowId() {
        return this.windowId;
    }

    public InventoryItem[] getItems() {
        return this.items;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.windowId = protoBuf.readByte();
        this.items = new InventoryItem[protoBuf.readShort()];

        for (int i = 0; i < this.items.length; i++) {
            this.items[i] = PacketUtil.readItem(protoBuf);
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeByte(this.windowId);
        protoBuf.writeShort(this.items.length);

        for (InventoryItem item : this.items) {
            PacketUtil.writeItem(protoBuf, item);
        }
    }

    public String toString() {
        return "PacketPlayServerWindowItems(windowId=" + this.getWindowId() + ", items=" + java.util.Arrays.deepToString(this.getItems()) + ")";
    }
}
