package com.github.derrop.proxy.network.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.List;

public final class PacketDeCompressor extends MessageToMessageDecoder<ByteBuf> {

    private final PacketCompressionHandler packetCompressionHandler;

    public PacketDeCompressor(int threshold) {
        this.packetCompressionHandler = new PacketCompressionHandler(threshold, false);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int size = DefinedPacket.readVarInt(byteBuf);
        if (size == 0) {
            list.add(byteBuf.slice().retain());
            byteBuf.skipBytes(byteBuf.readableBytes());
            return;
        }

        ByteBuf buf = channelHandlerContext.alloc().directBuffer();
        packetCompressionHandler.process(byteBuf, buf);
        if (buf.readableBytes() != size) {
            return;
        }

        list.add(buf);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.packetCompressionHandler.end();
    }
}
