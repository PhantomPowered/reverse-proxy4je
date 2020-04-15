package com.github.derrop.proxy.protocol.status;

import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketStatusRequest extends DefinedPacket {

    @Override
    public void read(@NotNull ByteBuf buf) {
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public int getId() {
        return ProtocolIds.ServerBound.Status.START;
    }
}
