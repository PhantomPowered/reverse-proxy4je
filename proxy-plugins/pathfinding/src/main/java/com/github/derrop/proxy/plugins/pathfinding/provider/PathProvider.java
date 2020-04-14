package com.github.derrop.proxy.plugins.pathfinding.provider;

import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.plugins.pathfinding.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PathProvider {

    Path findShortestPath(@NotNull BlockAccess access, @NotNull BlockPos start, @NotNull BlockPos end);

    Path findCirclePath(@NotNull BlockAccess access, @Nullable BlockPos start, @NotNull BlockPos center, int radius);

    Path findRectanglePath(@NotNull BlockAccess access, @Nullable BlockPos start, @NotNull BlockPos pos1, @NotNull BlockPos pos2);

}
