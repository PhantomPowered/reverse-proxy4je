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
package com.github.phantompowered.proxy.api.raytrace;

import com.github.phantompowered.proxy.api.entity.types.Entity;
import com.github.phantompowered.proxy.api.location.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockingObject {

    public static final BlockingObject MISS = new BlockingObject(null, null, Type.MISS);

    private final Entity entity;
    private final Location location;
    private final Type type;

    public BlockingObject(@Nullable Entity entity, @Nullable Location location, @NotNull Type type) {
        this.entity = entity;
        this.location = location;
        this.type = type;
    }

    @Nullable
    public Entity getEntity() {
        return this.entity;
    }

    @Nullable
    public Location getLocation() {
        return this.location;
    }

    @NotNull
    public Type getType() {
        return this.type;
    }

    public enum Type {
        MISS,
        ENTITY,
        BLOCK
    }

}
