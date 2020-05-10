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
package com.github.derrop.proxy.plugins.pathfinding.finder.astar;

import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.plugins.pathfinding.PathPoint;
import com.github.derrop.proxy.plugins.pathfinding.finder.PathFindInteraction;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AStarPathFinder {
    // TODO This is not perfect on diagonal paths
    // TODO players can jump over wholes, implement this
    // TODO Just MAYBE this could build bridges if the player has blocks in their inventory?

    private static final Queue<PathPoint> EMPTY_QUEUE = new ConcurrentLinkedQueue<>();

    // This can take pretty long with paths that aren't straight forward
    public Queue<PathPoint> findPath(PathFindInteraction interaction, BlockAccess access, boolean canFly, BlockPos start, BlockPos end) {
        PathPoint startPoint = new PathPoint(0, 0, 0);
        PathPoint endPoint = new PathPoint(end.getX() - start.getX(), end.getY() - start.getY(), end.getZ() - start.getZ());

        int jumpHeight = 1;
        int fallHeight = 4; // TODO calculate this with the current health of the player?
        int entityHeight = 2; // TODO do not ignore this, players are 2 blocks high
        int entityWidth = 1; // TODO do not ignore this, players are 1 block in width


        double maxDistanceSquared = start.distanceSq(end) + 100000;

        this.loadNeighbors(startPoint);


        Collection<PathPoint> visitedPoints = new ArrayList<>();
        Queue<PathPoint> frontier = new ConcurrentLinkedQueue<>(Arrays.asList(this.loadNeighbors(startPoint)));

        // TODO this goes through fences when placed diagonal

        while (!frontier.isEmpty()) {
            if (interaction.isCancelled()) {
                return EMPTY_QUEUE;
            }

            PathPoint point = frontier.poll();

            if (point == null || visitedPoints.contains(point)) {
                continue;
            }
            visitedPoints.add(point);

            interaction.setCurrentPoint(point);

            BlockPos absolutePoint = new BlockPos(start.getX() + point.getX(), start.getY() + point.getY(), start.getZ() + point.getZ());

            if (absolutePoint.distanceSq(end) > maxDistanceSquared) {
                continue;
            }

            if (!canFly) {
                int solidDown = -1;

                for (int i = 0; i < fallHeight; i++) {
                    if (access.getMaterial(absolutePoint.down(i)).isSolid()) {
                        solidDown = i;
                        break;
                    }
                }
                if (solidDown == -1) {
                    continue;
                }
                if (solidDown != 0) {
                    PathPoint next = new PathPoint(point.getX(), point.getY() - solidDown, point.getZ(), point);
                    this.loadNeighbors(next);
                    frontier.add(next);
                    continue;
                }
            } else {
                PathPoint pointDown = new PathPoint(point.getX(), point.getY() - 1, point.getZ());
                if (pointDown.equals(point.getPreviousPoint())) {
                    continue;
                }
            }

            if (access.getMaterial(absolutePoint.up(jumpHeight + 1)).isSolid()) {
                continue;
            }

            if (access.getMaterial(absolutePoint.up(jumpHeight)).isSolid()) {
                PathPoint next = new PathPoint(point.getX(), point.getY() + jumpHeight, point.getZ(), point);
                this.loadNeighbors(next);
                frontier.add(next);
                continue;
            }

            if (this.isCompleted(point, endPoint)) {
                PathPoint previous = point;
                previous.setY(previous.getY() + 1.3);

                Queue<PathPoint> points = new ConcurrentLinkedQueue<>();

                do {
                    points.add(previous);
                } while ((previous = previous.getPreviousPoint()) != startPoint);
                System.out.println("Success: " + points);

                return this.reverse(points);
            }

            frontier.addAll(Arrays.asList(this.loadNeighbors(point)));
        }

        return EMPTY_QUEUE;
    }

    private boolean isCompleted(PathPoint current, PathPoint end) {
        if (current.equals(end)) {
            return true;
        }
        if (current.getX() == end.getX() && current.getZ() == end.getZ()) {
            return current.getY() - 1 == end.getY() || current.getY() + 1 == end.getY();
        }
        double dx = Math.abs(current.getX() - end.getX());
        double dy = Math.abs(current.getY() - end.getY());
        double dz = Math.abs(current.getZ() - end.getZ());
        return dx <= 1 && dy <= 1 && dz <= 1;
    }

    private Queue<PathPoint> reverse(Queue<PathPoint> points) {
        PathPoint[] array = points.toArray(new PathPoint[0]);
        Queue<PathPoint> result = new ConcurrentLinkedQueue<>();

        for (int i = array.length - 1; i >= 0; i--) {
            result.add(array[i]);
        }

        return result;
    }

    private PathPoint[] loadNeighbors(PathPoint point) {
        // TODO the priority of the straight paths should be higher than the priority of the diagonal paths
        PathPoint[] neighbors = new PathPoint[18];

        neighbors[0] = new PathPoint(point.getX() + 1, point.getY() + 0, point.getZ() + 0, point);
        neighbors[1] = new PathPoint(point.getX() + 0, point.getY() + 1, point.getZ() + 0, point);
        neighbors[2] = new PathPoint(point.getX() + 0, point.getY() + 0, point.getZ() + 1, point);
        neighbors[3] = new PathPoint(point.getX() + -1, point.getY() + 0, point.getZ() + 0, point);
        neighbors[4] = new PathPoint(point.getX() + 0, point.getY() + -1, point.getZ() + 0, point);
        neighbors[5] = new PathPoint(point.getX() + 0, point.getY() + 0, point.getZ() + -1, point);

        neighbors[6] = new PathPoint(point.getX() + 1, point.getY() + 0, point.getZ() + -1, point);
        neighbors[7] = new PathPoint(point.getX() + -1, point.getY() + 0, point.getZ() + 1, point);
        neighbors[8] = new PathPoint(point.getX() + -1, point.getY() + 0, point.getZ() + -1, point);
        neighbors[9] = new PathPoint(point.getX() + 1, point.getY() + 0, point.getZ() + 1, point);

        neighbors[10] = new PathPoint(point.getX() + 1, point.getY() + -1, point.getZ() + 1, point);
        neighbors[11] = new PathPoint(point.getX() + -1, point.getY() + -1, point.getZ() + -1, point);
        neighbors[12] = new PathPoint(point.getX() + 1, point.getY() + -1, point.getZ() + -1, point);
        neighbors[13] = new PathPoint(point.getX() + -1, point.getY() + -1, point.getZ() + 1, point);
        neighbors[14] = new PathPoint(point.getX() + 1, point.getY() + 1, point.getZ() + 1, point);
        neighbors[15] = new PathPoint(point.getX() + -1, point.getY() + 1, point.getZ() + -1, point);
        neighbors[16] = new PathPoint(point.getX() + 1, point.getY() + 1, point.getZ() + -1, point);
        neighbors[17] = new PathPoint(point.getX() + -1, point.getY() + 1, point.getZ() + 1, point);

        return neighbors;
    }

}
