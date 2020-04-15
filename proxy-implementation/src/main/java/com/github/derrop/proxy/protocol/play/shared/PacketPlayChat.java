package com.github.derrop.proxy.protocol.play.shared;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketPlayChat extends DefinedPacket {

    private String message;
    private byte position;

    public PacketPlayChat(String message) {
        this(message, (byte) 0);
    }

    @Override
    public void read(@NotNull ByteBuf buf, @NotNull ProtocolDirection direction) {
        message = readString(buf);

        if (direction == ProtocolDirection.TO_CLIENT) {
            position = buf.readByte();
        }
    }

    @Override
    public void write(@NotNull ByteBuf buf, @NotNull ProtocolDirection direction) {
        writeString(message, buf);

        if (direction == ProtocolDirection.TO_CLIENT) {
            buf.writeByte(position);
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.CHAT;
    }
}
