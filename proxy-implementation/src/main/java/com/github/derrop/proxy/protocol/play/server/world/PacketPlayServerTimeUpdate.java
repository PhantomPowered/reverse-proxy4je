package com.github.derrop.proxy.protocol.play.server.world;

import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class PacketPlayServerTimeUpdate extends DefinedPacket {

    private long totalWorldTime;
    private long worldTime;

    @Override
    public void read(ByteBuf buf) {
        this.totalWorldTime = buf.readLong();
        this.worldTime = buf.readLong();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeLong(this.totalWorldTime);
        buf.writeLong(this.worldTime);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }
}
