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
package com.github.phantompowered.proxy.block.chunk;

import java.util.Arrays;

public class ChunkSection {

    public static final byte[] MAX_LIGHT_LEVEL = new byte[2048];

    static {
        Arrays.fill(MAX_LIGHT_LEVEL, (byte) -1);
    }

    private final int yBase;
    private final char[] data;

    public ChunkSection(int yBase) {
        this.yBase = yBase;
        this.data = new char[4096];
    }

    public int getBlockState(int x, int y, int z) {
        return this.data[y << 8 | z << 4 | x];
    }

    public void setBlockState(int x, int y, int z, int state) {
        this.data[y << 8 | z << 4 | x] = (char) state;
    }

    public char[] getData() {
        return data;
    }

    public int getYLocation() {
        return yBase;
    }
}
