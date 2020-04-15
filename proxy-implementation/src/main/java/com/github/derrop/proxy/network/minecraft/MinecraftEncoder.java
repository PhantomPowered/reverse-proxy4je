package com.github.derrop.proxy.network.minecraft;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public final class MinecraftEncoder extends MessageToByteEncoder<Packet> {

    private final ProtocolDirection direction;

    public MinecraftEncoder(ProtocolDirection direction) {
        this.direction = direction;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) {
        ByteBufUtils.writeVarInt(packet.getId(), byteBuf);
        packet.write(byteBuf, this.direction, 47);
    }
}
