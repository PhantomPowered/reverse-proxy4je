package com.github.derrop.proxy.protocol.play.server.world;

import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class PacketPlayServerBlockChange extends DefinedPacket {

    private BlockPos pos;
    private int blockState;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.blockState = readVarInt(buf);
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        buf.writeLong(this.pos.toLong());
        writeVarInt(this.blockState, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }

    @Override
    public int getId() {
        return ProtocolIds.ClientBound.Play.BLOCK_CHANGE;
    }
}
