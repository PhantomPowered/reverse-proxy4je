package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import net.md_5.bungee.connection.UserConnection;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class MappedPacketCache<V extends DefinedPacket> implements PacketCacheHandler {

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
    public void sendCached(UserConnection con) {
        for (V value : this.cachedPackets.values()) {
            con.unsafe().sendPacket(value);
        }
    }
}
