package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.PacketConstants;
import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import de.derrop.minecraft.proxy.connection.cache.packet.ChunkData;
import de.derrop.minecraft.proxy.connection.cache.packet.UnloadChunk;
import net.md_5.bungee.netty.ChannelWrapper;

import java.util.ArrayList;
import java.util.Collection;

public class ChunkCache implements PacketCacheHandler {

    private Collection<ChunkData> chunks = new ArrayList<>();

    @Override
    public int[] getPacketIDs() {
        return new int[]{PacketConstants.CHUNK_DATA, PacketConstants.CHUNK_UNLOAD};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        switch (newPacket.getPacketId()) {
            case PacketConstants.CHUNK_DATA:
                ChunkData chunkData = new ChunkData();
                chunkData.read(newPacket.getPacketData());
                this.load(chunkData);
                break;
            case PacketConstants.CHUNK_UNLOAD:
                UnloadChunk unloadChunk = new UnloadChunk();
                unloadChunk.read(newPacket.getPacketData());
                this.unload(unloadChunk);
                break;
        }
    }

    private void load(ChunkData chunkData) {
        this.chunks.add(chunkData);
    }

    private void unload(UnloadChunk chunk) {
        this.chunks.removeIf(chunkData -> chunkData.getX() == chunk.getX() && chunkData.getZ() == chunk.getZ());
    }

    @Override
    public void sendCached(ChannelWrapper ch) {
        for (ChunkData chunk : this.chunks) {
            ch.write(chunk);
        }
    }
}
