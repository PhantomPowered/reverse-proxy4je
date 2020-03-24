package de.derrop.minecraft.proxy.util.chunk;

public class ExtendedBlockStorage {

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
