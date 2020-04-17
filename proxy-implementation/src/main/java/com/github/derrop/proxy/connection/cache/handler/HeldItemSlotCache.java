package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.api.connection.PacketSender;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.client.PacketPlayClientHeldItemSlot;
import com.github.derrop.proxy.protocol.play.server.entity.player.PacketPlayServerHeldItemSlot;

public class HeldItemSlotCache implements PacketCacheHandler {

    private int slot;

    @Override
    public int[] getPacketIDs() {
        return new int[]{ProtocolIds.ToClient.Play.HELD_ITEM_SLOT, ProtocolIds.FromClient.Play.HELD_ITEM_SLOT};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        this.slot = ((PacketPlayServerHeldItemSlot) newPacket.getDeserializedPacket()).getSlot();
    }

    @Override
    public void cacheClientPacket(PacketCache packetCache, Packet newPacket) {
        this.slot = ((PacketPlayClientHeldItemSlot) newPacket).getSlot();
    }

    @Override
    public void sendCached(PacketSender con) {
        con.sendPacket(new PacketPlayServerHeldItemSlot(this.slot));
    }
}
