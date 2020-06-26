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
package com.github.derrop.proxy.block.chunk;

import com.github.derrop.proxy.api.block.BlockConsumer;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.protocol.play.server.world.material.PacketPlayServerMapChunk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Chunk {

    private final ChunkSection[] storageArrays = new ChunkSection[16];
    private PacketPlayServerMapChunk lastChunkData;
    private final byte[] biomeArray = new byte[256];

    public void fillChunk(PacketPlayServerMapChunk chunkData, int dimension) {
        this.fillChunk(chunkData.getExtracted().data, chunkData.getExtracted().dataLength, chunkData.isFullChunk(), dimension);
        this.lastChunkData = chunkData;
    }

    private void fillChunk(byte[] data, int chunkSize, boolean fullChunk, int dimension) { // TODO doesn't work in the end
        boolean hasSky = dimension == 0;

        int i = 0;

        for (int j = 0; j < this.storageArrays.length; ++j) {
            if ((chunkSize & 1 << j) != 0) {
                if (this.storageArrays[j] == null) {
                    this.storageArrays[j] = new ChunkSection(j << 4);
                }

                char[] storage = this.storageArrays[j].getData();

                for (int k = 0; k < storage.length; ++k) {
                    storage[k] = (char) ((data[i + 1] & 255) << 8 | data[i] & 255);
                    i += 2;
                }
            } else if (fullChunk && this.storageArrays[j] != null) {
                this.storageArrays[j] = null;
            }
        }

        for (int j = 0; j < this.storageArrays.length; ++j) {
            if ((chunkSize & 1 << j) != 0 && this.storageArrays[j] != null) {
                i += ChunkSection.MAX_LIGHT_LEVEL.length; // skip block light data
                if (hasSky) {
                    i += ChunkSection.MAX_LIGHT_LEVEL.length; // skip sky light data
                }
            }
        }

        if (fullChunk) {
            System.arraycopy(data, i, this.biomeArray, 0, this.biomeArray.length);
        }

    }

    public PacketPlayServerMapChunk.Extracted getBytes(int dimension) {
        if (this.lastChunkData == null) {
            return null;
        }

        int maxLength = 65535;
        boolean fullChunk = true;//this.lastChunkData.isFullChunk();
        boolean hasSky = dimension == 0; // 0 = overworld; -1 = nether; 1 = end

        ChunkSection[] storages = this.storageArrays;
        PacketPlayServerMapChunk.Extracted extracted = new PacketPlayServerMapChunk.Extracted();
        List<ChunkSection> list = new ArrayList<>();

        for (int i = 0; i < storages.length; ++i) {
            ChunkSection storage = storages[i];

            if (storage != null && (maxLength & 1 << i) != 0) {
            //if (storage != null && (!fullChunk || /*!storage.isEmpty()*/ true) && (maxLength & 1 << i) != 0) {
                extracted.dataLength |= 1 << i;
                list.add(storage);
            }
        }

        extracted.data = new byte[PacketPlayServerMapChunk.getArraySize(Integer.bitCount(extracted.dataLength), hasSky, fullChunk)];
        int j = 0;

        for (ChunkSection extendedblockstorage1 : list) {
            char[] achar = extendedblockstorage1.getData();

            for (char c0 : achar) {
                extracted.data[j++] = (byte) (c0 & 255);
                extracted.data[j++] = (byte) (c0 >> 8 & 255);
            }
        }

        for (ChunkSection extendedblockstorage2 : list) {
            j = copyArray(ChunkSection.MAX_LIGHT_LEVEL, extracted.data, j);
        }

        if (hasSky) {
            for (ChunkSection extendedblockstorage3 : list) {
                j = copyArray(ChunkSection.MAX_LIGHT_LEVEL, extracted.data, j);
            }
        }

        if (fullChunk) { // todo: why is this always a full chunk?
            copyArray(this.biomeArray, extracted.data, j);
        }

        return extracted;
    }

    private static int copyArray(byte[] sourceArray, byte[] targetArray, int copyAmount) {
        System.arraycopy(sourceArray, 0, targetArray, copyAmount, sourceArray.length);
        return copyAmount + sourceArray.length;
    }
    
    public int getBlockStateAt(int x, int y, int z) {
        if (y >= 0 && y >> 4 < this.storageArrays.length) {
            ChunkSection extendedblockstorage = this.storageArrays[y >> 4];

            if (extendedblockstorage != null) {
                return extendedblockstorage.get(x & 15, y & 15, z & 15);
            } else {
                return 0;
            }
        }

        return -1;
    }

    public void setBlockStateAt(int x, int y, int z, int state) {
        ChunkSection storage = this.storageArrays[y >> 4];

        if (storage == null) {
            if (state == 0) { // air
                return;
            }

            storage = this.storageArrays[y >> 4] = new ChunkSection(y >> 4 << 4);
        }

        storage.set(x & 15, y & 15, z & 15, state);
    }

    public int[][][] getAllBlockStates() {
        int[][][] result = new int[16][256][16];

        this.forEachBlockStates((x, y, z, oldState, state) -> result[x][y][z] = state);

        return result;
    }

    public Collection<Location> getPositionsByState(int allowedState) {
        Collection<Location> result = new ArrayList<>();

        this.forEachBlockStates((x, y, z, oldState, state) -> {
            if (state == allowedState) {
                result.add(new Location(x, y, z));
            }
        });

        return result;
    }

    public Collection<Location> getPositionsByStates(int[] allowedStates) {
        Collection<Location> result = new ArrayList<>();

        this.forEachBlockStates((x, y, z, oldState, state) -> {
            for (int allowedState : allowedStates) {
                if (allowedState == state) {
                    result.add(new Location(x, y, z));
                }
            }
        });

        return result;
    }

    private void forEachBlockStates(BlockConsumer consumer) {
        for (int y = 0; y < this.storageArrays.length; y++) {
            ChunkSection storage = this.storageArrays[y];
            if (storage == null) {
                for (int x = 0; x < 16; x++) {
                    for (int cY = 0; cY < 16; cY++) {
                        for (int z = 0; z < 16; z++) {
                            consumer.accept(x, cY + (y * 16), z, -1, 0);
                        }
                    }
                }
                continue;
            }

            for (int x = 0; x < 16; x++) {
                for (int cY = 0; cY < 16; cY++) {
                    for (int z = 0; z < 16; z++) {
                        consumer.accept(x, cY + (y * 16), z, -1, storage.get(x, cY, z));
                    }
                }
            }

        }
    }

    public PacketPlayServerMapChunk getLastChunkData() {
        return lastChunkData;
    }

    public int getX() {
        return this.lastChunkData.getX();
    }

    public int getZ() {
        return this.lastChunkData.getZ();
    }

    public boolean contains(Location pos) {
        return pos.isInChunk(this.getX(), this.getZ());
    }

}
