package com.github.derrop.proxy.protocol.status.server;

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
public class PacketStatusInPing extends DefinedPacket {

    private long time;

    @Override
    public void read(@NotNull ByteBuf buf) {
        time = buf.readLong();
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        buf.writeLong(time);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Status.PING;
    }
}
