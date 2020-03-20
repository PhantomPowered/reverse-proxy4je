package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.PacketConstants;
import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import net.md_5.bungee.netty.ChannelWrapper;

public class ChunkCache implements PacketCacheHandler {
    @Override
    public int[] getPacketIDs() {
        return new int[]{PacketConstants.CHUNK, PacketConstants.CHUNK_UNLOAD};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        switch (newPacket.getPacketId()) {
            case PacketConstants.CHUNK:
                this.load();
                break;
            case PacketConstants.CHUNK_UNLOAD:
                this.unload();
                break;
        }
    }

    private void load() {

    }

    private void unload() {

    }

    @Override
    public void sendCached(ChannelWrapper ch) {

    }
}
