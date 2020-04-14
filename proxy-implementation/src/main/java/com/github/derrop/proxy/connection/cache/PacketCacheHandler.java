package com.github.derrop.proxy.connection.cache;

import com.github.derrop.proxy.api.connection.PacketSender;
import com.github.derrop.proxy.api.entity.player.Player;

public interface PacketCacheHandler {

    int[] getPacketIDs();

    void cachePacket(PacketCache packetCache, CachedPacket newPacket);

    void sendCached(PacketSender con);

    default boolean sendOnSwitch() {
        return true;
    }

    default void onClientSwitch(Player con) {
    }

}
