package com.github.derrop.proxy.protocol.play.client.inventory;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.api.item.ItemStack;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientSetCreativeItem implements Packet {

    private int slot;
    private ItemStack itemStack;

    public PacketPlayClientSetCreativeItem(int slot, ItemStack itemStack) {
        this.slot = slot;
        this.itemStack = itemStack;
    }

    public PacketPlayClientSetCreativeItem() {
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void read(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.slot = buf.readShort();
        this.itemStack = buf.readItemStack();
    }

    @Override
    public void write(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        buf.writeShort(this.slot);
        buf.writeItemStack(this.itemStack);
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.SET_CREATIVE_SLOT;
    }
}
