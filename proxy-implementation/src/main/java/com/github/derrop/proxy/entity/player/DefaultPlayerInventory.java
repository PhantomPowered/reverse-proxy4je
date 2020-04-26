package com.github.derrop.proxy.entity.player;

import com.github.derrop.proxy.api.entity.player.inventory.InventoryType;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.entity.player.inventory.PlayerInventory;
import com.github.derrop.proxy.api.util.ItemStack;
import com.github.derrop.proxy.protocol.play.server.inventory.PacketPlayServerCloseWindow;
import com.github.derrop.proxy.protocol.play.server.inventory.PacketPlayServerOpenWindow;
import com.github.derrop.proxy.protocol.play.server.inventory.PacketPlayServerSetSlot;
import com.github.derrop.proxy.protocol.play.server.inventory.PacketPlayServerWindowItems;
import net.kyori.text.Component;
import org.jetbrains.annotations.NotNull;

public class DefaultPlayerInventory implements PlayerInventory {

    private Player player;

    private Component title;
    private InventoryType type;
    private int size;//TODO
    private ItemStack[] content;

    private boolean opened = false;
    private byte windowId;

    public DefaultPlayerInventory(Player player) {
        this.player = player;
    }

    // TODO modify the fields in this class when receiving packets from the server
    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public byte getWindowId() {
        return windowId;
    }

    public void setWindowId(byte windowId) {
        this.windowId = windowId;
    }

    @Override
    public void open() {
        if (!this.opened && this.type != null) {
            this.player.sendPacket(new PacketPlayServerOpenWindow(this.windowId, this.type.getDefaultTitle(), this.title, this.type.getDefaultSize(), -1));
            this.setContent(this.content);
            this.opened = true;
        }
    }

    @Override
    public void close() {
        if (this.opened) {
            this.player.sendPacket(new PacketPlayServerCloseWindow(this.windowId));
            this.opened = false;
        }
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(Component component) {
        this.title = component;
    }

    @Override
    public InventoryType getType() {
        return this.type;
    }

    @Override
    public void setType(InventoryType type) {
        this.type = type;
        this.content = new ItemStack[type.getDefaultSize()];
    }

    @Override
    public void setContent(@NotNull ItemStack[] items) {
        this.content = items.clone();
        this.player.sendPacket(new PacketPlayServerWindowItems(this.windowId, this.content));
    }

    @Override
    public @NotNull ItemStack[] getContent() {
        return this.content;
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        if (slot < 0 || slot >= this.content.length) {
            return;
        }
        if (item == null) {
            item = ItemStack.NONE;
        }
        this.content[slot] = item;
        if (this.opened) {
            this.player.sendPacket(new PacketPlayServerSetSlot(this.windowId, slot, item));
        }
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        ItemStack item = slot < 0 || slot >= this.content.length ? null : this.content[slot];
        return item != null ? item : ItemStack.NONE;
    }
}
