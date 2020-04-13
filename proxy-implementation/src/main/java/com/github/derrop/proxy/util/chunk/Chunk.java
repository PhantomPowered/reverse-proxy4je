package com.github.derrop.proxy.util.chunk;

import com.github.derrop.proxy.connection.cache.packet.world.ChunkData;
import com.github.derrop.proxy.util.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Chunk {

    private ExtendedBlockStorage[] storageArrays = new ExtendedBlockStorage[16];
    private ChunkData lastChunkData;

    public void fillChunk(ChunkData chunkData) {
        this.fillChunk(chunkData.getExtracted().data, chunkData.getExtracted().dataLength, chunkData.isB());
        this.lastChunkData = chunkData;
    }

    private void fillChunk(byte[] data, int chunkSize, boolean b) {
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
            } else if (b && this.storageArrays[j] != null) {
                this.storageArrays[j] = null;
            }
        }
    }

    public void fillChunkData(ChunkData chunkData) {
        if (this.lastChunkData == null) {
            return;
        }

        int maxLength = 65535;
        boolean fullChunk = true;
        boolean hasSky = this.lastChunkData.isB();

        ExtendedBlockStorage[] storages = this.storageArrays;
        ChunkData.Extracted extracted = new ChunkData.Extracted();
        List<ExtendedBlockStorage> list = new ArrayList<>();

        for (int i = 0; i < storages.length; ++i) {
            ExtendedBlockStorage storage = storages[i];

            if (storage != null && (!fullChunk || /*!storage.isEmpty()*/ true) && (maxLength & 1 << i) != 0) {
                extracted.dataLength |= 1 << i;
                list.add(storage);
            }
        }

        extracted.data = new byte[ChunkData.getArraySize(Integer.bitCount(extracted.dataLength), hasSky, fullChunk)];
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

        if (hasSky) {
            for (ExtendedBlockStorage extendedblockstorage3 : list) {
                j = copyArray(ExtendedBlockStorage.MAX_LIGHT_LEVEL, extracted.data, j);
            }
        }

        /*if (fullChunk) {
            copyArray(p_179756_0_.getBiomeArray(), extracted.data, j);
        }*/

        chunkData.setB(this.lastChunkData.isB());
        chunkData.setExtracted(extracted);
        chunkData.setX(this.lastChunkData.getX());
        chunkData.setZ(this.lastChunkData.getZ());
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

        return DefaultBlockStates.AIR;
    }

    public void setBlockStateAt(int x, int y, int z, int state) {
        ExtendedBlockStorage storage = this.storageArrays[y >> 4];

        if (storage == null) {
            if (state == DefaultBlockStates.AIR) {
                return;
            }

            storage = this.storageArrays[y >> 4] = new ExtendedBlockStorage(y >> 4 << 4);
        }

        storage.set(x & 15, y & 15, z & 15, state);

        // TODO implement light level updates?
        /*if (extendedblockstorage.getBlockByExtId(i, j & 15, k) != block) {
            return null;
        } else {
            if (flag) {
                this.generateSkylightMap();
            } else {
                int j1 = block.getLightOpacity();
                int k1 = block1.getLightOpacity();

                if (j1 > 0) {
                    if (j >= i1) {
                        this.relightBlock(i, j + 1, k);
                    }
                } else if (j == i1 - 1) {
                    this.relightBlock(i, j, k);
                }

                if (j1 != k1 && (j1 < k1 || this.getLightFor(EnumSkyBlock.SKY, pos) > 0 || this.getLightFor(EnumSkyBlock.BLOCK, pos) > 0)) {
                    this.propagateSkylightOcclusion(i, k);
                }
            }
        }*/
    }

    public ChunkData getLastChunkData() {
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
