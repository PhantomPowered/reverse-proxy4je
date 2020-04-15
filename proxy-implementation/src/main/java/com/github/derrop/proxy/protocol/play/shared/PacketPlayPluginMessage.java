package com.github.derrop.proxy.protocol.play.shared;

import com.github.derrop.proxy.protocol.ProtocolIds;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketPlayPluginMessage extends DefinedPacket {

    private String tag;
    private byte[] data;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.tag = readString(buf);
        int maxSize = this.getId() == ProtocolIds.ToClient.Play.CUSTOM_PAYLOAD ? 0x100000 : Short.MAX_VALUE;
        Preconditions.checkArgument(buf.readableBytes() < maxSize);
        data = new byte[buf.readableBytes()];
        buf.readBytes(data);
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeString(tag, buf);
        buf.writeBytes(data);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    public DataInput getStream() {
        return new DataInputStream(new ByteArrayInputStream(data));
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.CUSTOM_PAYLOAD;
    }
}
