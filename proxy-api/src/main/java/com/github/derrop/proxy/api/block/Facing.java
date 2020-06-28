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
package com.github.derrop.proxy.api.block;

import com.github.derrop.proxy.api.util.MathHelper;
import com.github.derrop.proxy.api.location.Vector;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

public enum Facing {

    DOWN(0, 1, -1, "down", Facing.AxisDirection.NEGATIVE, Facing.Axis.Y, new Vector(0, -1, 0)),
    UP(1, 0, -1, "up", Facing.AxisDirection.POSITIVE, Facing.Axis.Y, new Vector(0, 1, 0)),
    NORTH(2, 3, 2, "north", Facing.AxisDirection.NEGATIVE, Facing.Axis.Z, new Vector(0, 0, -1)),
    SOUTH(3, 2, 0, "south", Facing.AxisDirection.POSITIVE, Facing.Axis.Z, new Vector(0, 0, 1)),
    WEST(4, 5, 1, "west", Facing.AxisDirection.NEGATIVE, Facing.Axis.X, new Vector(-1, 0, 0)),
    EAST(5, 4, 3, "east", Facing.AxisDirection.POSITIVE, Facing.Axis.X, new Vector(1, 0, 0)),
    ;

    private final int index;
    private final int opposite;
    private final int horizontalIndex;
    private final String name;
    private final Facing.Axis axis;
    private final Facing.AxisDirection axisDirection;
    private final Vector directionVec;

    private static final Facing[] VALUES = new Facing[6];
    private static final Facing[] HORIZONTALS = new Facing[4];
    private static final Map<String, Facing> NAME_LOOKUP = Maps.newHashMap();

    Facing(int indexIn, int oppositeIn, int horizontalIndexIn, String nameIn, Facing.AxisDirection axisDirectionIn, Facing.Axis axisIn, Vector directionVecIn) {
        this.index = indexIn;
        this.horizontalIndex = horizontalIndexIn;
        this.opposite = oppositeIn;
        this.name = nameIn;
        this.axis = axisIn;
        this.axisDirection = axisDirectionIn;
        this.directionVec = directionVecIn;
    }

    public int getIndex() {
        return this.index;
    }

    public int getHorizontalIndex() {
        return this.horizontalIndex;
    }

    public Facing.AxisDirection getAxisDirection() {
        return this.axisDirection;
    }

    public Facing getOpposite() {
        return getFront(this.opposite);
    }

    public Facing rotateAround(Facing.Axis axis) {
        switch (axis) {
            case X:
                if (this != WEST && this != EAST) {
                    return this.rotateX();
                }

                return this;

            case Y:
                if (this != UP && this != DOWN) {
                    return this.rotateY();
                }

                return this;

            case Z:
                if (this != NORTH && this != SOUTH) {
                    return this.rotateZ();
                }

                return this;

            default:
                throw new IllegalStateException("Unable to get CW facing for axis " + axis);
        }
    }

    public Facing rotateY() {
        switch (this) {
            case NORTH:
                return EAST;

            case EAST:
                return SOUTH;

            case SOUTH:
                return WEST;

            case WEST:
                return NORTH;

            default:
                throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        }
    }

    private Facing rotateX() {
        switch (this) {
            case NORTH:
                return DOWN;
            case SOUTH:
                return UP;
            case UP:
                return NORTH;
            case DOWN:
                return SOUTH;
            case EAST:
            case WEST:
            default:
                throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        }
    }

    private Facing rotateZ() {
        switch (this) {
            case EAST:
                return DOWN;
            case WEST:
                return UP;
            case UP:
                return EAST;
            case DOWN:
                return WEST;
            case SOUTH:
            default:
                throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
        }
    }

    public Facing rotateYCCW() {
        switch (this) {
            case NORTH:
                return WEST;
            case EAST:
                return NORTH;
            case SOUTH:
                return EAST;
            case WEST:
                return SOUTH;
            default:
                throw new IllegalStateException("Unable to get CCW facing of " + this);
        }
    }

    public int getFrontOffsetX() {
        return this.axis == Facing.Axis.X ? this.axisDirection.getOffset() : 0;
    }

    public int getFrontOffsetY() {
        return this.axis == Facing.Axis.Y ? this.axisDirection.getOffset() : 0;
    }

