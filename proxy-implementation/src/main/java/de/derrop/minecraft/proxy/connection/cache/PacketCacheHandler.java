package de.derrop.minecraft.proxy.connection.cache;

import de.derrop.minecraft.proxy.api.connection.PacketReceiver;
import net.md_5.bungee.connection.UserConnection;

public interface PacketCacheHandler {

    int[] getPacketIDs();

    void cachePacket(PacketCache packetCache, CachedPacket newPacket);

    void sendCached(PacketReceiver con);

    default boolean sendOnSwitch() {
        return true;
    }

    default void onClientSwitch(UserConnection con) {
    }

}
