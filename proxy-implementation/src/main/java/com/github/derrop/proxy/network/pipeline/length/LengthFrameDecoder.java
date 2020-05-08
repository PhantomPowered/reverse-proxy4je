/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.network.pipeline.length;

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
