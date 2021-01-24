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

public class Sphere extends MathObject {

    private static final double RAD = Math.toRadians(1);

    private final Location center;
    private final int radius;
    private final int radiusSquared;

    protected Sphere(@NotNull Location center, int radius) {
        super(Type.SPHERE);
        this.center = center;
        this.radius = radius;
        this.radiusSquared = radius * radius;
    }

    @NotNull
    @Override
    public Location getCenter() {
        return this.center;
    }

    public int getRadius() {
        return this.radius;
    }

    public int getRadiusSquared() {
        return this.radiusSquared;
    }

    @Override
    public boolean contains(@NotNull Location location) {
        return this.center.distanceSquared(location) <= this.radiusSquared;
    }

    @Override
    public void forEachLocation(@NotNull Consumer<Location> consumer) {
        for (double x = -this.radius; x < this.radius; x++) {
            double xSq = x * x;
            for (double y = -this.radius; y < this.radius; y++) {
                double ySq = y * y;
                for (double z = -this.radius; z < this.radius; z++) {
                    double zSq = z * z;
                    if (xSq + ySq + zSq <= this.radiusSquared) {
                        consumer.accept(this.center.clone().add(x, y, z));
                    }
                }
            }
        }
    }

    @Override
    public void forEachLocationHollow(@NotNull Consumer<Location> consumer) {
        for (double p = 0; p < Math.PI * 2; p += Math.PI / 50) {
            for (double t = 0; t <= Math.PI * 2; t += RAD) {
                consumer.accept(this.center.clone().add(
                        this.radius * Math.cos(t) * Math.sin(p),
                        this.radius * Math.cos(p),
                        this.radius * Math.sin(t) * Math.sin(p)
                ));
            }
        }
    }
}
