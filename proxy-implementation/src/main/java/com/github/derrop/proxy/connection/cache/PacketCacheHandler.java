package com.github.derrop.proxy.connection.cache;

import com.github.derrop.proxy.api.connection.PacketSender;
import net.md_5.bungee.connection.UserConnection;

public interface PacketCacheHandler {

    int[] getPacketIDs();

    void cachePacket(PacketCache packetCache, CachedPacket newPacket);

    void sendCached(PacketSender con);

    default boolean sendOnSwitch() {
        return true;
    }

    default void onClientSwitch(UserConnection con) {
    }

}
