package com.github.derrop.proxy.protocol.play.client;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientHeldItemSlot implements Packet {

    private int slot;

    public PacketPlayClientHeldItemSlot(int slot) {
        this.slot = slot;
    }

    public PacketPlayClientHeldItemSlot() {
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.slot = protoBuf.readShort();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeShort(this.slot);
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.HELD_ITEM_SLOT;
    }
}
