package com.github.derrop.proxy.network.length;

import com.github.derrop.proxy.api.util.ByteBufUtils;
import com.github.derrop.proxy.network.NetworkUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class LengthFrameEncoder extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
        int readable = byteBuf.readableBytes();
        int length = NetworkUtils.varintSize(readable);
        byteBuf2.ensureWritable(readable + length);

        ByteBufUtils.writeVarInt(readable, byteBuf2);
        byteBuf2.writeBytes(byteBuf);
    }
}
