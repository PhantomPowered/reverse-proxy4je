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

import org.jetbrains.annotations.NotNull;

public class Location {

    public Location(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

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

    @NotNull
    public Location substract(@NotNull Location location) {
        this.x -= location.x;
        this.y -= location.y;
        this.z -= location.z;

        return this;
    }

    @NotNull
    public Location add(@NotNull Location location) {
        this.x += location.x;
        this.y += location.y;
        this.z += location.z;

        return this;
    }

    public double distanceSquared(@NotNull Location other) {
        double d1 = x - other.x;
        double d2 = y - other.y;
        double d3 = z - other.z;

        return (d1 * d1) + (d2 * d2) + (d3 * d3);
    }

    public double distance(@NotNull Location other) {
        return Math.sqrt(this.distanceSquared(other));
    }

    @NotNull
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (final CloneNotSupportedException ex) {
            return new Location(this.x, this.y, this.z, this.yaw, this.pitch);
        }
    }
}
