package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.connection.PacketConstants;
import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import de.derrop.minecraft.proxy.connection.cache.packet.ChunkBulk;
import de.derrop.minecraft.proxy.connection.cache.packet.ChunkData;
import net.md_5.bungee.netty.ChannelWrapper;

import java.util.ArrayList;
import java.util.Collection;

public class ChunkCache implements PacketCacheHandler {

    private Collection<ChunkData> chunks = new ArrayList<>();

    @Override
    public int[] getPacketIDs() {
        return new int[]{PacketConstants.CHUNK_DATA, PacketConstants.CHUNK_BULK};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        ChunkData[] data = null;

        switch (newPacket.getPacketId()) {
            case PacketConstants.CHUNK_DATA:
                ChunkData chunkData = new ChunkData();
                chunkData.read(newPacket.getPacketData());
                data = new ChunkData[]{chunkData};
                break;

            case PacketConstants.CHUNK_BULK:
                ChunkBulk chunkBulk = new ChunkBulk();
                chunkBulk.read(newPacket.getPacketData());

                data = new ChunkData[chunkBulk.getX().length];
                for (int i = 0; i < data.length; i++) {
                    data[i] = new ChunkData(chunkBulk.getX()[i], chunkBulk.getZ()[i], chunkBulk.isB(), chunkBulk.getExtracted()[i]);
                }

                break;
        }

        if (data == null) {
            return;
        }

        for (ChunkData chunkData : data) {
            if (chunkData.getExtracted().dataLength == 0) {
                this.unload(chunkData.getX(), chunkData.getZ());
                continue;
            }

            this.load(chunkData);
        }
    }

    private void load(ChunkData chunkData) {
        this.chunks.add(chunkData);
    }

    private void unload(int x, int z) {
        this.chunks.removeIf(chunkData -> chunkData.getX() == x && chunkData.getZ() == z);
    }

    @Override
    public void sendCached(ChannelWrapper ch) {
        for (ChunkData chunk : new ArrayList<>(this.chunks)) {
            ch.write(chunk);
        }
    }
}
