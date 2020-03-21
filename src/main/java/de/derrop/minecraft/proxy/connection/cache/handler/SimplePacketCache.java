package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;

public class SimplePacketCache implements PacketCacheHandler {

    private int packetId;

    private DefinedPacket lastPacket;

    public SimplePacketCache(int packetId) {
        this.packetId = packetId;
    }

    @Override
    public int[] getPacketIDs() {
        return new int[]{this.packetId};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        this.lastPacket = newPacket.getDeserializedPacket();
    }

    @Override
    public void sendCached(ChannelWrapper ch) {
        if (this.lastPacket != null) {
            ch.write(this.lastPacket);
        }
    }
}
