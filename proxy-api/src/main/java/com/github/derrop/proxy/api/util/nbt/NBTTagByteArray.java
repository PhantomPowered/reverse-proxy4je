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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagByteArray extends NBTBase {
    /**
     * The byte array stored in the tag.
     */
    private byte[] data;

    NBTTagByteArray() {
    }

    public NBTTagByteArray(byte[] data) {
        this.data = data;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput output) throws IOException {
        output.writeInt(this.data.length);
        output.write(this.data);
    }

    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(192L);
        int i = input.readInt();
        sizeTracker.read(8 * i);
        this.data = new byte[i];
        input.readFully(this.data);
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return (byte) 7;
    }

    public String toString() {
        return "[" + this.data.length + " bytes]";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy() {
        byte[] abyte = new byte[this.data.length];
        System.arraycopy(this.data, 0, abyte, 0, this.data.length);
        return new NBTTagByteArray(abyte);
    }

    public boolean equals(Object p_equals_1_) {
        return super.equals(p_equals_1_) && Arrays.equals(this.data, ((NBTTagByteArray) p_equals_1_).data);
    }

    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(this.data);
    }

    public byte[] getByteArray() {
        return this.data;
    }
}
