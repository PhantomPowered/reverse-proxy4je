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
package com.github.phantompowered.proxy.protocol.play.server.world.material;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerMapChunk implements Packet {

    private int x;
    private int z;
    private boolean fullChunk;
    private ChunkData chunkData;

    public PacketPlayServerMapChunk(int x, int z, boolean fullChunk, ChunkData chunkData) {
        this.x = x;
        this.z = z;
        this.fullChunk = fullChunk;
        this.chunkData = chunkData;
    }

    public PacketPlayServerMapChunk() {
    }

    public static int getArraySize(int dataLengthBits, boolean hasSky, boolean fullChunk) {
        int blockStateBytes = dataLengthBits * 2 * 4096;
        int blockLightBytes = dataLengthBits * 4096 / 2;
        int skyLightBytes = hasSky ? blockLightBytes : 0;
        int biomeBytes = fullChunk ? 256 : 0;
        return blockStateBytes + blockLightBytes + skyLightBytes + biomeBytes;
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.MAP_CHUNK;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public boolean isFullChunk() {
        return this.fullChunk;
    }

    public ChunkData getExtracted() {
        return this.chunkData;
    }

    public void setExtracted(ChunkData chunkData) {
        this.chunkData = chunkData;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.x = protoBuf.readInt();
        this.z = protoBuf.readInt();
        this.fullChunk = protoBuf.readBoolean();
        this.chunkData = new ChunkData();
        this.chunkData.dataLength = protoBuf.readShort();
        this.chunkData.data = protoBuf.readArray();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeInt(this.x);
        protoBuf.writeInt(this.z);
        protoBuf.writeBoolean(this.fullChunk);
        protoBuf.writeShort(this.chunkData.dataLength);
        protoBuf.writeArray(this.chunkData.data);
    }

    public String toString() {
        return "PacketPlayServerMapChunk(x=" + this.getX() + ", z=" + this.getZ() + ", fullChunk=" + this.isFullChunk() + ", extracted=" + this.getExtracted() + ")";
    }

    public static class ChunkData {
        public byte[] data;
        public int dataLength;

        @Override
        public String toString() {
            return "Extracted{"
                    + "data=" + data.length
                    + ", dataLength=" + dataLength
                    + '}';
        }
    }
}
