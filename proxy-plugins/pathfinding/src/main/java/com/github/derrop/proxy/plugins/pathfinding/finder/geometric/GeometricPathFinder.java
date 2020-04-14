package com.github.derrop.proxy.plugins.pathfinding.finder.geometric;

import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.plugins.pathfinding.PathPoint;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GeometricPathFinder {

    protected Queue<PathPoint> sortByStartPoint(Collection<BlockPos> positions, BlockPos start) {
        Queue<PathPoint> result = new ConcurrentLinkedQueue<>();
        //TODO
        return result;
    }

}
