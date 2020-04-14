package com.github.derrop.proxy.api.block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BlockStateRegistry {

    @NotNull
    int[] getValidBlockStateIDs(@Nullable Material material);

    @NotNull
    BlockState[] getValidStates(@Nullable Material material);

    int getDefaultBlockState(@Nullable Material material);

    @NotNull
    BlockState getExactDefaultBlockState(@Nullable Material material);

    @NotNull
    BlockState getExactBlockState(int blockStateId);

    @NotNull
    Material getMaterial(int blockStateId);

    boolean isMaterial(int blockStateId, @NotNull Material material);

}
