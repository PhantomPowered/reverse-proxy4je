/*
 * This class has been taken from the Bukkit API
 */
package com.github.derrop.proxy.api.raytrace;

import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.block.Facing;
import com.github.derrop.proxy.api.entity.types.Entity;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.location.Vector;
import com.github.derrop.proxy.api.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class performs ray tracing and iterates along blocks on a line.
 */
public class BlockIterator implements Iterator<Location> {

    private final BlockAccess blockAccess;
    private final int maxDistance;

    private static final int gridSize = 1 << 24;

    private boolean end = false;

    private final Location[] blockQueue = new Location[3];
    private int currentBlock;
    private int currentDistance;
    private final int maxDistanceInt;

    private int secondError;
    private int thirdError;

    private final int secondStep;
    private final int thirdStep;

    private Facing mainFace;
    private Facing secondFace;
    private Facing thirdFace;

    /**
     * Constructs the BlockIterator.
     * This considers all blocks as 1x1x1 in size.
     *
     * @param blockAccess The block access to use for tracing
     * @param start A Vector giving the initial location for the trace
     * @param direction A Vector pointing in the direction for the trace
     * @param yOffset The trace begins vertically offset from the start vector
     *     by this value
     * @param maxDistance This is the maximum distance in blocks for the
     *     trace. Setting this value above 140 may lead to problems with
     *     unloaded chunks. A value of 0 indicates no limit
     *
     */
    public BlockIterator(@NotNull BlockAccess blockAccess, @NotNull Vector start, @NotNull Vector direction, double yOffset, int maxDistance) {
        this.blockAccess = blockAccess;
        this.maxDistance = maxDistance;

        Vector startClone = start.clone();

        startClone.setY(startClone.getY() + yOffset);

        currentDistance = 0;

        double mainDirection = 0;
        double secondDirection = 0;
        double thirdDirection = 0;

        double mainPosition = 0;
        double secondPosition = 0;
        double thirdPosition = 0;

        Location startBlock = new Location(startClone.getX(), startClone.getY(), startClone.getZ());

        if (getXLength(direction) > mainDirection) {
            mainFace = getXFace(direction);
            mainDirection = getXLength(direction);
            mainPosition = getXPosition(direction, startClone, startBlock);

            secondFace = getYFace(direction);
            secondDirection = getYLength(direction);
            secondPosition = getYPosition(direction, startClone, startBlock);

            thirdFace = getZFace(direction);
            thirdDirection = getZLength(direction);
            thirdPosition = getZPosition(direction, startClone, startBlock);
        }
        if (getYLength(direction) > mainDirection) {
            mainFace = getYFace(direction);
            mainDirection = getYLength(direction);
            mainPosition = getYPosition(direction, startClone, startBlock);

            secondFace = getZFace(direction);
            secondDirection = getZLength(direction);
            secondPosition = getZPosition(direction, startClone, startBlock);

            thirdFace = getXFace(direction);
            thirdDirection = getXLength(direction);
            thirdPosition = getXPosition(direction, startClone, startBlock);
        }
        if (getZLength(direction) > mainDirection) {
            mainFace = getZFace(direction);
            mainDirection = getZLength(direction);
            mainPosition = getZPosition(direction, startClone, startBlock);

            secondFace = getXFace(direction);
            secondDirection = getXLength(direction);
            secondPosition = getXPosition(direction, startClone, startBlock);

            thirdFace = getYFace(direction);
            thirdDirection = getYLength(direction);
            thirdPosition = getYPosition(direction, startClone, startBlock);
        }

        // trace line backwards to find intercept with plane perpendicular to the main axis

        double d = mainPosition / mainDirection; // how far to hit face behind
        double secondd = secondPosition - secondDirection * d;
        double thirdd = thirdPosition - thirdDirection * d;

        // Guarantee that the ray will pass though the start block.
        // It is possible that it would miss due to rounding
        // This should only move the ray by 1 grid position
        secondError = MathHelper.floor(secondd * gridSize);
        secondStep = MathHelper.round(secondDirection / mainDirection * gridSize);
        thirdError = MathHelper.floor(thirdd * gridSize);
        thirdStep = MathHelper.round(thirdDirection / mainDirection * gridSize);

        if (secondError + secondStep <= 0) {
            secondError = -secondStep + 1;
        }

        if (thirdError + thirdStep <= 0) {
            thirdError = -thirdStep + 1;
        }

        Location lastBlock = startBlock.offset(mainFace.getOpposite());

        if (secondError < 0) {
            secondError += gridSize;
            lastBlock = lastBlock.offset(secondFace.getOpposite());
        }

        if (thirdError < 0) {
            thirdError += gridSize;
            lastBlock = lastBlock.offset(thirdFace.getOpposite());
        }

        // This means that when the variables are positive, it means that the coord=1 boundary has been crossed
        secondError -= gridSize;
        thirdError -= gridSize;

        blockQueue[0] = lastBlock;
        currentBlock = -1;

        scan();

        boolean startBlockFound = false;

        for (int cnt = currentBlock; cnt >= 0; cnt--) {
            if (blockEquals(blockQueue[cnt], startBlock)) {
                currentBlock = cnt;
                startBlockFound = true;
                break;
            }
        }

        if (!startBlockFound) {
            throw new IllegalStateException("Start block missed in BlockIterator");
        }

        // Calculate the number of planes passed to give max distance
        maxDistanceInt = MathHelper.round(maxDistance / (Math.sqrt(mainDirection * mainDirection + secondDirection * secondDirection + thirdDirection * thirdDirection) / mainDirection));

    }

