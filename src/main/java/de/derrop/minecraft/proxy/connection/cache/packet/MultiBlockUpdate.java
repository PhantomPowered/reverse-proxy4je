package de.derrop.minecraft.proxy.connection.cache.packet;

import de.derrop.minecraft.proxy.util.BlockPos;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class MultiBlockUpdate extends DefinedPacket {

    private int chunkX;
    private int chunkZ;
    private BlockUpdateData[] updateData;

    @Override
    public void read(ByteBuf buf) {
        this.chunkX = buf.readInt();
        this.chunkZ = buf.readInt();
        this.updateData = new BlockUpdateData[readVarInt(buf)];

        for (int i = 0; i < this.updateData.length; i++) {
            this.updateData[i] = new BlockUpdateData(buf.readShort(), readVarInt(buf));
        }
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(this.chunkX);
        buf.writeInt(this.chunkZ);
        writeVarInt(this.updateData.length, buf);

        for (BlockUpdateData updateData : this.updateData) {
            buf.writeShort(updateData.getChunkPosCrammed());
            writeVarInt(updateData.getBlockState(), buf);
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }

    public class BlockUpdateData {
        private short chunkPosCrammed;
        private int blockState;

        public BlockUpdateData(short chunkPosCrammed, int blockState) {
            this.chunkPosCrammed = chunkPosCrammed;
            this.blockState = blockState;
        }

        public BlockPos getPos() {
            int chunkX = MultiBlockUpdate.this.chunkX;
            int chunkZ = MultiBlockUpdate.this.chunkZ;

            int x = this.chunkPosCrammed >> 12 & 15;
            int y = this.chunkPosCrammed & 255;
            int z = this.chunkPosCrammed >> 8 & 15;

            return new BlockPos((chunkX << 4) + x, y, (chunkZ << 4) + z);
        }

        public short getChunkPosCrammed() {
            return chunkPosCrammed;
        }

        public int getBlockState() {
            return blockState;
        }
    }

}
