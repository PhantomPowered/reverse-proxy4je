package com.github.derrop.proxy.plugins.test;

import java.util.Objects;

public class PathPoint {

    private double x, y, z;
    private PathPoint previousPoint;

    public PathPoint(double x, double y, double z) {
        this(x, y, z, null);
    }

    public PathPoint(double x, double y, double z, PathPoint previousPoint) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.previousPoint = previousPoint;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setPreviousPoint(PathPoint previousPoint) {
        this.previousPoint = previousPoint;
    }

    public PathPoint getPreviousPoint() {
        return previousPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathPoint point = (PathPoint) o;
        return Double.compare(point.x, x) == 0 &&
                Double.compare(point.y, y) == 0 &&
                Double.compare(point.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "PathPoint{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
