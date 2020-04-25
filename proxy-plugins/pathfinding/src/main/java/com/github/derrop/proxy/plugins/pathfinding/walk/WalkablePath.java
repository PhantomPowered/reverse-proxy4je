package com.github.derrop.proxy.plugins.pathfinding.walk;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.plugins.pathfinding.Path;
import com.github.derrop.proxy.plugins.pathfinding.PathPoint;

import java.util.Queue;

public class WalkablePath {

    private final ServiceConnection connection;
    private final Path path;
    private final Runnable finishHandler;

    private PathPoint previousPoint;
    private PathPoint currentPoint;
    private Queue<PathPoint> currentWay;

    public WalkablePath(ServiceConnection connection, Path path, Runnable finishHandler) {
        this.connection = connection;
        this.path = path;
        this.finishHandler = finishHandler;
    }

    public Runnable getFinishHandler() {
        return finishHandler;
    }

    public ServiceConnection getConnection() {
        return connection;
    }

    public Path getPath() {
        return path;
    }

    public PathPoint getNextPoint() {
        return this.path.getNextPoint();
    }

    public PathPoint getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(PathPoint currentPoint) {
        this.currentPoint = currentPoint;
    }

    public PathPoint getPreviousPoint() {
        return previousPoint;
    }

    public void setPreviousPoint(PathPoint previousPoint) {
        this.previousPoint = previousPoint;
    }

    public Queue<PathPoint> getCurrentWay() {
        return currentWay;
    }

    public void setCurrentWay(Queue<PathPoint> currentWay) {
        this.currentWay = currentWay;
    }
}
