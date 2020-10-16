package com.github.phantompowered.proxy.connection.player;

import com.github.phantompowered.proxy.api.block.material.Material;
import com.github.phantompowered.proxy.api.item.ItemStack;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.player.inventory.InventoryType;
import com.github.phantompowered.proxy.api.player.inventory.PlayerInventory;
import com.github.phantompowered.proxy.item.ProxyItemStack;
import com.github.phantompowered.proxy.protocol.play.server.inventory.PacketPlayServerCloseWindow;
import com.github.phantompowered.proxy.protocol.play.server.inventory.PacketPlayServerOpenWindow;
import com.github.phantompowered.proxy.protocol.play.server.inventory.PacketPlayServerSetSlot;
import com.github.phantompowered.proxy.protocol.play.server.inventory.PacketPlayServerWindowItems;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

//TODO: https://github.com/Exceptionflug/protocolize/
public class DefaultPlayerInventory implements PlayerInventory {

    private final Player player;

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

    @Override
    public byte getWindowId() {
        return windowId;
    }

    @Override
    public void setWindowId(byte windowId) {
        this.windowId = windowId;
    }

    @Override
    public void open() {
        if (this.type != null) {
            // TODO support other types than chests and custom sizes
            this.player.sendPacket(new PacketPlayServerOpenWindow(this.windowId, "minecraft:container", this.title != null ? this.title : Component.text(this.type.getDefaultTitle()), this.type.getDefaultSize(), -1));
            for (int i = 0; i < this.content.length; i++) {
                if (this.content[i] != null && this.content[i].getMaterial() != Material.AIR) {
                    this.player.sendPacket(new PacketPlayServerSetSlot(this.windowId, i, this.content[i]));
                }
            }
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
    public @NotNull ItemStack[] getContent() {
        return this.content;
    }

    @Override
    public void setContent(@NotNull ItemStack[] items) {
        this.content = items.clone();
        this.player.sendPacket(new PacketPlayServerWindowItems(this.windowId, this.content));
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        if (slot < 0 || slot >= this.content.length) {
            return;
        }
        if (item == null) {
            item = ProxyItemStack.AIR;
        }
        this.content[slot] = item;
        if (this.opened) {
            this.player.sendPacket(new PacketPlayServerSetSlot(this.windowId, slot, item));
        }
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        ItemStack item = slot < 0 || slot >= this.content.length ? null : this.content[slot];
        return item != null ? item : ProxyItemStack.AIR;
    }
}
