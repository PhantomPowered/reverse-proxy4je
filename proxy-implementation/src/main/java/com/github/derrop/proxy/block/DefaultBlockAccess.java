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
package com.github.derrop.proxy.block;

import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.block.BlockConsumer;
import com.github.derrop.proxy.api.block.BlockStateRegistry;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.block.chunk.Chunk;
import com.github.derrop.proxy.connection.cache.handler.ChunkCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBlockAccess implements BlockAccess {

    private final Proxy proxy;
    private final BlockStateRegistry registry;
    private final ChunkCache chunkCache;

    private final Map<UUID, BlockConsumer> blockTrackers = new ConcurrentHashMap<>();

    public DefaultBlockAccess(Proxy proxy, ChunkCache chunkCache) {
        this.proxy = proxy;
        this.registry = proxy.getServiceRegistry().getProviderUnchecked(BlockStateRegistry.class);
        this.chunkCache = chunkCache;
        chunkCache.setBlockAccess(this);
    }

    public void handleBlockUpdate(BlockPos pos, int oldState, int newState) {
        if (oldState != newState && !this.blockTrackers.isEmpty()) {
            for (BlockConsumer consumer : this.blockTrackers.values()) {
                consumer.accept(pos.getX(), pos.getY(), pos.getZ(), oldState, newState);
            }
        }
    }

    public void handleChunkLoad(Chunk chunk) {
        if (this.blockTrackers.isEmpty()) {
            return;
        }

        this.forEachStates(chunk, (x, y, z, oldState, state) -> {
            for (BlockConsumer consumer : this.blockTrackers.values()) {
                consumer.accept(x, y, z, oldState, state);
            }
        });
    }

    public void handleChunkUnload(Chunk chunk) {
        if (this.blockTrackers.isEmpty()) {
            return;
        }

        this.forEachStates(chunk, (x, y, z, oldState, state) -> {
            for (BlockConsumer consumer : this.blockTrackers.values()) {
                consumer.accept(x, y, z, state, -1);
            }
        });
    }

    private void forEachStates(Chunk chunk, BlockConsumer consumer) {
        int[][][] states = chunk.getAllBlockStates();
        for (int x = 0; x < states.length; x++) {
            for (int y = 0; y < states[x].length; y++) {
                for (int z = 0; z < states[x][y].length; z++) {
                    consumer.accept(x + (chunk.getX() << 4), y, z + (chunk.getZ() << 4), -1, states[x][y][z]);
                }
            }
        }
    }

    @Override
    public void trackBlockUpdates(UUID trackerId, int[] states, BlockConsumer consumer) {
        this.blockTrackers.put(trackerId, (x, y, z, oldState, state) -> {
            for (int allowedState : states) {
                if (allowedState == state || allowedState == oldState) {
                    consumer.accept(x, y, z, oldState, state);
                }
            }
        });

        for (int state : states) {
            for (BlockPos position : this.getPositions(state)) {
                consumer.accept(position.getX(), position.getY(), position.getZ(), -1, state);
            }
        }
    }

    @Override
    public void trackBlockUpdates(UUID trackerId, Material material, BlockConsumer consumer) {
        int[] states = this.registry.getValidBlockStateIDs(material);
        if (states.length == 0) {
            return;
        }

        this.trackBlockUpdates(trackerId, states, consumer);
    }

    @Override
    public void untrackBlockUpdates(UUID trackerId) {
        this.blockTrackers.remove(trackerId);
    }

    private Collection<BlockPos> mapPositionsByChunk(Chunk chunk, Collection<BlockPos> input) {
        Collection<BlockPos> result = new ArrayList<>(input.size());

        for (BlockPos pos : input) {
            result.add(pos.add(chunk.getX() << 4, 0, chunk.getZ() << 4));
        }

        return result;
    }

    @Override
    public Collection<BlockPos> getPositions(int state) {
        Collection<BlockPos> result = new ArrayList<>();

        for (Chunk chunk : this.chunkCache.getChunks()) {
            result.addAll(this.mapPositionsByChunk(chunk, chunk.getPositionsByState(state)));
        }

        return result;
    }

    @Override
    public Collection<BlockPos> getPositions(int[] states) {
        Collection<BlockPos> result = new ArrayList<>();

        for (Chunk chunk : this.chunkCache.getChunks()) {
            result.addAll(this.mapPositionsByChunk(chunk, chunk.getPositionsByStates(states)));
        }

        return result;
    }

    @Override
    public Collection<BlockPos> getPositions(Material material) {
        int[] states = this.registry.getValidBlockStateIDs(material);
        if (states.length == 0) {
            return Collections.emptySet();
        }

        return this.getPositions(states);
    }

    @Override
    public int getBlockState(@NotNull BlockPos pos) {
        return this.chunkCache.getBlockStateAt(pos);
    }

    @NotNull
    @Override
    public Material getMaterial(@NotNull BlockPos pos) {
        return this.registry.getMaterial(this.getBlockState(pos));
    }

    @Override
    public boolean isAirBlock(@NotNull BlockPos pos) {
        return this.getBlockState(pos) == 0;
    }

    @Override
    public boolean isWaterBlock(@NotNull BlockPos pos) {
        Material material = this.getMaterial(pos);
        return material == Material.WATER || material == Material.STATIONARY_WATER;
    }

    @Override
    public boolean canSeeSky(@NotNull BlockPos pos) {
        for (int i = 0; i < 255; i++) {
            if (this.getBlockState(pos.up(i)) <= 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setMaterial(@NotNull BlockPos pos, @Nullable Material material) {
        this.setBlockState(pos, this.registry.getDefaultBlockState(material));
    }

    @Override
    public void setBlockState(@NotNull BlockPos pos, int blockState) {
        this.chunkCache.setBlockStateAt(pos, blockState);
    }

    @Override
    public BlockStateRegistry getBlockStateRegistry() {
        return this.registry;
    }

    @Override
    public int getDimension() {
        return this.chunkCache.getDimension();
    }
}
