package com.github.derrop.proxy.api.block;

import com.github.derrop.proxy.api.util.BlockPos;
import com.github.derrop.proxy.api.util.EnumFacing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BlockAccess {

    int getBlockState(@NotNull BlockPos pos);

    int getDefaultBlockState(@Nullable Material material);

    @NotNull
    int[] getValidBlockStates(@Nullable Material material);

    @NotNull
    Material getMaterial(@NotNull BlockPos pos);

    @NotNull
    Material getMaterial(int blockState);

    /**
     * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
     * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
     */
    boolean isAirBlock(@NotNull BlockPos pos);

    boolean isWaterBlock(@NotNull BlockPos pos);

    boolean canSeeSky(@NotNull BlockPos pos); // TODO not tested

    default int getStrongPower(@NotNull BlockPos pos, @NotNull EnumFacing direction) { // redstone (0 - 15)
        return 0;
    }

    void setMaterial(@NotNull BlockPos pos, @Nullable Material material);

    void setBlockState(@NotNull BlockPos pos, int blockState);

}
