package com.github.derrop.proxy.protocol.login;

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
public class PacketPlayServerLoginSuccess extends DefinedPacket {

    private String uuid;
    private String username;

    @Override
    public void read(@NotNull ByteBuf buf) {
        uuid = readString(buf);
        username = readString(buf);
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeString(uuid, buf);
        writeString(username, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public int getId() {
        return ProtocolIds.ClientBound.Login.SUCCESS;
    }
}
