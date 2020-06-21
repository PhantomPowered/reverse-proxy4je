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

import org.jetbrains.annotations.Contract;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class NBTBase {

    public abstract void write(DataOutput output) throws IOException;

    public abstract void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException;

    public abstract String toString();

    public abstract byte getId();

    public abstract NBTBase copy();

    public boolean hasNoTags() {
        return false;
    }

    @Contract(value = "null -> false", pure = true)
    public boolean equals(Object other) {
        if (!(other instanceof NBTBase)) {
            return false;
        } else {
            NBTBase nbtbase = (NBTBase) other;
            return this.getId() == nbtbase.getId();
        }
    }

    public int hashCode() {
        return this.getId();
    }

    public String getString() {
        return this.toString();
    }

    protected static NBTBase createNewByType(byte id) {
        switch (id) {
            case 0:
                return new NBTTagEnd();
            case 1:
                return new NBTTagByte();
            case 2:
                return new NBTTagShort();
            case 3:
                return new NBTTagInt();
            case 4:
                return new NBTTagLong();
            case 5:
                return new NBTTagFloat();
            case 6:
                return new NBTTagDouble();
            case 7:
                return new NBTTagByteArray();
            case 8:
                return new NBTTagString();
            case 9:
                return new NBTTagList();
            case 10:
                return new NBTTagCompound();
            case 11:
                return new NBTTagIntArray();
            default:
                return null;
        }
    }

    public abstract static class NBTPrimitive extends NBTBase {

        public abstract long getLong();

        public abstract int getInt();

        public abstract short getShort();

        public abstract byte getByte();

        public abstract double getDouble();

        public abstract float getFloat();
    }
}
