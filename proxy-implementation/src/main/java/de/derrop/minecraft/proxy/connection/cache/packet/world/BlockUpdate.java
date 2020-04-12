package de.derrop.minecraft.proxy.connection.cache.packet.world;

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
public class BlockUpdate extends DefinedPacket {

    private BlockPos pos;
    private int blockState;

    @Override
    public void read(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.blockState = readVarInt(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeLong(this.pos.toLong());
        writeVarInt(this.blockState, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }
}
