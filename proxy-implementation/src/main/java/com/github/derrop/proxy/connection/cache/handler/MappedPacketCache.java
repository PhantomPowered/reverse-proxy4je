package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.api.connection.PacketSender;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class MappedPacketCache<V extends Packet> implements PacketCacheHandler {

    private int packetId;
    private Map<Object, V> cachedPackets = new HashMap<>();
    private Function<V, Object> keyFunction;
    private Predicate<V> deleteTester;

    public MappedPacketCache(int packetId, Function<V, Object> keyFunction, Predicate<V> deleteTester) {
        this.packetId = packetId;
        this.keyFunction = keyFunction;
        this.deleteTester = deleteTester;
    }

    @Override
    public int[] getPacketIDs() {
        return new int[]{packetId};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        V packet = (V) newPacket.getDeserializedPacket();

        Object key = this.keyFunction.apply(packet);

        if (this.deleteTester.test(packet)) {
            this.cachedPackets.remove(key);
        } else {
            this.cachedPackets.put(key, packet);
        }
    }

    @Override
    public void sendCached(PacketSender con) {
        for (V value : this.cachedPackets.values()) {
            con.sendPacket(value);
        }
    }
}
