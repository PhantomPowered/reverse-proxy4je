package com.github.derrop.proxy.protocol.play.server;

import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketPlayServerKickPlayer extends DefinedPacket {

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
    public int getId() {
        return ProtocolIds.ToClient.Play.KICK_DISCONNECT;
    }
}
