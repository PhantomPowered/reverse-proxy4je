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
package com.github.derrop.proxy.util;

import com.github.derrop.proxy.api.util.EulerAngle;
import com.github.derrop.proxy.api.util.nbt.NBTTagFloat;
import com.github.derrop.proxy.api.util.nbt.NBTTagList;

public class Rotations {

    /**
     * Rotation on the X axis
     */
    protected final float x;

    /**
     * Rotation on the Y axis
     */
    protected final float y;

    /**
     * Rotation on the Z axis
     */
    protected final float z;

    public Rotations(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Rotations(NBTTagList nbt) {
        this.x = nbt.getFloatAt(0);
        this.y = nbt.getFloatAt(1);
        this.z = nbt.getFloatAt(2);
    }

    public NBTTagList writeToNBT() {
        NBTTagList nbttaglist = new NBTTagList();
        nbttaglist.appendTag(new NBTTagFloat(this.x));
        nbttaglist.appendTag(new NBTTagFloat(this.y));
        nbttaglist.appendTag(new NBTTagFloat(this.z));
        return nbttaglist;
    }

    public boolean equals(Object p_equals_1_) {
        if (!(p_equals_1_ instanceof Rotations)) {
            return false;
        } else {
            Rotations rotations = (Rotations) p_equals_1_;
            return this.x == rotations.x && this.y == rotations.y && this.z == rotations.z;
        }
    }

    public EulerAngle asEuler() {
        return new EulerAngle(this.x, this.y, this.z);
    }

    /**
     * Gets the X axis rotation
     */
    public float getX() {
        return this.x;
    }

    /**
     * Gets the Y axis rotation
     */
    public float getY() {
        return this.y;
    }

    /**
     * Gets the Z axis rotation
     */
    public float getZ() {
        return this.z;
    }
}
