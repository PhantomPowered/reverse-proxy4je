package com.github.derrop.proxy.network.length;

import com.github.derrop.proxy.api.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class LengthFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        byteBuf.markReaderIndex();

        byte[] buffer = new byte[3];
        for (int i = 0; i < buffer.length; i++) {
            if (!byteBuf.isReadable()) {
                byteBuf.resetReaderIndex();
                return;
            }

            buffer[i] = byteBuf.readByte();
            if (buffer[i] >= 0) {
                int length = ByteBufUtils.readVarInt(Unpooled.wrappedBuffer(buffer));
                if (length == 0) {
                    System.err.println("Unable to handle empty packet! Dump: " + channelHandlerContext.toString());
                    return;
                }

                if (byteBuf.readableBytes() < length) {
                    byteBuf.resetReaderIndex();
                    return;
                }

                if (byteBuf.hasMemoryAddress()) {
                    list.add(byteBuf.slice(byteBuf.readerIndex(), length).retain());
                    byteBuf.skipBytes(length);
                    return;
                }

                ByteBuf buf = channelHandlerContext.alloc().directBuffer(length);
                byteBuf.readBytes(buf);
                list.add(buf);
                break;
            }
        }
    }
}
