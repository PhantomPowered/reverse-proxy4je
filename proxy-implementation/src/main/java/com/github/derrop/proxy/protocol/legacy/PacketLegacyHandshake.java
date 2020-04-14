package com.github.derrop.proxy.protocol.legacy;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketLegacyHandshake extends DefinedPacket {

    @Override
    public void read(ByteBuf buf) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void write(ByteBuf buf) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }
}
