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

public class PacketPlayServerMapChunkBulk implements Packet {

    private int[] x;
    private int[] z;
    private boolean b;
    private PacketPlayServerMapChunk.ChunkData[] chunkData;

    public PacketPlayServerMapChunkBulk(int[] x, int[] z, boolean b, PacketPlayServerMapChunk.ChunkData[] chunkData) {
        this.x = x;
        this.z = z;
        this.b = b;
        this.chunkData = chunkData;
    }

    public PacketPlayServerMapChunkBulk() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.MAP_CHUNK_BULK;
    }

    public int[] getX() {
        return this.x;
    }

    public void setX(int[] x) {
        this.x = x;
    }

    public int[] getZ() {
        return this.z;
    }

    public void setZ(int[] z) {
        this.z = z;
    }

    public boolean isB() {
        return this.b;
    }

    public PacketPlayServerMapChunk.ChunkData[] getExtracted() {
        return this.chunkData;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.b = protoBuf.readBoolean();
        int size = protoBuf.readVarInt();
        this.x = new int[size];
        this.z = new int[size];
        this.chunkData = new PacketPlayServerMapChunk.ChunkData[size];

        for (int i = 0; i < size; i++) {
            this.x[i] = protoBuf.readInt();
            this.z[i] = protoBuf.readInt();
            this.chunkData[i] = new PacketPlayServerMapChunk.ChunkData();
            this.chunkData[i].dataLength = protoBuf.readShort() & 65535;

            this.chunkData[i].data = new byte[PacketPlayServerMapChunk.getArraySize(Integer.bitCount(this.chunkData[i].dataLength), this.b, true)];
        }

        for (int i = 0; i < size; i++) {
            protoBuf.readBytes(this.chunkData[i].data);
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeBoolean(this.b);
        protoBuf.writeVarInt(this.chunkData.length);

        for (int i = 0; i < this.chunkData.length; i++) {
            protoBuf.writeInt(this.x[i]);
            protoBuf.writeInt(this.z[i]);
            protoBuf.writeShort(this.chunkData[i].dataLength & 65535);
        }

        for (PacketPlayServerMapChunk.ChunkData value : this.chunkData) {
            protoBuf.writeBytes(value.data);
        }
    }

    public String toString() {
        return "PacketPlayServerMapChunkBulk(x=" + java.util.Arrays.toString(this.getX()) + ", z=" + java.util.Arrays.toString(this.getZ()) + ", b=" + this.isB() + ", extracted=" + java.util.Arrays.deepToString(this.getExtracted()) + ")";
    }
}
