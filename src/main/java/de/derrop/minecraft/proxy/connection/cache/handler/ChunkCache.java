package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.connection.PacketConstants;
import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import de.derrop.minecraft.proxy.connection.cache.packet.BlockUpdate;
import de.derrop.minecraft.proxy.connection.cache.packet.ChunkBulk;
import de.derrop.minecraft.proxy.connection.cache.packet.ChunkData;
import de.derrop.minecraft.proxy.connection.cache.packet.MultiBlockUpdate;
import de.derrop.minecraft.proxy.util.BlockPos;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChunkCache implements PacketCacheHandler {

    private Collection<ChunkData> chunks = new ArrayList<>();
    private Map<BlockPos, Integer> blockUpdates = new HashMap<>();

    @Override
    public int[] getPacketIDs() {
        return new int[]{PacketConstants.CHUNK_DATA, PacketConstants.CHUNK_BULK, PacketConstants.BLOCK_UPDATE, PacketConstants.MULTI_BLOCK_UPDATE};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        ChunkData[] data = null;
        DefinedPacket packet = newPacket.getDeserializedPacket();

        if (packet instanceof ChunkData) {

            data = new ChunkData[]{(ChunkData) packet};

        } else if (packet instanceof ChunkBulk) {

            ChunkBulk chunkBulk = (ChunkBulk) packet;

            data = new ChunkData[chunkBulk.getX().length];
            for (int i = 0; i < data.length; i++) {
                data[i] = new ChunkData(chunkBulk.getX()[i], chunkBulk.getZ()[i], chunkBulk.isB(), chunkBulk.getExtracted()[i]);
            }

        } else if (packet instanceof BlockUpdate) {

            BlockUpdate blockUpdate = (BlockUpdate) packet;

            this.blockUpdates.put(blockUpdate.getPos(), blockUpdate.getBlockState());

        } else if (packet instanceof MultiBlockUpdate) {

            MultiBlockUpdate multiBlockUpdate = (MultiBlockUpdate) packet;

            for (MultiBlockUpdate.BlockUpdateData updateData : multiBlockUpdate.getUpdateData()) {
                this.blockUpdates.put(updateData.getPos(), updateData.getBlockState());
            }

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
        // todo chunks are sometimes not displayed correctly (the client loads the chunks - you can walk on the blocks - but all blocks are invisible)
        for (ChunkData chunk : new ArrayList<>(this.chunks)) {
            ch.write(chunk);
        }

        for (Map.Entry<BlockPos, Integer> entry : new ArrayList<>(this.blockUpdates.entrySet())) {
            ch.write(new BlockUpdate(entry.getKey(), entry.getValue()));
        }
    }
}
