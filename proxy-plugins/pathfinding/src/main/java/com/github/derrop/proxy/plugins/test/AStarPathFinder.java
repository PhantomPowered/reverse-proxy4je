package com.github.derrop.proxy.plugins.test;

import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.util.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AStarPathFinder {
    // TODO This is not perfect on diagonal paths
    // TODO players can jump over wholes, implement this
    // TODO Just MAYBE this could build bridges if the player has blocks in their inventory?

    private Queue<PathPoint> points = new ConcurrentLinkedQueue<>();

    public Queue<PathPoint> getPoints() {
        return this.points;
    }

    // This can take pretty long with paths that aren't straight forward
    public boolean findPath(BlockAccess access, BlockPos start, BlockPos end) {

        PathPoint startPoint = new PathPoint(0, 0, 0);
        PathPoint endPoint = new PathPoint(end.getX() - start.getX(), end.getY() - start.getY(), end.getZ() - start.getZ());

        int jumpHeight = 1;
        int fallHeight = 4; // TODO calculate this with the current health of the player?
        int entityHeight = 2; // TODO do not ignore this, players are 2 blocks high
        int entityWidth = 1; // TODO do not ignore this, players are 1 block in width


        double maxDistanceSquared = start.distanceSq(end) + 100000;

        this.loadNeighbors(startPoint);


        Collection<PathPoint> visitedPoints = new ArrayList<>();
        Queue<PathPoint> frontier = new ConcurrentLinkedQueue<>(Arrays.asList(startPoint.getNeighbors()));

        while (!frontier.isEmpty()) {
            PathPoint point = frontier.poll();

            if (visitedPoints.contains(point)) {
                continue;
            }
            visitedPoints.add(point);

            BlockPos absolutePoint = new BlockPos(start.getX() + point.getX(), start.getY() + point.getY(), start.getZ() + point.getZ());

            if (absolutePoint.distanceSq(end) > maxDistanceSquared) {
                continue;
            }

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

            if (access.getMaterial(absolutePoint.up(jumpHeight + 1)).isSolid()) {
                continue;
            }

            if (access.getMaterial(absolutePoint.up(jumpHeight)).isSolid()) {
                PathPoint next = new PathPoint(point.getX(), point.getY() + jumpHeight, point.getZ(), point);
                this.loadNeighbors(next);
                frontier.add(next);
                continue;
            }

            if (point.equals(endPoint)) {
                System.out.println("FOUND " + endPoint);
                PathPoint previous = point;
                previous.setY(previous.getY() + 1.3);
                do {
                    this.points.add(previous);
                    System.out.println("Point: " + previous);
                    BlockPos pos = new BlockPos(start.getX() + previous.getX(), start.getY() + previous.getY(), start.getZ() + previous.getZ());
                    System.out.println("-> " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
                    access.setMaterial(pos, Material.EMERALD_BLOCK); // TODO remove this debug
                } while ((previous = previous.getPreviousPoint()) != startPoint);
                System.out.println("Success: " + this.points);
                return true;
            }

            this.loadNeighbors(point);
            frontier.addAll(Arrays.asList(point.getNeighbors()));
        }

        return false;
    }

    private void loadNeighbors(PathPoint point) {
        // TODO the priority of the straight paths should be higher than the priority of the diagonal paths
        point.getNeighbors()[0] = new PathPoint(point.getX() + 1, point.getY() + 0, point.getZ() + 0, point);
        point.getNeighbors()[1] = new PathPoint(point.getX() + 0, point.getY() + 1, point.getZ() + 0, point);
        point.getNeighbors()[2] = new PathPoint(point.getX() + 0, point.getY() + 0, point.getZ() + 1, point);
        point.getNeighbors()[3] = new PathPoint(point.getX() + -1, point.getY() + 0, point.getZ() + 0, point);
        point.getNeighbors()[4] = new PathPoint(point.getX() + 0, point.getY() + -1, point.getZ() + 0, point);
        point.getNeighbors()[5] = new PathPoint(point.getX() + 0, point.getY() + 0, point.getZ() + -1, point);

        point.getNeighbors()[6] = new PathPoint(point.getX() + 1, point.getY() + -1, point.getZ() + 1, point);
        point.getNeighbors()[7] = new PathPoint(point.getX() + -1, point.getY() + -1, point.getZ() + -1, point);
        point.getNeighbors()[8] = new PathPoint(point.getX() + 1, point.getY() + -1, point.getZ() + -1, point);
        point.getNeighbors()[9] = new PathPoint(point.getX() + -1, point.getY() + -1, point.getZ() + 1, point);
        point.getNeighbors()[10] = new PathPoint(point.getX() + 1, point.getY() + 1, point.getZ() + 1, point);
        point.getNeighbors()[11] = new PathPoint(point.getX() + -1, point.getY() + 1, point.getZ() + -1, point);
        point.getNeighbors()[12] = new PathPoint(point.getX() + 1, point.getY() + 1, point.getZ() + -1, point);
        point.getNeighbors()[13] = new PathPoint(point.getX() + -1, point.getY() + 1, point.getZ() + 1, point);
    }

}
