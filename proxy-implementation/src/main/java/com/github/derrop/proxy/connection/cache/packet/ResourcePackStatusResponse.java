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
public class ResourcePackStatusResponse extends DefinedPacket {

    private String hash;
    private Action action;

    @Override
    public void read(ByteBuf buf) {
        this.hash = readString(buf);
        this.action = Action.values()[readVarInt(buf)];
    }

    @Override
    public void write(ByteBuf buf) {
        writeString(this.hash, buf);
        writeVarInt(this.action.ordinal(), buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {

    }

    public static enum Action {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_DOWNLOAD,
        ACCEPTED,
        ;
    }

}
