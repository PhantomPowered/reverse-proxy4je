package com.github.derrop.proxy.api.block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BlockStateRegistry {

    @NotNull
    int[] getValidBlockStateIDs(@NotNull Material material);

    @Nullable
    Material getMaterial(int blockStateId);

    boolean isMaterial(int blockStateId, Material material);

}
