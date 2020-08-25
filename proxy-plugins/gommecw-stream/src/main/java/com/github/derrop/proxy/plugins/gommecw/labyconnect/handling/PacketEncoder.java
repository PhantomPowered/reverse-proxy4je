package com.github.derrop.proxy.plugins.gommecw.labyconnect.handling;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.network.wrapper.DefaultProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class PacketEncoder extends MessageToByteEncoder<LabyPacket> {

    protected void encode(ChannelHandlerContext channelHandlerContext, LabyPacket labyPacket, ByteBuf byteBuf) {
        ProtoBuf buffer = new DefaultProtoBuf(47, byteBuf);
        int id = LabyProtocol.getProtocol().getPacketId(labyPacket);
        if ((id != 62 && id != 63)) {
            System.err.println("[OUT] " + id + " " + labyPacket.getClass().getSimpleName());
        }

        buffer.writeVarInt(LabyProtocol.getProtocol().getPacketId(labyPacket));
        labyPacket.write(buffer);
    }
}