    public int getFrontOffsetZ() {
        return this.axis == Facing.Axis.Z ? this.axisDirection.getOffset() : 0;
    }

    public Facing.Axis getAxis() {
        return this.axis;
    }

    public static Facing byName(@NotNull String name) {
        return NAME_LOOKUP.get(name.toLowerCase());
    }

    public static Facing getFront(int index) {
        return VALUES[Math.abs(index % VALUES.length)];
    }

    public static Facing getHorizontal(int p_176731_0_) {
        return HORIZONTALS[Math.abs(p_176731_0_ % HORIZONTALS.length)];
    }

    public static Facing fromAngle(double angle) {
        return getHorizontal(MathHelper.floor(angle / 90.0D + 0.5D) & 3);
    }

    public static Facing getFacingFromVector(float x, float y, float z) {
        Facing enumfacing = NORTH;

        float f = Float.MIN_VALUE;
        for (Facing facing : values()) {
            float f1 = x * (float) facing.directionVec.getX() + y * (float) facing.directionVec.getY() + z * (float) facing.directionVec.getZ();

            if (f1 > f) {
                f = f1;
                enumfacing = facing;
            }
        }

        return enumfacing;
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public static Facing getByAxisDirection(Facing.AxisDirection axisDirection, Facing.Axis axis) {
        for (Facing facing : values()) {
            if (facing.getAxisDirection() == axisDirection && facing.getAxis() == axis) {
                return facing;
            }
        }

        throw new IllegalArgumentException("No such direction: " + axisDirection + " " + axis);
    }

    public Vector getDirectionVec() {
        return this.directionVec;
    }

    static {
        for (Facing facing : values()) {
            VALUES[facing.index] = facing;
            NAME_LOOKUP.put(facing.getName().toLowerCase(), facing);

            if (facing.getAxis().isHorizontal()) {
                HORIZONTALS[facing.horizontalIndex] = facing;
            }
        }
    }

    public enum Axis implements Predicate<Facing> {
        X("x", Facing.Plane.HORIZONTAL),
        Y("y", Facing.Plane.VERTICAL),
        Z("z", Facing.Plane.HORIZONTAL),
        NONE("none", null);

        private static final Map<String, Facing.Axis> NAME_LOOKUP = Maps.newHashMap();
        private final String name;
        private final Facing.Plane plane;

        Axis(String name, Facing.Plane plane) {
            this.name = name;
            this.plane = plane;
        }

        public static Facing.Axis byName(String name) {
            return NAME_LOOKUP.get(name.toLowerCase());
        }

        public boolean isVertical() {
            return this.plane == Facing.Plane.VERTICAL;
        }

        public boolean isHorizontal() {
            return this.plane == Facing.Plane.HORIZONTAL;
        }

        public String toString() {
            return this.name;
        }

        @Override
        public boolean apply(Facing facing) {
            return facing != null && facing.getAxis() == this;
        }

        public Facing.Plane getPlane() {
            return this.plane;
        }

        public String getName() {
            return this.name;
        }

        static {
            for (Facing.Axis axis : values()) {
                NAME_LOOKUP.put(axis.getName().toLowerCase(), axis);
            }
        }
    }

    public enum AxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int offset;
        private final String description;

        AxisDirection(int offset, String description) {
            this.offset = offset;
            this.description = description;
        }

        public int getOffset() {
            return this.offset;
        }

        public String toString() {
            return this.description;
        }
    }

    public enum Plane implements Predicate<Facing>, Iterable<Facing> {
        HORIZONTAL,
        VERTICAL;

        public Facing[] facings() {
            switch (this) {
                case HORIZONTAL:
                    return new Facing[]{Facing.NORTH, Facing.EAST, Facing.SOUTH, Facing.WEST};
                case VERTICAL:
                    return new Facing[]{Facing.UP, Facing.DOWN};
                default:
                    throw new Error("Someone's been tampering with the universe!");
            }
        }

        @Override
        public boolean apply(Facing facing) {
            return facing != null && facing.getAxis().getPlane() == this;
        }

        @Override
        public @NotNull Iterator<Facing> iterator() {
            return Iterators.forArray(this.facings());
        }
    }
}
