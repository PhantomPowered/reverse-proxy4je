package com.github.derrop.proxy.plugins.pathfinding.finder;

import com.github.derrop.proxy.plugins.pathfinding.PathPoint;
import org.jetbrains.annotations.ApiStatus;

public class PathFindInteraction {

    private boolean cancelled;
    private boolean completed;
    private PathPoint currentPoint;

    public PathFindInteraction() {
    }

    @ApiStatus.Internal
    public void setCurrentPoint(PathPoint currentPoint) {
        this.currentPoint = currentPoint;
    }

    @ApiStatus.Internal
    public void complete() {
        this.completed = true;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public PathPoint getCurrentPoint() {
        return this.currentPoint;
    }
}
