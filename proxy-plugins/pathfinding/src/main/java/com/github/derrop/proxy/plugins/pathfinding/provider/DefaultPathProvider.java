package com.github.derrop.proxy.plugins.pathfinding.provider;

import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.plugins.pathfinding.Path;
import com.github.derrop.proxy.plugins.pathfinding.finder.astar.AStarPathFinder;
import com.github.derrop.proxy.plugins.pathfinding.finder.geometric.CirclePathFinder;
import com.github.derrop.proxy.plugins.pathfinding.finder.geometric.RectanglePathFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultPathProvider implements PathProvider {

    private AStarPathFinder aStarPathFinder = new AStarPathFinder();
    private CirclePathFinder circlePathFinder = new CirclePathFinder();
    private RectanglePathFinder rectanglePathFinder = new RectanglePathFinder();

    @Override
    public Path findShortestPath(@NotNull BlockAccess access, @NotNull BlockPos start, @NotNull BlockPos end) {
        return new Path(this.aStarPathFinder.findPath(access, start, end));
    }

    @Override
    public Path findCirclePath(@NotNull BlockAccess access, @Nullable BlockPos start, @NotNull BlockPos center, int radius) {
        return null;
    }

    @Override
    public Path findRectanglePath(@NotNull BlockAccess access, @Nullable BlockPos start, @NotNull BlockPos pos1, @NotNull BlockPos pos2) {
        return new Path(this.rectanglePathFinder.findRectanglePath(access, start, pos1, pos2));
    }
}
