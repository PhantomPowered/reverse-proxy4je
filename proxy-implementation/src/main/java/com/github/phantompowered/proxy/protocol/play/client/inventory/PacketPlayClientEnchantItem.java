package com.github.phantompowered.proxy.protocol.play.client.inventory;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientEnchantItem implements Packet {

    private int windowId;
    private int button;

    public PacketPlayClientEnchantItem(int windowId, int button) {
        this.windowId = windowId;
        this.button = button;
    }

    public PacketPlayClientEnchantItem() {
    }

    public int getWindowId() {
        return windowId;
    }

    public void setWindowId(int windowId) {
        this.windowId = windowId;
    }

    public int getButton() {
        return button;
    }

    public void setButton(int button) {
        this.button = button;
    }

    @Override
    public void read(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.windowId = buf.readByte();
        this.button = buf.readByte();
    }

    @Override
    public void write(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        buf.writeByte(this.windowId);
        buf.writeByte(this.button);
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.ENCHANT_ITEM;
    }
}
