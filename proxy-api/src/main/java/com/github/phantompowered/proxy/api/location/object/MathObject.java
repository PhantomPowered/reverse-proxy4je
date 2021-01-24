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
package com.github.phantompowered.proxy.api.location.object;

import com.github.phantompowered.proxy.api.location.Location;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class MathObject {

    private final Type type;

    protected MathObject(@NotNull Type type) {
        this.type = type;
    }

    @NotNull
    public static Cuboid cuboidFromBottomCenter(@NotNull Location center, double width, double height) {
        return new Cuboid(
                new Location(
                        center.getX() - width / 2,
                        center.getY(),
                        center.getZ() - width / 2
                ),
                new Location(
                        center.getX() + width / 2,
                        center.getY() + height,
                        center.getZ() + width / 2
                )
        );
    }

    @NotNull
    public static Cuboid cuboidFromCenter(@NotNull Location center, double width, double height) {
        return new Cuboid(
                new Location(
                        center.getX() - width / 2,
                        center.getY() - height / 2,
                        center.getZ() - width / 2
                ),
                new Location(
                        center.getX() + width / 2,
                        center.getY() + height / 2,
                        center.getZ() + width / 2
                )
        );
    }

    @NotNull
    public static Cuboid cuboid(@NotNull Location corner1, @NotNull Location corner2) {
        return new Cuboid(
                new Location(
                        Math.min(corner1.getX(), corner2.getX()),
                        Math.min(corner1.getY(), corner2.getY()),
                        Math.min(corner1.getZ(), corner2.getZ())
                ),
                new Location(
                        Math.max(corner1.getX(), corner2.getX()),
                        Math.max(corner1.getY(), corner2.getY()),
                        Math.max(corner1.getZ(), corner2.getZ())
                )
        );
    }

    @NotNull
    public static Sphere sphere(@NotNull Location center, int radius) {
        return new Sphere(center, radius);
    }

    @NotNull
    public Type getType() {
        return this.type;
    }

    @NotNull
    public abstract Location getCenter();

    public abstract boolean contains(@NotNull Location location);

    public abstract void forEachLocation(@NotNull Consumer<Location> consumer);

    public abstract void forEachLocationHollow(@NotNull Consumer<Location> consumer);

    public enum Type {

        CUBOID,
        SPHERE
        // TODO CYLINDER

    }

}
