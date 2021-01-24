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

public class Cuboid extends MathObject {

    private final Location lowerCorner;
    private final Location upperCorner;

    public Cuboid(@NotNull Location lowerCorner, @NotNull Location upperCorner) {
        super(Type.CUBOID);
        this.lowerCorner = lowerCorner;
        this.upperCorner = upperCorner;
    }

    @NotNull
    @Override
    public Location getCenter() {
        return new Location(
                this.lowerCorner.getX() + (this.upperCorner.getX() - this.lowerCorner.getX()) / 2,
                this.lowerCorner.getY() + (this.upperCorner.getY() - this.lowerCorner.getY()) / 2,
                this.lowerCorner.getZ() + (this.upperCorner.getZ() - this.lowerCorner.getZ()) / 2
        );
    }

    @NotNull
    public Location getLowerCorner() {
        return this.lowerCorner;
    }

    @NotNull
    public Location getUpperCorner() {
        return this.upperCorner;
    }

    @Override
    public boolean contains(@NotNull Location location) {
        return location.getX() > this.lowerCorner.getX()
                && location.getY() > this.lowerCorner.getY()
                && location.getZ() > this.lowerCorner.getZ()
                && location.getX() < this.upperCorner.getX()
                && location.getY() < this.upperCorner.getY()
                && location.getZ() < this.upperCorner.getZ();
    }

    @Override
    public void forEachLocation(@NotNull Consumer<Location> consumer) {
        for (int x = this.lowerCorner.getBlockX(); x < this.upperCorner.getBlockX(); x++) {
            for (int y = this.lowerCorner.getBlockY(); y < this.upperCorner.getBlockY(); y++) {
                for (int z = this.lowerCorner.getBlockZ(); z < this.upperCorner.getBlockZ(); z++) {
                    consumer.accept(new Location(x, y, z));
                }
            }
        }
    }

    @Override
    public void forEachLocationHollow(@NotNull Consumer<Location> consumer) {
        for (int y = this.lowerCorner.getBlockY(); y < this.upperCorner.getBlockY(); y++) {
            for (int z = this.lowerCorner.getBlockZ(); z < this.upperCorner.getBlockZ(); z++) {
                consumer.accept(new Location(this.lowerCorner.getBlockX(), y, z));
                consumer.accept(new Location(this.upperCorner.getBlockX(), y, z));
            }
        }

        for (int x = this.lowerCorner.getBlockX(); x < this.upperCorner.getBlockX(); x++) {
            for (int z = this.lowerCorner.getBlockZ(); z < this.upperCorner.getBlockZ(); z++) {
                consumer.accept(new Location(x, this.lowerCorner.getBlockY(), z));
                consumer.accept(new Location(x, this.upperCorner.getBlockY(), z));
            }
        }

        for (int x = this.lowerCorner.getBlockX(); x < this.upperCorner.getBlockX(); x++) {
            for (int y = this.lowerCorner.getBlockY(); y < this.upperCorner.getBlockY(); y++) {
                consumer.accept(new Location(x, y, this.lowerCorner.getBlockZ()));
                consumer.accept(new Location(x, y, this.lowerCorner.getBlockZ()));
            }
        }
    }
}
