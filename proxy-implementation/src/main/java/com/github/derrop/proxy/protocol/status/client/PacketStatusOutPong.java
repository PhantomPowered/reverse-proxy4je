package com.github.derrop.proxy.protocol.status.client;

import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class PacketStatusOutPong extends DefinedPacket {

    private long clientTime;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.clientTime = buf.readLong();
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        buf.writeLong(this.clientTime);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Status.PONG;
    }
}
