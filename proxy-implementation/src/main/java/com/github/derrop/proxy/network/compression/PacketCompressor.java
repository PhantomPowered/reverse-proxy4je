package com.github.derrop.proxy.network.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.md_5.bungee.protocol.DefinedPacket;

public final class PacketCompressor extends MessageToByteEncoder<ByteBuf> {

    private final PacketCompressionHandler packetCompressionHandler;

    public PacketCompressor(int threshold) {
        this.packetCompressionHandler = new PacketCompressionHandler(threshold, true);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
        int size = byteBuf.readableBytes();
        if (size < this.packetCompressionHandler.getThreshold()) {
            DefinedPacket.writeVarInt(0, byteBuf2);
            byteBuf2.writeBytes(byteBuf);
            return;
        }

        DefinedPacket.writeVarInt(size, byteBuf2);
        this.packetCompressionHandler.process(byteBuf, byteBuf2);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.packetCompressionHandler.end();
    }

    public void setThreshold(int threshold) {
        this.packetCompressionHandler.setThreshold(threshold);
    }
}
