package com.github.derrop.proxy.plugins.pathfinding.walk;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.location.Location;

public class DirectWalkablePath extends WalkablePath {

    private static final double MAX_DISTANCE = 0.4;
    private static final double MAX_DISTANCE_SQUARED = MAX_DISTANCE * MAX_DISTANCE;

    private final Location targetLocation;

    public DirectWalkablePath(ServiceConnection connection, Location targetLocation, Runnable finishHandler) {
        super(connection, finishHandler);
        this.targetLocation = targetLocation;
    }

    private Location getCurrentLocation() {
        return this.getConnection().getLocation();
    }

    private double getXDistance() {
        return this.getConnection().getLocation().getX() - this.targetLocation.getX();
    }

    private double getYDistance() {
        return this.getConnection().getLocation().getY() - this.targetLocation.getY();
    }

    private double getZDistance() {
        return this.getConnection().getLocation().getZ() - this.targetLocation.getZ();
    }

    private double getWalkDistance() {
        return Math.max(MAX_DISTANCE - 0.1, DefaultPathWalker.BPT);
    }

    @Override
    public boolean isRecursive() {
        return false;
    }

    @Override
    public boolean isDone() {
        return this.getCurrentLocation().distanceSquared(this.targetLocation) < MAX_DISTANCE_SQUARED;
    }

    @Override
    public void handleTick() {
        double x = this.getXDistance();
        double y = this.getYDistance();
        double z = this.getZDistance();

        Location location = this.getCurrentLocation().clone();

        if (Math.abs(x) > MAX_DISTANCE) {
            location.setX(location.getX() + (this.getWalkDistance() * (x > 0 ? -1 : 1)));
        }
        if (Math.abs(y) > MAX_DISTANCE) {
            location.setY(location.getY() + (this.getWalkDistance() * (y > 0 ? -1 : 1)));
        }
        if (Math.abs(z) > MAX_DISTANCE) {
            location.setZ(location.getZ() + (this.getWalkDistance() * (z > 0 ? -1 : 1)));
        }

        this.getConnection().interactive().teleport(location);
    }
}
