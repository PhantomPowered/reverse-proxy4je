package com.github.derrop.proxy.protocol.status.client;

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
public class PacketStatusOutResponse extends DefinedPacket {

    private String response;

    @Override
    public void read(@NotNull ByteBuf buf) {
        response = readString(buf);
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeString(response, buf);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Status.SERVER_INFO;
    }
}