    private boolean blockEquals(@NotNull Location a, @NotNull Location b) {
        return a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
    }

    private Facing getXFace(@NotNull Vector direction) {
        return ((direction.getX() > 0) ? Facing.EAST : Facing.WEST);
    }

    private Facing getYFace(@NotNull Vector direction) {
        return ((direction.getY() > 0) ? Facing.UP : Facing.DOWN);
    }

    private Facing getZFace(@NotNull Vector direction) {
        return ((direction.getZ() > 0) ? Facing.SOUTH : Facing.NORTH);
    }

    private double getXLength(@NotNull Vector direction) {
        return Math.abs(direction.getX());
    }

    private double getYLength(@NotNull Vector direction) {
        return Math.abs(direction.getY());
    }

    private double getZLength(@NotNull Vector direction) {
        return Math.abs(direction.getZ());
    }

    private double getPosition(double direction, double position, int blockPosition) {
        return direction > 0 ? (position - blockPosition) : (blockPosition + 1 - position);
    }

    private double getXPosition(@NotNull Vector direction, @NotNull Vector position, @NotNull Location block) {
        return getPosition(direction.getX(), position.getX(), block.getBlockX());
    }

    private double getYPosition(@NotNull Vector direction, @NotNull Vector position, @NotNull Location block) {
        return getPosition(direction.getY(), position.getY(), block.getBlockY());
    }

    private double getZPosition(@NotNull Vector direction, @NotNull Vector position, @NotNull Location block) {
        return getPosition(direction.getZ(), position.getZ(), block.getBlockZ());
    }

    public BlockAccess getBlockAccess() {
        return this.blockAccess;
    }

    /**
     * Constructs the BlockIterator.
     * This considers all blocks as 1x1x1 in size.
     *
     * @param loc The location for the start of the ray trace
     * @param yOffset The trace begins vertically offset from the start vector
     *     by this value
     * @param maxDistance This is the maximum distance in blocks for the
     *     trace. Setting this value above 140 may lead to problems with
     *     unloaded chunks. A value of 0 indicates no limit
     */
    public BlockIterator(@NotNull BlockAccess blockAccess, @NotNull Location loc, double yOffset, int maxDistance) {
        this(blockAccess, loc.toVector(), loc.getDirection(), yOffset, maxDistance);
    }

    /**
     * Constructs the BlockIterator.
     * This considers all blocks as 1x1x1 in size.
     *
     * @param loc The location for the start of the ray trace
     * @param yOffset The trace begins vertically offset from the start vector
     *     by this value
     */

    public BlockIterator(@NotNull BlockAccess blockAccess, @NotNull Location loc, double yOffset) {
        this(blockAccess, loc.toVector(), loc.getDirection(), yOffset, 0);
    }

    /**
     * Constructs the BlockIterator.
     * This considers all blocks as 1x1x1 in size.
     *
     * @param loc The location for the start of the ray trace
     */

    public BlockIterator(@NotNull BlockAccess blockAccess, @NotNull Location loc) {
        this(blockAccess, loc, 0D);
    }

    /**
     * Constructs the BlockIterator.
     * This considers all blocks as 1x1x1 in size.
     *
     * @param entity Information from the entity is used to set up the trace
     * @param maxDistance This is the maximum distance in blocks for the
     *     trace. Setting this value above 140 may lead to problems with
     *     unloaded chunks. A value of 0 indicates no limit
     */

    public BlockIterator(@NotNull BlockAccess blockAccess, @NotNull Entity entity, int maxDistance) {
        this(blockAccess, entity.getLocation(), entity.getHeadHeight(), maxDistance);
    }

    /**
     * Returns true if the iteration has more elements.
     */
    @Override
    public boolean hasNext() {
        scan();
        return currentBlock != -1;
    }

    /**
     * Returns the next Block in the trace.
     *
     * @return the next Block in the trace
     */
    @Override
    @NotNull
    public Location next() throws NoSuchElementException {
        scan();
        if (currentBlock <= -1) {
            throw new NoSuchElementException();
        } else {
            return blockQueue[currentBlock--];
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("[BlockIterator] doesn't support block removal");
    }

    private void scan() {
        if (currentBlock >= 0) {
            return;
        }
        if (maxDistance != 0 && currentDistance > maxDistanceInt) {
            end = true;
            return;
        }
        if (end) {
            return;
        }

        currentDistance++;

        secondError += secondStep;
        thirdError += thirdStep;

        if (secondError > 0 && thirdError > 0) {
            blockQueue[2] = blockQueue[0].offset(mainFace);
            if (((long) secondStep) * ((long) thirdError) < ((long) thirdStep) * ((long) secondError)) {
                blockQueue[1] = blockQueue[2].offset(secondFace);
                blockQueue[0] = blockQueue[1].offset(thirdFace);
            } else {
                blockQueue[1] = blockQueue[2].offset(thirdFace);
                blockQueue[0] = blockQueue[1].offset(secondFace);
            }
            thirdError -= gridSize;
            secondError -= gridSize;
            currentBlock = 2;
            return;
        } else if (secondError > 0) {
            blockQueue[1] = blockQueue[0].offset(mainFace);
            blockQueue[0] = blockQueue[1].offset(secondFace);
            secondError -= gridSize;
            currentBlock = 1;
            return;
        } else if (thirdError > 0) {
            blockQueue[1] = blockQueue[0].offset(mainFace);
            blockQueue[0] = blockQueue[1].offset(thirdFace);
            thirdError -= gridSize;
            currentBlock = 1;
            return;
        } else {
            blockQueue[0] = blockQueue[0].offset(mainFace);
            currentBlock = 0;
            return;
        }
    }
}
