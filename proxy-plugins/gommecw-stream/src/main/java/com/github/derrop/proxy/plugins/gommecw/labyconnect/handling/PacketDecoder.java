package com.github.derrop.proxy.plugins.gommecw.labyconnect.handling;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.network.wrapper.DefaultProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;


public class PacketDecoder extends ByteToMessageDecoder {
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> objects) throws Exception {
        ProtoBuf buffer = new DefaultProtoBuf(47, byteBuf);
        if (buffer.readableBytes() < 1)
            return;
        int id = buffer.readVarInt();

        LabyPacket labyPacket = LabyProtocol.getProtocol().getPacket(id);
        if ((id != 62 && id != 63)) {
            System.err.println("[IN] " + id + " " + labyPacket.getClass().getSimpleName());
        }
        labyPacket.read(buffer);
        if (buffer.readableBytes() > 0) {
            throw new IOException("Packet  (" + labyPacket.getClass().getSimpleName() + ") was larger than I expected, found " + buffer.readableBytes() + " bytes extra whilst reading packet " + labyPacket);
        }
        objects.add(labyPacket);
    }
}

