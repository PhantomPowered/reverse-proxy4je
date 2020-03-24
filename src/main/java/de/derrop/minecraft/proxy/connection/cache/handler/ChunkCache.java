package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.connection.PacketConstants;
import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import de.derrop.minecraft.proxy.connection.cache.packet.world.BlockUpdate;
import de.derrop.minecraft.proxy.connection.cache.packet.world.ChunkBulk;
import de.derrop.minecraft.proxy.connection.cache.packet.world.ChunkData;
import de.derrop.minecraft.proxy.connection.cache.packet.world.MultiBlockUpdate;
import de.derrop.minecraft.proxy.util.BlockPos;
import de.derrop.minecraft.proxy.util.chunk.Chunk;
import net.md_5.bungee.connection.UserConnection;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChunkCache implements PacketCacheHandler {

    private static final int AIR_BLOCK_STATE = 0;

    private Collection<Chunk> chunks = new CopyOnWriteArrayList<>();
    private Map<BlockPos, Integer> blockUpdates = new ConcurrentHashMap<>();

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

            Chunk chunk = this.getChunk(blockUpdate.getPos());
            if (chunk != null) {
                chunk.setBlockStateAt(blockUpdate.getPos().getX(), blockUpdate.getPos().getY(), blockUpdate.getPos().getZ(), blockUpdate.getBlockState());
            }

        } else if (packet instanceof MultiBlockUpdate) {

            MultiBlockUpdate multiBlockUpdate = (MultiBlockUpdate) packet;

            for (MultiBlockUpdate.BlockUpdateData updateData : multiBlockUpdate.getUpdateData()) {
                BlockPos pos = updateData.getPos();
                Chunk chunk = this.getChunk(pos);
                if (chunk != null) {
                    chunk.setBlockStateAt(pos.getX(), pos.getY(), pos.getZ(), updateData.getBlockState());
                }
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
        Chunk chunk = new Chunk();
        chunk.fillChunk(chunkData);
        this.chunks.add(chunk);
    }

    private void unload(int x, int z) {
        this.chunks.removeIf(chunkData -> chunkData.getX() == x && chunkData.getZ() == z);
        for (BlockPos pos : this.blockUpdates.keySet()) {
            if (pos.isInChunk(x, z)) {
                this.blockUpdates.remove(pos);
            }
        }
    }

    public int getBlockStateAt(BlockPos pos) {
        /*if (this.blockUpdates.containsKey(pos)) {
            return this.blockUpdates.get(pos); // todo this is the blockstate, not the material
        }*/

        Chunk chunk = this.getChunk(pos);
        if (chunk == null) {
            return -1;
        }

        return chunk.getBlockStateAt(pos.getX(), pos.getY(), pos.getZ());

        /*int i = 0;

        char[] chars = new char[4096];
        for (int k = 0; k < chars.length; k++) {
            chars[k] = (char) ((chunk.getExtracted().data[i + 1] & 255) << 8 | chunk.getExtracted().data[i] & 255);
            i += 2;
        }

        int cIndex = (pos.getY() & 15) << 8 | (pos.getZ() & 15) << 4 | (pos.getX() & 15);
        return chars[cIndex];*/

        /*for (ChunkData chunk : this.chunks) {
            if (pos.isInChunk(chunk.getX(), chunk.getZ())) {
                int i = 0;

                char[] chars = new char[4096];
                for (int k = 0; k < chars.length; k++) {
                    chars[k] = (char) ((chunk.getExtracted().data[i + 1] & 255) << 8 | chunk.getExtracted().data[i] & 255);
                    i += 2;
                }

                int cIndex = pos.getY() << 8 | pos.getZ() << 4 | pos.getX();
                if (cIndex >= 0) {
                    return chars[cIndex];
                }
            }
        }*/

    }

    public Chunk getChunk(int x, int z) {
        return this.chunks.stream().filter(chunkData -> chunkData.getX() == x && chunkData.getZ() == z).findFirst().orElse(null);
    }

    public Chunk getChunk(BlockPos pos) {
        return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public boolean isChunkLoaded(BlockPos pos) {
        return this.getChunk(pos) != null;
    }

    @Override
    public void sendCached(UserConnection con) {
        // todo chunks are sometimes not displayed correctly (the client loads the chunks - you can walk on the blocks - but all blocks are invisible): until you break a block in that chunk
        for (Chunk chunk : new ArrayList<>(this.chunks)) {
            ChunkData chunkData = new ChunkData();
            chunk.fillChunkData(chunkData);
            con.unsafe().sendPacket(chunkData);
        }

        for (Map.Entry<BlockPos, Integer> entry : this.blockUpdates.entrySet()) {
            con.unsafe().sendPacket(new BlockUpdate(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public void onClientSwitch(UserConnection con) {
        for (Chunk chunk : this.chunks) {
            /*ChunkData modChunk = new ChunkData(chunk.getX(), chunk.getZ(), chunk.isB(), new ChunkData.Extracted());
            modChunk.getExtracted().dataLength = 0;
            modChunk.getExtracted().data = new byte[0];*/
            ChunkData modChunk = new ChunkData(chunk.getX(), chunk.getZ(), chunk.getLastChunkData().isB(), new ChunkData.Extracted());
            modChunk.getExtracted().dataLength = 0;
            modChunk.getExtracted().data = new byte[0];
            con.unsafe().sendPacket(modChunk);
        }
        for (BlockPos pos : this.blockUpdates.keySet()) {
            con.unsafe().sendPacket(new BlockUpdate(pos, AIR_BLOCK_STATE));
        }
    }
}
