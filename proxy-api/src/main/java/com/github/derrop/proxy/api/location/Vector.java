/*
 * This class has been taken from the Bukkit API
 */
package com.github.derrop.proxy.api.location;

import com.github.derrop.proxy.api.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Represents a mutable vector. Because the components of Vectors are mutable,
 * storing Vectors long term may be dangerous if passing code modifies the
 * Vector later. If you want to keep around a Vector, it may be wise to call
 * <code>clone()</code> in order to get a copy.
 */
public class Vector implements Cloneable, Serializable {
    private static final long serialVersionUID = -2657651106777219169L;

    private static final double EPSILON = 0.000001;

    protected double x;
    protected double y;
    protected double z;

    public Vector() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @NotNull
    public Vector add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    @NotNull
    public Vector add(@NotNull Vector vec) {
        return this.add(vec.x, vec.y, vec.z);
    }

    @NotNull
    public Vector subtract(@NotNull Vector vec) {
        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
        return this;
    }

    @NotNull
    public Vector multiply(@NotNull Vector vec) {
        x *= vec.x;
        y *= vec.y;
        z *= vec.z;
        return this;
    }

    @NotNull
    public Vector divide(@NotNull Vector vec) {
        x /= vec.x;
        y /= vec.y;
        z /= vec.z;
        return this;
    }

    @NotNull
    public Vector copy(@NotNull Vector vec) {
        x = vec.x;
        y = vec.y;
        z = vec.z;
        return this;
    }

    public double length() {
        return Math.sqrt(MathHelper.square(x) + MathHelper.square(y) + MathHelper.square(z));
    }

    public double lengthSquared() {
        return MathHelper.square(x) + MathHelper.square(y) + MathHelper.square(z);
    }

    public double distance(@NotNull Vector o) {
        return Math.sqrt(MathHelper.square(x - o.x) + MathHelper.square(y - o.y) + MathHelper.square(z - o.z));
    }

    public double distanceSquared(@NotNull Vector o) {
        return MathHelper.square(x - o.x) + MathHelper.square(y - o.y) + MathHelper.square(z - o.z);
    }

    public float angle(@NotNull Vector other) {
        double dot = MathHelper.clamp(dot(other) / (length() * other.length()), -1, 1);
        return (float) Math.acos(dot);
    }

    @NotNull
    public Vector midpoint(@NotNull Vector other) {
        x = (x + other.x) / 2;
        y = (y + other.y) / 2;
        z = (z + other.z) / 2;
        return this;
    }

    @NotNull
    public Vector getMidpoint(@NotNull Vector other) {
        double x = (this.x + other.x) / 2;
        double y = (this.y + other.y) / 2;
        double z = (this.z + other.z) / 2;
        return new Vector(x, y, z);
    }

    @NotNull
    public Vector multiply(int m) {
        x *= m;
        y *= m;
        z *= m;
        return this;
    }

    @NotNull
    public Vector multiply(double m) {
        x *= m;
        y *= m;
        z *= m;
        return this;
    }

    @NotNull
    public Vector multiply(float m) {
        x *= m;
        y *= m;
        z *= m;
        return this;
    }

    public double dot(@NotNull Vector other) {
        return x * other.x + y * other.y + z * other.z;
    }

    @NotNull
    public Vector crossProduct(@NotNull Vector o) {
        double newX = y * o.z - o.y * z;
        double newY = z * o.x - o.z * x;
        double newZ = x * o.y - o.x * y;

        x = newX;
        y = newY;
        z = newZ;
        return this;
    }

    @NotNull
    public Vector getCrossProduct(@NotNull Vector o) {
        double x = this.y * o.z - o.y * this.z;
        double y = this.z * o.x - o.z * this.x;
        double z = this.x * o.y - o.x * this.y;
        return new Vector(x, y, z);
    }

    @NotNull
    public Vector normalize() {
        double length = length();

        x /= length;
        y /= length;
        z /= length;

        return this;
    }

