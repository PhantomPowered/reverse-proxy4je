package com.github.derrop.proxy.protocol.play.server.entity.player;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerHeldItemSlot implements Packet {

    private int slot;

    public PacketPlayServerHeldItemSlot(int slot) {
        this.slot = slot;
    }

    public PacketPlayServerHeldItemSlot() {
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.slot = protoBuf.readByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeByte(this.slot);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.HELD_ITEM_SLOT;
    }
}
