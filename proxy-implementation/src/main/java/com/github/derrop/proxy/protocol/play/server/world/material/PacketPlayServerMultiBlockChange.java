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
package com.github.derrop.proxy.protocol.play.server.world.material;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerMultiBlockChange implements Packet {

    private int chunkX;
    private int chunkZ;
    private BlockUpdateData[] updateData;

    public PacketPlayServerMultiBlockChange(int chunkX, int chunkZ, BlockUpdateData[] updateData) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.updateData = updateData;
    }

    public PacketPlayServerMultiBlockChange() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.MULTI_BLOCK_CHANGE;
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public BlockUpdateData[] getUpdateData() {
        return this.updateData;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.chunkX = protoBuf.readInt();
        this.chunkZ = protoBuf.readInt();
        this.updateData = new BlockUpdateData[protoBuf.readVarInt()];

        for (int i = 0; i < this.updateData.length; i++) {
            this.updateData[i] = new BlockUpdateData(protoBuf.readShort(), protoBuf.readVarInt());
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeInt(this.chunkX);
        protoBuf.writeInt(this.chunkZ);
        protoBuf.writeVarInt(this.updateData.length);

        for (BlockUpdateData updateData : this.updateData) {
            protoBuf.writeShort(updateData.getChunkPosCrammed());
            protoBuf.writeVarInt(updateData.getBlockState());
        }
    }

    public String toString() {
        return "PacketPlayServerMultiBlockChange(chunkX=" + this.getChunkX() + ", chunkZ=" + this.getChunkZ() + ", updateData=" + java.util.Arrays.deepToString(this.getUpdateData()) + ")";
    }

    public class BlockUpdateData {
        private final short chunkPosCrammed;
        private final int blockState;

        public BlockUpdateData(short chunkPosCrammed, int blockState) {
            this.chunkPosCrammed = chunkPosCrammed;
            this.blockState = blockState;
        }

        public Location getPos() {
            int chunkX = PacketPlayServerMultiBlockChange.this.chunkX;
            int chunkZ = PacketPlayServerMultiBlockChange.this.chunkZ;

            int x = this.chunkPosCrammed >> 12 & 15;
            int y = this.chunkPosCrammed & 255;
            int z = this.chunkPosCrammed >> 8 & 15;

            return new Location((chunkX << 4) + x, y, (chunkZ << 4) + z);
        }

        public short getChunkPosCrammed() {
            return chunkPosCrammed;
        }

        public int getBlockState() {
            return blockState;
        }

        @Override
        public String toString() {
            return "BlockUpdateData{"
                    + "chunkPosCrammed=" + chunkPosCrammed
                    + ", blockState=" + blockState
                    + '}';
        }
    }

}
