package com.github.phantompowered.proxy.protocol.play.client.inventory;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.item.ItemStack;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.api.player.inventory.ClickType;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientClickWindow implements Packet {

    private int windowId;
    private int slot;
    private int usedButton;
    private short actionNumber;
    private ItemStack clickedItem;
    private int mode;

    public PacketPlayClientClickWindow(int windowId, int slot, short actionNumber, ItemStack clickedItem, ClickType type) {
        this(windowId, type == ClickType.WINDOW_BORDER_LEFT || type == ClickType.WINDOW_BORDER_RIGHT ? -1 : slot,
                type.isLeftClick() || type == ClickType.WINDOW_BORDER_LEFT ? 0
                        : type.isRightClick() || type == ClickType.WINDOW_BORDER_RIGHT ? 1
                        : type == ClickType.MIDDLE ? 2 : 0,
                actionNumber,
                clickedItem,
                type == ClickType.LEFT || type == ClickType.RIGHT ? 0
                        : type == ClickType.SHIFT_LEFT || type == ClickType.SHIFT_RIGHT ? 1
                        : type == ClickType.NUMBER_KEY ? 2
                        : type == ClickType.MIDDLE || type == ClickType.UNKNOWN ? 3
                        : type == ClickType.DROP || type == ClickType.CONTROL_DROP ? 4
                        : type == ClickType.DOUBLE_CLICK ? 6
                        : -1
        );
    }

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

    public ClickType getClick() {
        if (this.slot == -1) {
            return this.usedButton == 0 ? ClickType.WINDOW_BORDER_LEFT : ClickType.WINDOW_BORDER_RIGHT;
        } else if (this.mode == 0) {
            if (this.usedButton == 0) {
                return ClickType.LEFT;
            } else if (this.usedButton == 1) {
                return ClickType.RIGHT;
            }
        } else if (this.mode == 1) {
            if (this.usedButton == 0) {
                return ClickType.SHIFT_LEFT;
            } else if (this.usedButton == 1) {
                return ClickType.SHIFT_RIGHT;
            }
        } else if (this.mode == 2) {
            return ClickType.NUMBER_KEY;
        } else if (this.mode == 3) {
            return this.usedButton == 2 ? ClickType.MIDDLE : ClickType.UNKNOWN;
        } else if (this.mode == 4) {
            if (this.slot >= 0) {
                return this.usedButton == 0 ? ClickType.DROP : ClickType.CONTROL_DROP;
            } else {
                return this.usedButton == 1 ? ClickType.RIGHT : ClickType.LEFT;
            }
        } else if (this.mode == 6) {
            return ClickType.DOUBLE_CLICK;
        }
        return null;
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
