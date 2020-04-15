package com.github.derrop.proxy.block.chunk;

import com.github.derrop.proxy.api.block.BlockConsumer;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.protocol.play.server.world.PacketPlayServerChunkData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Chunk {

    private ExtendedBlockStorage[] storageArrays = new ExtendedBlockStorage[16];
    private PacketPlayServerChunkData lastChunkData;
    private byte[] biomeArray = new byte[256];
    private boolean hasSky;

    public void fillChunk(PacketPlayServerChunkData chunkData) {
        this.fillChunk(chunkData.getExtracted().data, chunkData.getExtracted().dataLength, chunkData.isFullChunk());
        this.lastChunkData = chunkData;
    }

    private void fillChunk(byte[] data, int chunkSize, boolean fullChunk) { // TODO this doesn't work in the nether, but hasSky works perfectly
        int i = 0;

        for (int j = 0; j < this.storageArrays.length; ++j) {
            if ((chunkSize & 1 << j) != 0) {
                if (this.storageArrays[j] == null) {
                    this.storageArrays[j] = new ExtendedBlockStorage(j << 4);
                }

                char[] achar = this.storageArrays[j].getData();

                for (int k = 0; k < achar.length; ++k) {
                    achar[k] = (char) ((data[i + 1] & 255) << 8 | data[i] & 255);
                    i += 2;
                }
            } else if (fullChunk && this.storageArrays[j] != null) {
                this.storageArrays[j] = null;
            }
        }

        for (int j = 0; j < this.storageArrays.length; j++) {
            if (this.storageArrays[j] != null) {
                i += 2048; // skip block light data
            }
        }
        int oldI = i;
        for (int j = 0; j < this.storageArrays.length; j++) {
            if (this.storageArrays[j] != null) {
                i += 2048; // skip sky light data
            }
        }
        this.hasSky = data.length - i == (fullChunk ? 256 : 0); // has the packet skylight data? (256 = biome data -> sent if fullChunk is true)
        if (!hasSky) {
            i = oldI;
        }

        if (fullChunk) {
            System.arraycopy(data, i, this.biomeArray, 0, 256);
        }

    }

    public PacketPlayServerChunkData.Extracted getBytes() {
        if (this.lastChunkData == null) {
            return null;
        }

        int maxLength = 65535;
        boolean fullChunk = this.lastChunkData.isFullChunk();

        ExtendedBlockStorage[] storages = this.storageArrays;
        PacketPlayServerChunkData.Extracted extracted = new PacketPlayServerChunkData.Extracted();
        List<ExtendedBlockStorage> list = new ArrayList<>();

        for (int i = 0; i < storages.length; ++i) {
            ExtendedBlockStorage storage = storages[i];

            if (storage != null && (!fullChunk || /*!storage.isEmpty()*/ true) && (maxLength & 1 << i) != 0) {
                extracted.dataLength |= 1 << i;
                list.add(storage);
            }
        }

        extracted.data = new byte[PacketPlayServerChunkData.getArraySize(Integer.bitCount(extracted.dataLength), hasSky, fullChunk)];
        int j = 0;

        for (ExtendedBlockStorage extendedblockstorage1 : list) {
            char[] achar = extendedblockstorage1.getData();

            for (char c0 : achar) {
                extracted.data[j++] = (byte) (c0 & 255);
                extracted.data[j++] = (byte) (c0 >> 8 & 255);
            }
        }

        for (ExtendedBlockStorage extendedblockstorage2 : list) {
            j = copyArray(ExtendedBlockStorage.MAX_LIGHT_LEVEL, extracted.data, j);
        }

        if (this.hasSky) {
            for (ExtendedBlockStorage extendedblockstorage3 : list) {
                j = copyArray(ExtendedBlockStorage.MAX_LIGHT_LEVEL, extracted.data, j);
            }
        }

        if (fullChunk) {
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
            ExtendedBlockStorage extendedblockstorage = this.storageArrays[y >> 4];

            if (extendedblockstorage != null) {
                return extendedblockstorage.get(x & 15, y & 15, z & 15);
            }
        }

        return -1;
    }

    public void setBlockStateAt(int x, int y, int z, int state) {
        ExtendedBlockStorage storage = this.storageArrays[y >> 4];

        if (storage == null) {
            if (state == 0) { // air
                return;
            }

            storage = this.storageArrays[y >> 4] = new ExtendedBlockStorage(y >> 4 << 4);
        }

        storage.set(x & 15, y & 15, z & 15, state);
    }

    public int[][][] getAllBlockStates() {
        int[][][] result = new int[16][256][16];

        this.forEachBlockStates((x, y, z, oldState, state) -> result[x][y][z] = state);

        return result;
    }

    public Collection<BlockPos> getPositionsByState(int allowedState) {
        Collection<BlockPos> result = new ArrayList<>();

        this.forEachBlockStates((x, y, z, oldState, state) -> {
            if (state == allowedState) {
                result.add(new BlockPos(x, y, z));
            }
        });

        return result;
    }

    public Collection<BlockPos> getPositionsByStates(int[] allowedStates) {
        Collection<BlockPos> result = new ArrayList<>();

        this.forEachBlockStates((x, y, z, oldState, state) -> {
            for (int allowedState : allowedStates) {
                if (allowedState == state) {
                    result.add(new BlockPos(x, y, z));
                }
            }
        });

        return result;
    }

    private void forEachBlockStates(BlockConsumer consumer) {
        for (int y = 0; y < this.storageArrays.length; y++) {
            ExtendedBlockStorage storage = this.storageArrays[y];
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

    public PacketPlayServerChunkData getLastChunkData() {
        return lastChunkData;
    }

    public int getX() {
        return this.lastChunkData.getX();
    }

    public int getZ() {
        return this.lastChunkData.getZ();
    }

    public boolean contains(BlockPos pos) {
        return pos.isInChunk(this.getX(), this.getZ());
    }

}
