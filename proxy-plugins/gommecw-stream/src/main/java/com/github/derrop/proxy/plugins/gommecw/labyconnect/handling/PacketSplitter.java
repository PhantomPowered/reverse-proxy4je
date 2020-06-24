package com.github.derrop.proxy.plugins.gommecw.labyconnect.handling;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.network.wrapper.DefaultProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketSplitter
        extends MessageToByteEncoder<ByteBuf> {
    protected void encode(ChannelHandlerContext ctx, ByteBuf buffer, ByteBuf byteBuf) {
        int readable = buffer.readableBytes();
        int varIntSize = LabyBufferUtils.getVarIntSize(readable);
        if (varIntSize > 3) {
            throw new IllegalArgumentException("unable to fit " + readable + " into " + '\003');
        }
        ProtoBuf packetBuffer = new DefaultProtoBuf(47, byteBuf);
        packetBuffer.ensureWritable(varIntSize + readable);
        packetBuffer.writeVarInt(readable);
        packetBuffer.writeBytes(buffer, buffer.readerIndex(), readable);
    }
}


