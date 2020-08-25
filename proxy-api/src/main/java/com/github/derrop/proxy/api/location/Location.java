/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.api.location;

import com.github.derrop.proxy.api.block.Facing;
import com.github.derrop.proxy.api.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Location {

    public static final Location ZERO = new Location(0, 0, 0, 0, 0);

    private static final int NUM_X_BITS = 1 + MathHelper.calculateLogBaseTwo(MathHelper.roundUpToPowerOfTwo(30000000));
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
    private static final int Y_SHIFT = NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

    public Location(double x, double y, double z) {
        this(x, y, z, 0, 0);
    }

    public Location(double x, double y, double z, float yaw, float pitch) {
        this(x, y, z, yaw, pitch, false);
    }

    public Location(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean onGround;

    public int getBlockX() {
        return MathHelper.floor(this.getX());
    }

    public int getBlockY() {
        return MathHelper.floor(this.getY());
    }

    public int getBlockZ() {
        return MathHelper.floor(this.getZ());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public Vector toVector() {
        return new Vector(this.getX(), this.getY(), this.getZ());
    }

    public long toLong() {
        return ((long) this.getBlockX() & X_MASK) << X_SHIFT | ((long) this.getBlockY() & Y_MASK) << Y_SHIFT | ((long) this.getBlockZ() & Z_MASK);
    }

    public static Location fromLong(long serialized) {
        int x = (int) (serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
        int y = (int) (serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
        int z = (int) (serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
        return new Location(x, y, z);
    }

    public Location up() {
        return this.up(1);
    }

    public Location up(int n) {
        return this.offset(Facing.UP, n);
    }

    public Location down() {
        return this.down(1);
    }

    public Location down(int n) {
        return this.offset(Facing.DOWN, n);
    }

    public Location north() {
        return this.north(1);
    }

    public Location north(int n) {
        return this.offset(Facing.NORTH, n);
    }

    public Location south() {
        return this.south(1);
    }

    public Location south(int n) {
        return this.offset(Facing.SOUTH, n);
    }

    public Location west() {
        return this.west(1);
    }

    public Location west(int n) {
        return this.offset(Facing.WEST, n);
    }

    public Location east() {
        return this.east(1);
    }

    public Location east(int n) {
        return this.offset(Facing.EAST, n);
    }

    public Location offset(Facing facing) {
        return this.offset(facing, 1);
    }

    public Location offset(Facing facing, int n) {
        return n == 0 ? this : new Location(this.getX() + facing.getFrontOffsetX() * n, this.getY() + facing.getFrontOffsetY() * n, this.getZ() + facing.getFrontOffsetZ() * n);
    }

    public boolean isInChunk(int x, int z) {
        return this.getBlockX() >> 4 == x && this.getBlockZ() >> 4 == z;
    }

    @NotNull
    public Location subtract(@NotNull Location location) {
        return this.subtract(location.getX(), location.getY(), location.getZ());
    }

    public Location subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;

        return this;
    }

    @NotNull
    public Location add(@NotNull Location location) {
        return this.add(location.getX(), location.getY(), location.getZ());
    }

    public Location add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;

        return this;
    }

    @NotNull
    public Location subtract(@NotNull Vector location) {
        this.x -= location.getX();
        this.y -= location.getY();
        this.z -= location.getZ();

        return this;
    }

    @NotNull
    public Location add(@NotNull Vector location) {
        this.x += location.getX();
        this.y += location.getY();
        this.z += location.getZ();

        return this;
    }

    public double distanceSquared(@NotNull Location other) {
        double d1 = x - other.x;
        double d2 = y - other.y;
        double d3 = z - other.z;

        return (d1 * d1) + (d2 * d2) + (d3 * d3);
    }

    @NotNull
    public Vector getDirection() {
        Vector vector = new Vector();

        double rotX = this.getYaw();
        double rotY = this.getPitch();

        vector.setY(-Math.sin(Math.toRadians(rotY)));

        double xz = Math.cos(Math.toRadians(rotY));

        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));

        return vector;
    }

    @NotNull
    public Location setDirection(@NotNull Vector vector) {
        final double _2PI = 2 * Math.PI;
        final double x = vector.getX();
        final double z = vector.getZ();

        if (x == 0 && z == 0) {
            pitch = vector.getY() > 0 ? -90 : 90;
            return this;
        }

        double theta = Math.atan2(-x, z);
        yaw = (float) Math.toDegrees((theta + _2PI) % _2PI);

        double x2 = MathHelper.square(x);
        double z2 = MathHelper.square(z);
        double xz = Math.sqrt(x2 + z2);
        pitch = (float) Math.toDegrees(Math.atan(-vector.getY() / xz));

        return this;
    }

    public double distance(@NotNull Location other) {
        return Math.sqrt(this.distanceSquared(other));
    }

    @Override
    public String toString() {
        return "Location{"
                + "x=" + x
                + ", y=" + y
                + ", z=" + z
                + ", yaw=" + yaw
                + ", pitch=" + pitch
                + ", onGround=" + onGround
                + '}';
    }

    public String toShortString() {
        return this.getBlockX() + ", " + this.getBlockY() + ", " + this.getBlockZ();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Location location = (Location) o;
        return Double.compare(location.x, x) == 0
                && Double.compare(location.y, y) == 0
                && Double.compare(location.z, z) == 0
                && Float.compare(location.yaw, yaw) == 0
                && Float.compare(location.pitch, pitch) == 0
                && onGround == location.onGround;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, yaw, pitch, onGround);
    }

    @Override
    @NotNull
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (final CloneNotSupportedException ex) {
            return new Location(this.x, this.y, this.z, this.yaw, this.pitch);
        }
    }
}
