package com.github.derrop.proxy.protocol.handshake;

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
public class PacketHandshakingInSetProtocol extends DefinedPacket { // TODO: rename

    private int protocolVersion;
    private String host;
    private int port;
    private int requestedProtocol;

    @Override
    public void read(@NotNull ByteBuf buf) {
        protocolVersion = readVarInt(buf);
        host = readString(buf);
        port = buf.readUnsignedShort();
        requestedProtocol = readVarInt(buf);
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeVarInt(protocolVersion, buf);
        writeString(host, buf);
        buf.writeShort(port);
        writeVarInt(requestedProtocol, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public int getId() {
        return ProtocolIds.ServerBound.Handshaking.SET_PROTOCOL;
    }
}
