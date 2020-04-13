package com.github.derrop.proxy.connection.cache.packet;

import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@EqualsAndHashCode(callSuper = false)
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourcePackSend extends DefinedPacket {

    private String url;
    private String hash;

    @Override
    public void read(ByteBuf buf) {
        this.url = readString(buf);
        this.hash = readString(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        writeString(this.url, buf);
        writeString(this.hash, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }
}
