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
package com.github.derrop.proxy.plugins.pathfinding;

import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.location.Location;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Path {

    private BlockPos beginPos;
    private Queue<PathPoint> points;
    private PathPoint[] allPoints;
    private boolean started;
    private boolean recursive;
    private boolean success;

    public Path(BlockPos beginPos, Queue<PathPoint> points) {
        this.beginPos = beginPos;
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

    public boolean hasPointsLeft() {
        return !this.points.isEmpty();
    }

    public boolean isRecursive() {
        return recursive;
    }

    public Location getAbsoluteLocation(PathPoint point) {
        return new Location(
                this.beginPos.getX() + point.getX(),
                this.beginPos.getY() + point.getY() + 1,
                this.beginPos.getZ() + point.getZ(),
                0,
                0
        );
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
