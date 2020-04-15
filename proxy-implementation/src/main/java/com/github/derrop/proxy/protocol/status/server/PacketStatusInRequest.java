package com.github.derrop.proxy.protocol.status.server;

import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketStatusInRequest extends DefinedPacket {

    @Override
    public void read(@NotNull ByteBuf buf) {
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Status.START;
    }
}
