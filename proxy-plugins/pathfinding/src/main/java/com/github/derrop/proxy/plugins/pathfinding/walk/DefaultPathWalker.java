package com.github.derrop.proxy.plugins.pathfinding.walk;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.plugins.pathfinding.Path;
import com.github.derrop.proxy.plugins.pathfinding.PathPoint;

import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultPathWalker implements PathWalker {

    public static final double TPS = 20; // ticks per second
    public static final double BPT = 0.215; // blocks per tick

    private Map<UUID, WalkablePath> runningPaths = new ConcurrentHashMap<>();

    {
        new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep((long) (1000D / TPS));
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                this.doTick();
            }
        }).start();
    }

    public void doTick() {
        for (Map.Entry<UUID, WalkablePath> entry : this.runningPaths.entrySet()) {
            WalkablePath path = entry.getValue();
            if (!path.getPath().hasPointsLeft()) {
                path.getFinishHandler().run();

                if (!path.getPath().isRecursive()) {
                    runningPaths.remove(entry.getKey());
                    continue;
                }
            }

            PathPoint point = path.getCurrentWay() == null ? null : path.getCurrentWay().poll();

            if (point == null) {
                PathPoint previousPoint = path.getCurrentPoint();
                PathPoint currentPoint = path.getNextPoint();
                path.setPreviousPoint(previousPoint);
                path.setCurrentPoint(currentPoint);
                path.setCurrentWay(this.smoothWay(previousPoint, currentPoint));
                continue;
            }

            Location location = path.getPath().getAbsoluteLocation(point);
            PathPoint nextPoint = path.getCurrentPoint();
            if (nextPoint != null) {
                location.setDirection(path.getPath().getAbsoluteLocation(nextPoint).subtract(location).toVector());
            } else {
                location.setDirection(path.getConnection().getLocation().toVector());
            }
            /*if (path.getConnection().getLocation().distanceSquared(location) > 25) { TODO
                runningPaths.remove(entry.getKey());
                System.err.println("FAILED");
                continue;
            }*/
            path.getConnection().unsafe().setLocationUnchecked(location);
        }
    }

    private Queue<PathPoint> smoothWay(PathPoint previousPoint, PathPoint currentPoint) {
        if (currentPoint == null) {
            return null;
        }
        if (previousPoint == null) {
            return new LinkedBlockingQueue<>(Collections.singletonList(currentPoint));
        }

        double deltaX = (currentPoint.getX() - previousPoint.getX());
        double deltaY = (currentPoint.getY() - previousPoint.getY());
        double deltaZ = (currentPoint.getZ() - previousPoint.getZ());

        if (deltaY > 0.6 && (deltaX > 0.5 || deltaZ > 0.5)) {
            PathPoint midPoint = new PathPoint(previousPoint.getX(), currentPoint.getY(), previousPoint.getZ());
            Queue<PathPoint> result = new LinkedBlockingQueue<>();
            result.addAll(this.smoothWay(previousPoint, midPoint));
            result.addAll(this.smoothWay(midPoint, currentPoint));
            return result;
        }

        return this.smoothWay0(previousPoint, currentPoint);
    }

    private Queue<PathPoint> smoothWay0(PathPoint previousPoint, PathPoint currentPoint) {
        // TODO you can fall off corners easily
        double pointCount = BPT;

        double deltaX = Math.abs(currentPoint.getX() - previousPoint.getX());
        double deltaY = Math.abs(currentPoint.getY() - previousPoint.getY());
        double deltaZ = Math.abs(currentPoint.getZ() - previousPoint.getZ());

        double stepX = pointCount;// / deltaX;
        double stepY = pointCount;// / deltaY;
        double stepZ = pointCount;// / deltaZ;

        Queue<PathPoint> result = new LinkedBlockingQueue<>();

        double x = 0;
        double y = 0;
        double z = 0;
        while (x < deltaX || y < deltaY || z < deltaZ) {
            if (x < deltaX) {
                x += stepX;
            }
            if (y < deltaY) {
                y += stepY;
            }
            if (z < deltaZ) {
                z += stepZ;
            }

            result.offer(new PathPoint(previousPoint.getX() + x, previousPoint.getY() + y, previousPoint.getZ() + z));
        }

        return result;
    }

    @Override
    public UUID walkPath(ServiceConnection connection, Path path, Runnable roundFinishedHandler) {
        UUID id = UUID.randomUUID();
        this.runningPaths.put(id, new WalkablePath(connection, path, roundFinishedHandler));
        return id;
    }

    @Override
    public void cancelPath(UUID id) {
        this.runningPaths.remove(id);
    }

    @Override
    public boolean isWalking(UUID id) {
        return this.runningPaths.containsKey(id);
    }
}
