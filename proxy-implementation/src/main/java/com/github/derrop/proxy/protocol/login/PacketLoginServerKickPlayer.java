package com.github.derrop.proxy.protocol.login;

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
public class PacketLoginServerKickPlayer extends DefinedPacket {

    private String message;

    @Override
    public void read(@NotNull ByteBuf buf) {
        message = readString(buf);
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeString(message, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public int getId() {
        return ProtocolIds.ClientBound.Login.DISCONNECT;
    }

}
