package com.github.derrop.proxy.plugins.pathfinding;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Path {

    private Queue<PathPoint> points;
    private PathPoint[] allPoints;
    private boolean started;
    private boolean recursive;
    private boolean success;

    public Path(Queue<PathPoint> points) {
        this.points = points;
        this.success = !points.isEmpty();
    }

    public boolean isSuccess() {
        return this.success;
    }

    public Path setRecursive() {
        if (!this.success) {
            return this;
        }
        if (this.started) {
            throw new IllegalStateException("Cannot change recursive state when path is already started");
        }

        this.allPoints = this.points.toArray(new PathPoint[0]);
        this.recursive = true;

        return this;
    }

    public PathPoint getNextPoint() {
        if (!this.success) {
            return null;
        }

        if (this.points.isEmpty()) {
            if (!this.recursive) {
                return null;
            }

            this.points = new ConcurrentLinkedQueue<>(Arrays.asList(this.allPoints));
        }

        return this.points.poll();
    }

    @Override
    public String toString() {
        return "Path{" +
                "points=" + points +
                ", allPoints=" + Arrays.toString(allPoints) +
                ", started=" + started +
                ", recursive=" + recursive +
                ", success=" + success +
                '}';
    }
}
