package com.github.derrop.proxy.protocol.play.server;

import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = false)
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacketPlayServerResourcePackSend extends DefinedPacket {

    private String url;
    private String hash;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.url = readString(buf);
        this.hash = readString(buf);
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeString(this.url, buf);
        writeString(this.hash, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }

    @Override
    public int getId() {
        return ProtocolIds.ClientBound.Play.RESOURCE_PACK_SEND;
    }
}
