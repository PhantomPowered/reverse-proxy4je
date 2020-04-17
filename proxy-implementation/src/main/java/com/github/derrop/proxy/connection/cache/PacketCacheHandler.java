package com.github.derrop.proxy.connection.cache;

import com.github.derrop.proxy.api.connection.PacketSender;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.network.Packet;

public interface PacketCacheHandler {

    int[] getPacketIDs();

    void cachePacket(PacketCache packetCache, CachedPacket newPacket);

    default void cacheClientPacket(PacketCache packetCache, Packet newPacket) {
    }

    void sendCached(PacketSender con);

    default boolean sendOnSwitch() {
        return true;
    }

    default void onClientSwitch(Player con) {
    }

}
