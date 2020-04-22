package com.github.derrop.proxy.protocol.play.client.inventory;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.api.util.ItemStack;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientClickWindow implements Packet {

    private int windowId;
    private int slot;
    private int usedButton;
    private short actionNumber;
    private ItemStack clickedItem;
    private int mode;

    public PacketPlayClientClickWindow(int windowId, int slot, int usedButton, short actionNumber, ItemStack clickedItem, int mode) {
        this.windowId = windowId;
        this.slot = slot;
        this.usedButton = usedButton;
        this.actionNumber = actionNumber;
        this.clickedItem = clickedItem;
        this.mode = mode;
    }

    public PacketPlayClientClickWindow() {
    }

    public int getWindowId() {
        return windowId;
    }

    public void setWindowId(int windowId) {
        this.windowId = windowId;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getUsedButton() {
        return usedButton;
    }

    public void setUsedButton(int usedButton) {
        this.usedButton = usedButton;
    }

    public short getActionNumber() {
        return actionNumber;
    }

    public void setActionNumber(short actionNumber) {
        this.actionNumber = actionNumber;
    }

    public ItemStack getClickedItem() {
        return clickedItem;
    }

    public void setClickedItem(ItemStack clickedItem) {
        this.clickedItem = clickedItem;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public void read(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.windowId = buf.readByte();
        this.slot = buf.readShort();
        this.usedButton = buf.readByte();
        this.actionNumber = buf.readShort();
        this.mode = buf.readByte();
        this.clickedItem = buf.readItemStack();
    }

    @Override
    public void write(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        buf.writeByte(this.windowId);
        buf.writeShort(this.slot);
        buf.writeByte(this.usedButton);
        buf.writeShort(this.actionNumber);
        buf.writeByte(this.mode);
        buf.writeItemStack(this.clickedItem);
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.WINDOW_CLICK;
    }

}
