package com.github.derrop.proxy.block;

import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.block.BlockStateRegistry;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.util.BlockPos;
import com.github.derrop.proxy.block.chunk.Chunk;
import com.github.derrop.proxy.connection.cache.handler.ChunkCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class DefaultBlockAccess implements BlockAccess {

    private final Proxy proxy;
    private final ChunkCache chunkCache;

    public DefaultBlockAccess(Proxy proxy, ChunkCache chunkCache) {
        this.proxy = proxy;
        this.chunkCache = chunkCache;
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
        int[] states = this.getValidBlockStates(material);
        if (states.length == 0) {
            return Collections.emptySet();
        }

        return this.getPositions(states);
    }

    @Override
    public int getBlockState(@NotNull BlockPos pos) {
        return this.chunkCache.getBlockStateAt(pos);
    }

    @Override
    public @NotNull int[] getValidBlockStates(@Nullable Material material) {
        return material == null ? new int[0] : this.proxy.getServiceRegistry().getProviderUnchecked(BlockStateRegistry.class).getValidBlockStateIDs(material);
    }

    @Override
    public int getDefaultBlockState(@Nullable Material material) {
        int[] ids = this.getValidBlockStates(material);
        return ids.length != 0 ? ids[0] : 0;
    }

    @NotNull
    @Override
    public Material getMaterial(@NotNull BlockPos pos) {
        return this.getMaterial(this.getBlockState(pos));
    }

    @Override
    public @NotNull Material getMaterial(int blockState) {
        Material material = this.proxy.getServiceRegistry().getProviderUnchecked(BlockStateRegistry.class).getMaterial(blockState);
        return material != null ? material : Material.AIR;
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
        this.setBlockState(pos, this.getDefaultBlockState(material));
    }

    @Override
    public void setBlockState(@NotNull BlockPos pos, int blockState) {
        this.chunkCache.setBlockStateAt(pos, blockState);
    }
}
