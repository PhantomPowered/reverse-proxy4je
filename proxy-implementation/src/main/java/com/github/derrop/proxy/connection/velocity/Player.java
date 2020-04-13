package com.github.derrop.proxy.connection.velocity;

import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Player extends DefinedPacket {

    private boolean onGround;

    @Override
    public void read(ByteBuf buf) {
        this.onGround = buf.readUnsignedByte() != 0;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(this.onGround ? 1 : 0);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }
}
