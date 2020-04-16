package com.github.derrop.proxy.api.block;

import com.github.derrop.proxy.api.location.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface BlockAccess {

    // the consumer will be called with -1 when a chunk is unloaded
    void trackBlockUpdates(UUID trackerId, int[] states, BlockConsumer consumer);

    // the consumer will be called with -1 when a chunk is unloaded
    void trackBlockUpdates(UUID trackerId, Material material, BlockConsumer consumer);

    void untrackBlockUpdates(UUID trackerId);

    Collection<BlockPos> getPositions(int state);

    Collection<BlockPos> getPositions(int[] states);

    Collection<BlockPos> getPositions(Material material);

    int getBlockState(@NotNull BlockPos pos);

    @NotNull
    Material getMaterial(@NotNull BlockPos pos);

    /**
     * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
     * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
     */
    boolean isAirBlock(@NotNull BlockPos pos);

    boolean isWaterBlock(@NotNull BlockPos pos);

    boolean canSeeSky(@NotNull BlockPos pos); // TODO not tested

    default int getStrongPower(@NotNull BlockPos pos, @NotNull Facing direction) { // redstone (0 - 15)
        return 0;
    }

    void setMaterial(@NotNull BlockPos pos, @Nullable Material material);

    void setBlockState(@NotNull BlockPos pos, int blockState);

    BlockStateRegistry getBlockStateRegistry();

    int getDimension();

}
