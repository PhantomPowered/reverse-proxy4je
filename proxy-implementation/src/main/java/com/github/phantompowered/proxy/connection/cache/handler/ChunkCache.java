/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.phantompowered.proxy.connection.cache.handler;

import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.PacketSender;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.block.DefaultBlockAccess;
import com.github.phantompowered.proxy.block.chunk.Chunk;
import com.github.phantompowered.proxy.connection.ConnectedProxyClient;
import com.github.phantompowered.proxy.connection.cache.PacketCache;
import com.github.phantompowered.proxy.connection.cache.PacketCacheHandler;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.server.PacketPlayServerRespawn;
import com.github.phantompowered.proxy.protocol.play.server.world.material.PacketPlayServerBlockChange;
import com.github.phantompowered.proxy.protocol.play.server.world.material.PacketPlayServerMapChunk;
import com.github.phantompowered.proxy.protocol.play.server.world.material.PacketPlayServerMapChunkBulk;
import com.github.phantompowered.proxy.protocol.play.server.world.material.PacketPlayServerMultiBlockChange;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChunkCache implements PacketCacheHandler {

    private final Collection<Chunk> chunks = new CopyOnWriteArrayList<>();
    private int dimension;

    private Player connectedPlayer;

    private DefaultBlockAccess blockAccess;

    public void setBlockAccess(DefaultBlockAccess blockAccess) {
        this.blockAccess = blockAccess;
    }

    @Override
    public int[] getPacketIDs() {
        return new int[]{
                ProtocolIds.ToClient.Play.MAP_CHUNK,
                ProtocolIds.ToClient.Play.MAP_CHUNK_BULK,
                ProtocolIds.ToClient.Play.BLOCK_CHANGE,
                ProtocolIds.ToClient.Play.MULTI_BLOCK_CHANGE,
                ProtocolIds.ToClient.Play.RESPAWN
        };
    }

    @Override
    public void cachePacket(PacketCache packetCache, Packet packet) {

        this.dimension = packetCache.getTargetProxyClient().getDimension();

        if (packet instanceof PacketPlayServerRespawn) {

            this.chunks.clear();

        } else if (packet instanceof PacketPlayServerMapChunk) {

            PacketPlayServerMapChunk chunkData = (PacketPlayServerMapChunk) packet;

            Chunk chunk = this.load(packetCache, chunkData);
            if (chunk != null) {
                chunkData.setExtracted(chunk.getBytes(this.dimension));
            }

        } else if (packet instanceof PacketPlayServerMapChunkBulk) {

            PacketPlayServerMapChunkBulk chunkBulk = (PacketPlayServerMapChunkBulk) packet;

            for (int i = 0; i < chunkBulk.getX().length; i++) {
                PacketPlayServerMapChunk chunkData = new PacketPlayServerMapChunk(chunkBulk.getX()[i], chunkBulk.getZ()[i], chunkBulk.isB(), chunkBulk.getExtracted()[i]);

                Chunk chunk = this.load(packetCache, chunkData);
                if (chunk != null) {
                    chunkBulk.getExtracted()[i] = chunk.getBytes(this.dimension);
                }
            }

        } else if (packet instanceof PacketPlayServerBlockChange) {

            PacketPlayServerBlockChange blockUpdate = (PacketPlayServerBlockChange) packet;

            this.handleBlockUpdate(blockUpdate.getPos(), blockUpdate.getBlockState());

        } else if (packet instanceof PacketPlayServerMultiBlockChange) {

            PacketPlayServerMultiBlockChange multiBlockUpdate = (PacketPlayServerMultiBlockChange) packet;

            for (PacketPlayServerMultiBlockChange.BlockUpdateData updateData : multiBlockUpdate.getUpdateData()) {
                this.handleBlockUpdate(updateData.getPos(), updateData.getBlockState());
            }

        }
    }

    private void handleBlockUpdate(Location pos, int newBlockState) {
        if (this.blockAccess != null) {
            this.blockAccess.handleBlockUpdate(pos, this.getBlockStateAt(pos), newBlockState);
        }

        Chunk chunk = this.getChunk(pos);
        if (chunk != null) {
            chunk.setBlockStateAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), newBlockState);
        }
    }

    private Chunk load(PacketCache cache, PacketPlayServerMapChunk chunkData) {
        if (chunkData.getExtracted().dataLength == 0) {
            this.unload(cache, chunkData.getX(), chunkData.getZ());
            return null;
        }

        Chunk chunk = new Chunk();
        chunk.fillChunk(chunkData, this.dimension);
        this.chunks.add(chunk);

        if (this.blockAccess != null) {
            this.blockAccess.handleChunkLoad(cache.getTargetProxyClient().getConnection(), chunk);
        }

        return chunk;
    }

    private void unload(PacketCache cache, int x, int z) {
        for (Chunk chunk : this.chunks) {
            if (chunk.getX() == x && chunk.getZ() == z) {
                if (this.blockAccess != null) {
                    this.blockAccess.handleChunkUnload(cache.getTargetProxyClient().getConnection(), chunk);
                }
                this.chunks.remove(chunk);
            }
        }
    }

    public void setBlockStateAt(Location pos, int blockState) {
        Chunk chunk = this.getChunk(pos);
        if (chunk == null) {
            return;
        }
        chunk.setBlockStateAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), blockState);

        if (this.connectedPlayer != null) {
            this.connectedPlayer.sendPacket(new PacketPlayServerBlockChange(pos, blockState));
        }
    }

    public int getBlockStateAt(Location pos) {
        Chunk chunk = this.getChunk(pos);
        if (chunk == null) {
            return -1;
        }

        return chunk.getBlockStateAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
    }

    public Collection<Chunk> getChunks() {
        return this.chunks;
    }

    public Chunk getChunk(int x, int z) {
        return this.chunks.stream().filter(chunkData -> chunkData.getX() == x && chunkData.getZ() == z).findFirst().orElse(null);
    }

    public Chunk getChunk(Location pos) {
        return this.getChunk(pos.getBlockX() >> 4, pos.getBlockZ() >> 4);
    }

    public boolean isChunkLoaded(Location pos) {
        return this.getChunk(pos) != null;
    }

    public int getDimension() {
        return this.dimension;
    }

    @Override
    public void sendCached(PacketSender sender, ConnectedProxyClient targetProxyClient) {
        if (sender instanceof Player) {
            this.connectedPlayer = (Player) sender;
        }

        for (Chunk chunk : this.chunks) {
            if (chunk.getLastChunkData() == null) {
                continue;
            }
            PacketPlayServerMapChunk data = new PacketPlayServerMapChunk(chunk.getX(), chunk.getZ(), true, chunk.getBytes(this.dimension));
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
