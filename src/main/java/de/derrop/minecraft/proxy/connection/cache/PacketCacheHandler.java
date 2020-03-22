package de.derrop.minecraft.proxy.connection.cache;

import net.md_5.bungee.connection.UserConnection;
import net.md_5.bungee.netty.ChannelWrapper;

public interface PacketCacheHandler {

    int[] getPacketIDs();

    void cachePacket(PacketCache packetCache, CachedPacket newPacket);

    void sendCached(UserConnection con);

    default boolean sendOnSwitch() {
        return true;
    }

    default void onClientSwitch(UserConnection con) {
    }

}
