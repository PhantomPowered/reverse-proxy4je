package com.github.derrop.proxy.block.chunk;

import java.util.Arrays;

/**
 * Represents a 16x16x16 block storage in a chunk. Normally this also contains the light level data and the sky light data,
 * but we don't have this here since it is easier and is not required.
 */
public class ExtendedBlockStorage {

    public static final byte[] MAX_LIGHT_LEVEL = new byte[2048];

    static {
        Arrays.fill(MAX_LIGHT_LEVEL, (byte) -1);
    }

    private int yBase;
    private char[] data;

    public ExtendedBlockStorage(int yBase) {
        this.yBase = yBase;
        this.data = new char[4096];
    }

    public int get(int x, int y, int z) {
        return this.data[y << 8 | z << 4 | x];
    }

    public void set(int x, int y, int z, int state) {
        this.data[y << 8 | z << 4 | x] = (char) state;
    }

    public char[] getData() {
        return data;
    }

    public int getYLocation() {
        return yBase;
    }
}
