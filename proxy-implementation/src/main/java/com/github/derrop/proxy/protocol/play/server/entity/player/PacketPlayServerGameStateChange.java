package com.github.derrop.proxy.protocol.play.server.entity.player;

import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class PacketPlayServerGameStateChange extends DefinedPacket {

    private int state;
    private float value;

    @Override
    public void read(ByteBuf buf) {
        this.state = buf.readUnsignedByte();
        this.value = buf.readFloat();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(this.state);
        buf.writeFloat(this.value);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }
}
