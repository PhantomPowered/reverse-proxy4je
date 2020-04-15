package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.api.connection.PacketSender;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.block.DefaultBlockAccess;
import com.github.derrop.proxy.block.chunk.Chunk;
import com.github.derrop.proxy.connection.PacketConstants;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.protocol.play.server.world.PacketPlayServerBlockUpdate;
import com.github.derrop.proxy.protocol.play.server.world.PacketPlayServerChunkBulk;
import com.github.derrop.proxy.protocol.play.server.world.PacketPlayServerChunkData;
import com.github.derrop.proxy.protocol.play.server.world.PacketPlayServerMultiBlockUpdate;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChunkCache implements PacketCacheHandler {

    private Collection<Chunk> chunks = new CopyOnWriteArrayList<>(); // TODO Reset on Dimension Change

    private Player connectedPlayer;

    private DefaultBlockAccess blockAccess;

    public void setBlockAccess(DefaultBlockAccess blockAccess) {
        this.blockAccess = blockAccess;
    }

    @Override
    public int[] getPacketIDs() {
        return new int[]{PacketConstants.CHUNK_DATA, PacketConstants.CHUNK_BULK, PacketConstants.BLOCK_UPDATE, PacketConstants.MULTI_BLOCK_UPDATE};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        PacketPlayServerChunkData[] data = null;
        DefinedPacket packet = newPacket.getDeserializedPacket();

        if (packet instanceof PacketPlayServerChunkData) {

            data = new PacketPlayServerChunkData[]{(PacketPlayServerChunkData) packet};

        } else if (packet instanceof PacketPlayServerChunkBulk) {

            PacketPlayServerChunkBulk chunkBulk = (PacketPlayServerChunkBulk) packet;

            data = new PacketPlayServerChunkData[chunkBulk.getX().length];
            for (int i = 0; i < data.length; i++) {
                data[i] = new PacketPlayServerChunkData(chunkBulk.getX()[i], chunkBulk.getZ()[i], chunkBulk.isB(), chunkBulk.getExtracted()[i]);
            }

        } else if (packet instanceof PacketPlayServerBlockUpdate) {

            PacketPlayServerBlockUpdate blockUpdate = (PacketPlayServerBlockUpdate) packet;

            this.handleBlockUpdate(blockUpdate.getPos(), blockUpdate.getBlockState());

        } else if (packet instanceof PacketPlayServerMultiBlockUpdate) {

            PacketPlayServerMultiBlockUpdate multiBlockUpdate = (PacketPlayServerMultiBlockUpdate) packet;

            for (PacketPlayServerMultiBlockUpdate.BlockUpdateData updateData : multiBlockUpdate.getUpdateData()) {
                this.handleBlockUpdate(updateData.getPos(), updateData.getBlockState());
            }

        }

        if (data == null) {
            return;
        }

        for (PacketPlayServerChunkData chunkData : data) {
            if (chunkData.getExtracted().dataLength == 0) {
                this.unload(chunkData.getX(), chunkData.getZ());
                continue;
            }

            this.load(chunkData);
        }
    }

    private void handleBlockUpdate(BlockPos pos, int newBlockState) {
        if (this.blockAccess != null) {
            this.blockAccess.handleBlockUpdate(pos, this.getBlockStateAt(pos), newBlockState);
        }

        Chunk chunk = this.getChunk(pos);
        if (chunk != null) {
            chunk.setBlockStateAt(pos.getX(), pos.getY(), pos.getZ(), newBlockState);
        }
    }

    private void load(PacketPlayServerChunkData chunkData) {
        Chunk chunk = new Chunk();
        chunk.fillChunk(chunkData);
        this.chunks.add(chunk);

        if (this.blockAccess != null) {
            this.blockAccess.handleChunkLoad(chunk);
        }
    }

    private void unload(int x, int z) {
        for (Chunk chunk : this.chunks) {
            if (chunk.getX() == x && chunk.getZ() == z) {
                if (this.blockAccess != null) {
                    this.blockAccess.handleChunkUnload(chunk);
                }
                this.chunks.remove(chunk);
            }
        }
    }

    public void setBlockStateAt(BlockPos pos, int blockState) {
        Chunk chunk = this.getChunk(pos);
        if (chunk == null) {
            return;
        }
        chunk.setBlockStateAt(pos.getX(), pos.getY(), pos.getZ(), blockState);

        if (this.connectedPlayer != null) {
            this.connectedPlayer.sendPacket(new PacketPlayServerBlockUpdate(pos, blockState));
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
    public void sendCached(PacketSender sender) {
        // todo chunks are sometimes not displayed correctly (the client loads the chunks - you can walk on the blocks - but all blocks are invisible): until you break a block in that chunk. Now fixed?

        if (sender instanceof Player) {
            this.connectedPlayer = (Player) sender;
        }

        for (Chunk chunk : this.chunks) {
            if (chunk.getLastChunkData() == null) {
                continue;
            }
            PacketPlayServerChunkData data = new PacketPlayServerChunkData(chunk.getX(), chunk.getZ(), chunk.getLastChunkData().isFullChunk(), chunk.getBytes());
            sender.sendPacket(data);
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
