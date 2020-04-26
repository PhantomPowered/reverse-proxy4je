package com.github.derrop.proxy.plugins.betterlogin.connection;

import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.block.BlockConsumer;
import com.github.derrop.proxy.api.block.BlockStateRegistry;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.location.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class LoginBlockAccess implements BlockAccess {
    @Override
    public void trackBlockUpdates(UUID trackerId, int[] states, BlockConsumer consumer) {
    }

    @Override
    public void trackBlockUpdates(UUID trackerId, Material material, BlockConsumer consumer) {
    }

    @Override
    public void untrackBlockUpdates(UUID trackerId) {
    }

    @Override
    public Collection<BlockPos> getPositions(int state) {
        return Collections.emptyList();
    }

    @Override
    public Collection<BlockPos> getPositions(int[] states) {
        return Collections.emptyList();
    }

    @Override
    public Collection<BlockPos> getPositions(Material material) {
        return Collections.emptyList();
    }

    @Override
    public int getBlockState(@NotNull BlockPos pos) {
        return 0;
    }

    @Override
    public @NotNull Material getMaterial(@NotNull BlockPos pos) {
        return Material.AIR;
    }

    @Override
    public boolean isAirBlock(@NotNull BlockPos pos) {
        return true;
    }

    @Override
    public boolean isWaterBlock(@NotNull BlockPos pos) {
        return false;
    }

    @Override
    public boolean canSeeSky(@NotNull BlockPos pos) {
        return true;
    }

    @Override
    public void setMaterial(@NotNull BlockPos pos, @Nullable Material material) {
    }

    @Override
    public void setBlockState(@NotNull BlockPos pos, int blockState) {
    }

    @Override
    public BlockStateRegistry getBlockStateRegistry() {
        return null;
    }

    @Override
    public int getDimension() {
        return 0;
    }
}
