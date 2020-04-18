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
package com.github.derrop.proxy.api.util.nbt;

import com.github.derrop.proxy.api.util.MathHelper;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagFloat extends NBTBase.NBTPrimitive {
    /**
     * The float value for the tag.
     */
    private float data;

    NBTTagFloat() {
    }

    public NBTTagFloat(float data) {
        this.data = data;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput output) throws IOException {
        output.writeFloat(this.data);
    }

    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(96L);
        this.data = input.readFloat();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return (byte) 5;
    }

    public String toString() {
        return "" + this.data + "f";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy() {
        return new NBTTagFloat(this.data);
    }

    public boolean equals(Object p_equals_1_) {
        if (super.equals(p_equals_1_)) {
            NBTTagFloat nbttagfloat = (NBTTagFloat) p_equals_1_;
            return this.data == nbttagfloat.data;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return super.hashCode() ^ Float.floatToIntBits(this.data);
    }

    public long getLong() {
        return (long) this.data;
    }

    public int getInt() {
        return MathHelper.floor_float(this.data);
    }

    public short getShort() {
        return (short) (MathHelper.floor_float(this.data) & 65535);
    }

    public byte getByte() {
        return (byte) (MathHelper.floor_float(this.data) & 255);
    }

    public double getDouble() {
        return this.data;
    }

    public float getFloat() {
        return this.data;
    }
}