    public boolean isInAABB(@NotNull Vector min, @NotNull Vector max) {
        return x >= min.x && x <= max.x && y >= min.y && y <= max.y && z >= min.z && z <= max.z;
    }

    public boolean isInSphere(@NotNull Vector origin, double radius) {
        return (MathHelper.square(origin.x - x) + MathHelper.square(origin.y - y) + MathHelper.square(origin.z - z)) <= MathHelper.square(radius);
    }

    public boolean isNormalized() {
        return Math.abs(this.lengthSquared() - 1) < getEpsilon();
    }

    @NotNull
    public Vector rotateAroundX(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double y = angleCos * getY() - angleSin * getZ();
        double z = angleSin * getY() + angleCos * getZ();
        return setY(y).setZ(z);
    }

    @NotNull
    public Vector rotateAroundY(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double x = angleCos * getX() + angleSin * getZ();
        double z = -angleSin * getX() + angleCos * getZ();
        return setX(x).setZ(z);
    }

    @NotNull
    public Vector rotateAroundZ(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double x = angleCos * getX() - angleSin * getY();
        double y = angleSin * getX() + angleCos * getY();
        return setX(x).setY(y);
    }

    @NotNull
    public Vector rotateAroundAxis(@NotNull Vector axis, double angle) throws IllegalArgumentException {
        return rotateAroundNonUnitAxis(axis.isNormalized() ? axis : axis.clone().normalize(), angle);
    }

    @NotNull
    public Vector rotateAroundNonUnitAxis(@NotNull Vector axis, double angle) throws IllegalArgumentException {
        double x = getX(), y = getY(), z = getZ();
        double x2 = axis.getX(), y2 = axis.getY(), z2 = axis.getZ();

        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);
        double dotProduct = this.dot(axis);

        double xPrime = x2 * dotProduct * (1d - cosTheta) + x * cosTheta + (-z2 * y + y2 * z) * sinTheta;
        double yPrime = y2 * dotProduct * (1d - cosTheta) + y * cosTheta + (z2 * x - x2 * z) * sinTheta;
        double zPrime = z2 * dotProduct * (1d - cosTheta) + z * cosTheta + (-y2 * x + x2 * y) * sinTheta;

        return setX(xPrime).setY(yPrime).setZ(zPrime);
    }

    public double getX() {
        return x;
    }

    public int getBlockX() {
        return MathHelper.floor(x);
    }

    public double getY() {
        return y;
    }

    public int getBlockY() {
        return MathHelper.floor(y);
    }

    public double getZ() {
        return z;
    }

    public int getBlockZ() {
        return MathHelper.floor(z);
    }

    @NotNull
    public Vector setX(int x) {
        this.x = x;
        return this;
    }

    @NotNull
    public Vector setX(double x) {
        this.x = x;
        return this;
    }

    @NotNull
    public Vector setX(float x) {
        this.x = x;
        return this;
    }

    @NotNull
    public Vector setY(int y) {
        this.y = y;
        return this;
    }

    @NotNull
    public Vector setY(double y) {
        this.y = y;
        return this;
    }

    @NotNull
    public Vector setY(float y) {
        this.y = y;
        return this;
    }

    @NotNull
    public Vector setZ(int z) {
        this.z = z;
        return this;
    }

    @NotNull
    public Vector setZ(double z) {
        this.z = z;
        return this;
    }

    @NotNull
    public Vector setZ(float z) {
        this.z = z;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector)) {
            return false;
        }

        Vector other = (Vector) obj;
        return Math.abs(x - other.x) < EPSILON && Math.abs(y - other.y) < EPSILON && Math.abs(z - other.z) < EPSILON && (this.getClass().equals(obj.getClass()));
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 79 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        return hash;
    }

    @NotNull
    @Override
    public Vector clone() {
        try {
            return (Vector) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Vector(this.x, this.y, this.z); // pail
        }
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }

    @NotNull
    public Location toLocation(float yaw, float pitch) {
        return new Location(x, y, z, yaw, pitch);
    }

    public static double getEpsilon() {
        return EPSILON;
    }

}
