package com.github.derrop.proxy.block;

import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.block.BlockStateRegistry;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.util.BlockPos;
import com.github.derrop.proxy.connection.cache.handler.ChunkCache;

public class DefaultBlockAccess implements BlockAccess {

    private final Proxy proxy;
    private final ChunkCache chunkCache;

    public DefaultBlockAccess(Proxy proxy, ChunkCache chunkCache) {
        this.proxy = proxy;
        this.chunkCache = chunkCache;
    }

    @Override
    public int getBlockState(BlockPos pos) {
        return this.chunkCache.getBlockStateAt(pos);
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return this.getBlockState(pos) == 0;
    }

    @Override
    public boolean isWaterBlock(BlockPos pos) {
        Material material = this.proxy.getServiceRegistry().getProviderUnchecked(BlockStateRegistry.class).getMaterial(this.getBlockState(pos));
        return material == Material.WATER || material == Material.STATIONARY_WATER;
    }
}
