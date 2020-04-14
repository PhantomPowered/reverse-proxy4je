package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.connection.PacketConstants;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.connection.cache.packet.world.BlockUpdate;
import com.github.derrop.proxy.connection.cache.packet.world.ChunkBulk;
import com.github.derrop.proxy.connection.cache.packet.world.ChunkData;
import com.github.derrop.proxy.connection.cache.packet.world.MultiBlockUpdate;
import com.github.derrop.proxy.api.util.BlockPos;
import com.github.derrop.proxy.block.chunk.Chunk;
import com.github.derrop.proxy.api.connection.PacketSender;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChunkCache implements PacketCacheHandler {

    private Collection<Chunk> chunks = new CopyOnWriteArrayList<>();

    private Player connectedPlayer;

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
    }

    public void setBlockStateAt(BlockPos pos, int blockState) {
        Chunk chunk = this.getChunk(pos);
        if (chunk == null) {
            return;
        }
        chunk.setBlockStateAt(pos.getX(), pos.getY(), pos.getZ(), blockState);

        if (this.connectedPlayer != null) {
            this.connectedPlayer.sendPacket(new BlockUpdate(pos, blockState));
        }
    }

    public int getBlockStateAt(BlockPos pos) {
        Chunk chunk = this.getChunk(pos);
        if (chunk == null) {
            return -1;
        }

        return chunk.getBlockStateAt(pos.getX(), pos.getY(), pos.getZ());
    }

    public Collection<Chunk> getChunks() {
        return this.chunks;
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
    public void sendCached(PacketSender con) {
        // todo chunks are sometimes not displayed correctly (the client loads the chunks - you can walk on the blocks - but all blocks are invisible): until you break a block in that chunk. Now fixed?
        for (Chunk chunk : new ArrayList<>(this.chunks)) {
            ChunkData chunkData = new ChunkData();
            chunk.fillChunkData(chunkData);
            con.sendPacket(chunkData);
        }

        if (con instanceof Player) {
            this.connectedPlayer = (Player) con;
        }
    }

    @Override
    public void onClientSwitch(Player con) {
        /*for (Chunk chunk : this.chunks) {
            ChunkData modChunk = new ChunkData(chunk.getX(), chunk.getZ(), chunk.getLastChunkData().isB(), new ChunkData.Extracted());
            modChunk.getExtracted().dataLength = 0;
            modChunk.getExtracted().data = new byte[0];
            con.sendPacket(modChunk);
        }*/
    }
}
