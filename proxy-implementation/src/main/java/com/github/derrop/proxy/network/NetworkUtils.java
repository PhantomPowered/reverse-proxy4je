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
package com.github.derrop.proxy.network;

import com.github.derrop.proxy.network.pipeline.length.LengthFrameEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.FastThreadLocalThread;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

public final class NetworkUtils {

    private NetworkUtils() {
        throw new UnsupportedOperationException();
    }

    static {
        if (System.getProperty("io.netty.leakDetectionLevel") == null) {
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
        }

        System.setProperty("io.netty.allocator.maxOrder", "9");
        System.setProperty("io.netty.noPreferDirect", "true");
        System.setProperty("io.netty.maxDirectMemory", "0");
        System.setProperty("io.netty.recycler.maxCapacity", "0");
        System.setProperty("io.netty.recycler.maxCapacity.default", "0");
        System.setProperty("io.netty.selectorAutoRebuildThreshold", "0");
        System.setProperty("io.netty.allocator.type", "UNPOOLED");
    }

    public static final String TIMEOUT = "timeout";
    public static final String LENGTH_DECODER = "length_decoder";
    public static final String LENGTH_ENCODER = "length_encoder";
    public static final String ENDPOINT = "endpoint";
    public static final String PACKET_DECODER = "packet_decoder";
    public static final String PACKET_ENCODER = "packet_encoder";
    public static final String DECRYPT = "decrypt";
    public static final String ENCRYPT = "encrypt";
    public static final String COMPRESSOR = "compressor";
    public static final String DE_COMPRESSOR = "de-compressor";

    public static final MessageToByteEncoder<ByteBuf> LENGTH_FRAME_ENCODER = new LengthFrameEncoder();
    public static final WriteBufferWaterMark WATER_MARK = new WriteBufferWaterMark(524288, 2097152);

    public static int varIntSize(int paramInt) {
        if ((paramInt & -128) == 0) {
            return 1;
        }

        if ((paramInt & -16_384) == 0) {
            return 2;
        }

        if ((paramInt & -2_097_152) == 0) {
            return 3;
        }

        if ((paramInt & -268_435_456) == 0) {
            return 4;
        }

        return 5;
    }

    public static EventLoopGroup newEventLoopGroup() {
        return Epoll.isAvailable()
                ? new EpollEventLoopGroup(Runtime.getRuntime().availableProcessors(), threadFactory())
                : new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), threadFactory());
    }

    public static Class<? extends SocketChannel> getSocketChannelClass() {
        return Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
        return Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    @NotNull
    public static ThreadFactory threadFactory() {
        return r -> new FastThreadLocalThread(r);
    }
}
